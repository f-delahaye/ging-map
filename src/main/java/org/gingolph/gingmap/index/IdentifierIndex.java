package org.gingolph.gingmap.index;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.TopicMapImpl;
import org.gingolph.gingmap.event.TopicMapEventListener;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.index.Index;

/**
 * Not directly exposed as an index by tmapi, but exposed through TopicMap.getBy*Identifiers()
 * methods.
 */
public class IdentifierIndex extends AbstractIndex implements Index, TopicMapEventListener {

  Map<String, Construct> constructsByIds = new LinkedHashMap<>();
  Map<Locator, Topic> topicsBySubjectIdentifiers = new LinkedHashMap<>();
  Map<Locator, Topic> topicsBySubjectLocators = new LinkedHashMap<>();
  Map<Locator, Construct> constructsByItemIdentifiers = new LinkedHashMap<>();

  public IdentifierIndex(TopicMapImpl topicMap, Collection<TopicImpl> topics,
      Collection<AssociationImpl> associations) {
    super(topicMap.getEquality());
    onConstructCreated(topicMap);
    topics.forEach(topic -> {
      onConstructCreated(topic);
      topic.getOccurrences().forEach(occurrence -> onConstructCreated(occurrence));
      topic.getNames().forEach(name -> {
        onConstructCreated(name);
        name.getVariants().forEach(variant -> onConstructCreated(variant));
      });
      topic.getSubjectLocators().forEach(locator -> onSubjectLocatorAdded(topic, locator));
      topic.getSubjectIdentifiers()
          .forEach(identifier -> onSubjectIdentifierAdded(topic, identifier));
    });
    associations.forEach(association -> {
      onConstructCreated(association);
      association.getRoles().forEach(role -> {
        onConstructCreated(role);
      });
    });
  }

  @Override
  protected void doClose() {}

  public Construct getConstructById(String id) {
    return constructsByIds.get(id);
  }

  public Construct getConstructByItemIdentifier(Locator itemIdentifier) {
    return constructsByItemIdentifiers.get(itemIdentifier);
  }

  public Topic getTopicBySubjectIdentifier(Locator subjectIdentifier) {
    return topicsBySubjectIdentifiers.get(subjectIdentifier);
  }

  public Topic getTopicBySubjectLocator(Locator subjectLocator) {
    return topicsBySubjectLocators.get(subjectLocator);
  }

  @Override
  public final void onConstructCreated(Construct construct) {
    constructsByIds.put(construct.getId(), construct);
    construct.getItemIdentifiers()
        .forEach(identifier -> onItemIdentifierAdded(construct, identifier));
  }

  @Override
  public final void onConstructRemoved(Construct construct) {
    constructsByIds.remove(construct.getId());
  }

  @Override
  public final void onSubjectIdentifierAdded(Topic topic, Locator subjectIdentifier) {
    topicsBySubjectIdentifiers.put(subjectIdentifier, topic);
  }

  @Override
  public final void onSubjectIdentifierRemoved(Locator subjectIdentifier) {
    topicsBySubjectIdentifiers.remove(subjectIdentifier);
  }


  @Override
  public final void onSubjectLocatorAdded(Topic topic, Locator locator) {
    topicsBySubjectLocators.put(locator, topic);
  }

  @Override
  public final void onSubjectLocatorRemoved(Locator subjectLocator) {
    topicsBySubjectLocators.remove(subjectLocator);
  }

  @Override
  public final void onItemIdentifierAdded(Construct construct, Locator itemIdentifier) {
    constructsByItemIdentifiers.put(itemIdentifier, construct);
  }

  @Override
  public final void onItemIdentifierRemoved(Locator itemIdentifier) {
    constructsByItemIdentifiers.remove(itemIdentifier);
  }
}
