package org.gingolph.tm.equality;

import java.util.Collection;
import java.util.Set;

import org.gingolph.tm.AssociationImpl;
import org.gingolph.tm.NameImpl;
import org.gingolph.tm.OccurrenceImpl;
import org.gingolph.tm.RoleImpl;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.VariantImpl;
import org.tmapi.core.Locator;
import org.tmapi.core.Role;

public class SAMEquality implements Equality {

  @Override
  public boolean equals(TopicImpl topic1, TopicImpl topic2) {
    if (topic2.getId().equals(topic1.getId())) {
      return true;
    }
    // Optimization:
    // If automerge, 2 topics which are equals would have been merged and the above test would have
    // returned true
    // commented out: what if equals is called when trying to merge?? 2 Topics may be equals but may
    // not have been merged yet ...
    // if (isAutoMerge(getTopicMap())) {
    // return false;
    // }
    final Collection<Locator> subjectIdentifiers = topic1.getSupport().getSubjectIdentifiers();
    final Collection<Locator> subjectLocators = topic1.getSupport().getSubjectLocators();
    final Collection<Locator> itemIdentifiers = topic1.getSupport().getItemIdentifiers();

    return subjectIdentifiers != null
        && subjectIdentifiers.stream()
            .anyMatch(identifier -> topic2.getSubjectIdentifiers().contains(identifier)
                || topic2.getItemIdentifiers().contains(identifier))
        || subjectLocators != null && subjectLocators.stream()
            .anyMatch(identifier -> topic2.getSubjectLocators().contains(identifier))
        || itemIdentifiers != null && (itemIdentifiers.stream()
            .anyMatch(identifier -> topic2.getItemIdentifiers().contains(identifier))
            || topic2.getSubjectIdentifiers().stream()
                .anyMatch(identifier -> itemIdentifiers.contains(identifier)));
  }


  @Override
  public boolean equals(AssociationImpl association1, AssociationImpl association2) {
    return association1.getType().equals(association2.getType()) && association1.getScope().equals(association2.getScope()) && association1.getRoles().equals(association2.getRoles()); 
  }

  @Override
  public boolean equals(RoleImpl role1, RoleImpl role2) {
    return role1.getParent().equals(role2.getParent()) && equalsNoParent(role1, role2);
  }

  // specific method to be called when we know for sure (or don't care that) other.parent = this.parent  
  public static boolean equalsNoParent(Role role, Role otherRole) {
    return role.getPlayer().equals(otherRole.getPlayer()) && role.getType().equals(otherRole.getType());    
  }

  @Override
  public boolean equals(NameImpl name1, NameImpl name2) {
    return equalsNoParent(name1, name2) && name1.getParent().equals(name2.getParent());
  }

  // specific method to be called when we know for sure (or don't care that) other.parent = this.parent
  protected boolean equalsNoParent(NameImpl name1, NameImpl name2) {
    return name1.getValue().equals(name2.getValue()) && name1.getType().equals(name2.getType())
        && name1.getScope().equals(name2.getScope());    
  }

  @Override
  public boolean equals(OccurrenceImpl occurrence1, OccurrenceImpl occurrence2) {
    return equalsNoParent(occurrence1, occurrence2) && occurrence1.getParent().equals(occurrence2.getParent());
  }
  
  protected boolean equalsNoParent(OccurrenceImpl occurrence1, OccurrenceImpl occurrence2) {
    return occurrence1.getValue().equals(occurrence2.getValue()) && occurrence1.getDatatype().equals(occurrence2.getDatatype())
        && occurrence1.getType().equals(occurrence2.getType()) && occurrence1.getScope().equals(occurrence2.getScope());
  }


  @Override
  public boolean equals(VariantImpl variant1, VariantImpl variant2) {
    return equalsNoParent(variant1, variant2) && variant1.getParent().equals(variant2.getParent());
  }
  
  protected boolean equalsNoParent(VariantImpl variant1, VariantImpl variant2) {
    return variant1.getValue().equals(variant2.getValue()) && variant1.getDatatype().equals(variant2.getDatatype())
        && variant1.getScope().equals(variant2.getScope());
  }

  @Override
  public int hashCode(TopicImpl topic) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hashCode(AssociationImpl association) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hashCode(RoleImpl role) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hashCode(NameImpl name) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hashCode(OccurrenceImpl occurrence) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hashCode(VariantImpl variant) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Set<TopicImpl> newTopicSet() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<AssociationImpl> newAssociationSet() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<RoleImpl> newRoleSet() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<NameImpl> newNameSet() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<OccurrenceImpl> newOccurrenceSet() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<VariantImpl> newVariantSet() {
    // TODO Auto-generated method stub
    return null;
  }

 
}
