package org.gingolph.gingmap.processing;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.NameImpl;
import org.gingolph.gingmap.OccurrenceImpl;
import org.gingolph.gingmap.RoleImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.TopicMapImpl;
import org.gingolph.gingmap.VariantImpl;
import org.gingolph.gingmap.equality.Equality;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;

/**
 * Helper class to merge two topic maps.
 * 
 * The source topic map will not be modified in any way.
 * Topics from source that do not exist in dest will be created.
 * Topics from source that already exist in dest will be merged.
 * 
 * Note that all reference topics (players, types, scopes ...) for constructs imported in dest must be imported too i.e. we cannot just copy an association from source to dest, its types and scopes must be changed to topics existing in dest.
 * 
 * @author frederic
 *
 */
public class TopicMapMerger implements TopicMapVisitor{

  final TopicMapImpl dest;
  final Equality equality;
  
  Map<String, TopicImpl> sourceIdToDestTopic = new LinkedHashMap<>();
  
  // Would feel more natural being implemented as a queue that would contain for example topic -> name -> variant, 
  // but this would require notifications when a constuct is done being processed so that it can be removed from the queue.
  ThreadLocal<CurrentConstructs> currentConstruct = new ThreadLocal<CurrentConstructs>() {
    @Override
    public CurrentConstructs initialValue() {
      return new CurrentConstructs();
    }
  };

  public TopicMapMerger(TopicMapImpl dest, Equality equality) {
    this.dest = dest;
    this.equality = equality;
  }

  public void mergeIn(TopicMapImpl source) {
    source.topics().forEach(this::createDestTopic);
    new TopicMapWalker().visitTopicMap(source, this);
  }
  
  private void createDestTopic(TopicImpl sourceTopic) {
    String sourceId = sourceTopic.getId();
    Optional<TopicImpl> existingDestTopic = dest.topics().filter(topic -> equality.equals( topic, sourceTopic)).findAny();
    TopicImpl destTopic;
    if (existingDestTopic.isPresent()) {
      destTopic = existingDestTopic.get();
      destTopic.copyLocators(sourceTopic);
    } else {      
      destTopic = dest.createTopicFrom(sourceTopic);
      destTopic.getSupport().setId(sourceTopic.getId());
    }
    sourceIdToDestTopic.put(sourceId, destTopic);
  }
  
  private TopicImpl getDestTopic(Topic sourceTopic) {
    return sourceTopic == null ? null : sourceIdToDestTopic.get(sourceTopic.getId());
  }
  
  private Stream<TopicImpl> getDestTopics(Collection<Topic> sourceTopics) {
    return sourceTopics.stream().map(this::getDestTopic);
  }


  private List<Topic> getDestScope(Scoped scoped) {
    return getDestTopics(scoped.getScope()).collect(Collectors.toList());
  }
  
  @Override
  public void onTopic(TopicImpl topic) {
    TopicImpl destTopic = sourceIdToDestTopic.get(topic.getId());
    getDestTopics(topic.getTypes()).forEach(destTopic::addType);
    currentConstruct.get().topic = destTopic;
  }

  @Override
  public void onAssociation(AssociationImpl association) {
    AssociationImpl destAssociation = dest.createAssociation(getDestTopic(association.getType()), getDestScope(association));
    destAssociation.getSupport().setId(association.getId());
    currentConstruct.get().association = destAssociation;
  }

  @Override
  public void onRole(RoleImpl role) {
    AssociationImpl destAssociation = currentConstruct.get().association;
    destAssociation.createRole(getDestTopic(role.getType()), getDestTopic(role.getPlayer()));
  }

  @Override
  public void onName(NameImpl sourceName) {
    TopicImpl destTopic = currentConstruct.get().topic;
    NameImpl destName = destTopic.names().filter(name -> equality.equals( name, sourceName)).findAny().orElseGet(() -> createDestName(sourceName, destTopic));
    currentConstruct.get().name = destName;
  }

  private NameImpl createDestName(NameImpl sourceName, TopicImpl destTopic) {
    return destTopic.createName(getDestTopic(sourceName.getType()), sourceName.getValue(), getDestScope(sourceName));
  }
  
  @Override
  public void onVariant(VariantImpl sourceVariant) {
    NameImpl destName = currentConstruct.get().name;
    if (destName.variants().noneMatch(variant -> equality.equals(variant, sourceVariant))) {
      createDestVariant(sourceVariant, destName);
    }
  }

  private VariantImpl createDestVariant(VariantImpl sourceVariant, NameImpl destName) {
    return destName.createVariant(sourceVariant.getValue(), sourceVariant.getDatatype(), getDestScope(sourceVariant));
  }

  @Override
  public void onOccurrence(OccurrenceImpl sourceOccurrence) {
    TopicImpl destTopic = currentConstruct.get().topic;
    if (destTopic.occurrences().noneMatch(occurrence -> equality.equals(occurrence, sourceOccurrence))) {
      createDestOccurrence(sourceOccurrence, destTopic);
    }
  }

  private OccurrenceImpl createDestOccurrence(OccurrenceImpl occurrence, TopicImpl destTopic) {
    return destTopic.createOccurrence(getDestTopic(occurrence.getType()), occurrence.getValue(), getDestScope(occurrence));
  }
  
  private static class CurrentConstructs {
    TopicImpl topic;
    AssociationImpl association;
    NameImpl  name;
  }
  
}
