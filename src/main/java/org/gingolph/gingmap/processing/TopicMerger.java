package org.gingolph.gingmap.processing;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.NameImpl;
import org.gingolph.gingmap.OccurrenceImpl;
import org.gingolph.gingmap.RoleImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.VariantImpl;
import org.gingolph.gingmap.equality.SAMEquality;
import org.tmapi.core.Topic;

/**
 * Helper class to merge two topics from the same topic map.
 * @author frederic
 *
 */
public class TopicMerger implements TopicVisitor{

  private TopicImpl dest;
  private SAMEquality equality;
  
  private NameImpl currentName;

  public TopicMerger(TopicImpl dest, SAMEquality equality) {
    this.dest = dest;
    this.equality = equality;
  }

  public void mergeIn(TopicImpl source) {
    // copying the locators at the beginning of the process will ensure that from now on, source and dest will be deemed equals (as long as the supplied equality is SAMEqualiy).
    dest.copyLocators(source);
    Set<Topic> destTypes = dest.getTypes();
    source.getTypes().stream().filter(sourceType -> !destTypes.contains(sourceType)).forEach(dest::addType);
    new TopicWalker().visitTopic(source, this);
    
    List<RoleImpl> roles = source.rolesPlayed().collect(Collectors.toList());
    // remove the roles first otherwise topic.remove will fail
    roles.forEach(RoleImpl::remove);
    source.remove();
    source.setSupport(dest.getSupport());
  }

  
  @Override
  public void onName(NameImpl sourceName) {
    NameImpl destName = dest.names().filter(name -> equality.equals( name, sourceName)).findAny().orElseGet(() -> createDestName(sourceName, dest));
    sourceName.getItemIdentifiers().forEach(destName::importItemIdentifier);
    if (destName.getReifier() == null) {
      destName.setReifier(sourceName.getReifier());
    } else if (sourceName.getReifier() != null) {
      TopicImpl reifier = sourceName.getReifier();
      sourceName.setReifier(null);
      new TopicMerger(destName.getReifier(), equality).mergeIn(reifier);
    }
    
    currentName = destName;
  }

  private NameImpl createDestName(NameImpl sourceName, TopicImpl destTopic) {
    return destTopic.createName(sourceName.getType(), sourceName.getValue(), sourceName.getScope());
  }
  
  @Override
  public void onVariant(VariantImpl sourceVariant) {
    if (currentName.variants().noneMatch(variant -> equality.equals(variant, sourceVariant))) {
      currentName.createVariant(sourceVariant.getValue(), sourceVariant.getDatatype(), sourceVariant.getScope());
    }
  }

  @Override
  public void onOccurrence(OccurrenceImpl sourceOccurrence) {
    OccurrenceImpl destOccurrence = dest.occurrences().filter(occ -> equality.equals(occ, sourceOccurrence)).findAny().orElseGet(() -> createDestOccurrence(sourceOccurrence));
    sourceOccurrence.getItemIdentifiers().forEach(destOccurrence::importItemIdentifier);    
  }
  
  private OccurrenceImpl createDestOccurrence(OccurrenceImpl sourceOccurrence) {
    return dest.createOccurrence(sourceOccurrence.getType(), sourceOccurrence.getValue(), sourceOccurrence.getScope());    
  }
  
  @Override
  public void onRolePlayed(RoleImpl sourceRole) {
    AssociationImpl sourceAssociation = sourceRole.getParent();
    
    if (dest.rolesPlayed().map(role -> role.getParent()).noneMatch(candidateAssociation -> equality.associationEquals(candidateAssociation, sourceAssociation, false))) {
      AssociationImpl association = dest.getTopicMap().createAssociation(sourceAssociation.getType(), sourceAssociation.getScope());
      RoleImpl role = association.createRole(sourceRole.getType(), dest);
      // This along with the hack in AbstractConstruct.getId should ensure TestTopicMerge.testRolePlaying will pass.
      role.getSupport().setId(sourceRole.getId());
    }
    
//    Optional<AssociationImpl> equivalentAssociation = dest.rolesPlayed().map(role -> role.getParent()).filter(candidateAssociation -> equality.associationEquals(candidateAssociation, sourceAssociation, false)).findAny();
//    if (equivalentAssociation.isPresent()) {
//      sourceRole.getTopicMap().removeAssociation(sourceAssociation);
//    } else {
//      sourceRole.setPlayer(dest);
//    }
    
  }


  
}
