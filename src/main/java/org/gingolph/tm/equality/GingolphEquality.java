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

public class GingolphEquality implements Equality {

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
                .anyMatch(identifier -> itemIdentifiers.contains(identifier)));  }

  @Override
  public boolean equals(AssociationImpl association1, AssociationImpl association2) {
    return association1.getType().equals(association2.getType()) && association1.getScope().equals(association2.getScope())
        && equalsAssociationRoles(association1.getRoles(), association2.getRoles());
  }

  protected boolean equalsAssociationRoles(Collection<Role> roles, Collection<Role> otherRoles) {
    return roles.stream().noneMatch(
        (role) -> (!otherRoles.stream().anyMatch((otherRole) -> equalsAssociationRoles(role, otherRole))));
  }  

// Whether 2 roles within an association are equals.
// Since we know there are in the same association, we don't compare their respective parents so as to avoid recursive loop.
  private boolean equalsAssociationRoles(Role role, Role otherRole) {
    return role.getType().equals(otherRole.getType())
        && role.getPlayer().equals(otherRole.getPlayer());
  }
  
  @Override
  public boolean equals(RoleImpl role1, RoleImpl role2) {
    // According to the TopicMap specs, roles are deemed equals if they have the same player, type and
    // parent.
    // But I can't see how TestTopic.testRoleAssociationFilter can work with that (role1 and role2
    // have the same player, type and parent so getRolesPlayed(), which returns a set, should haved 1
    // item, not 2)
      return role1 == role2 || role1.getId().equals(role2.getId());  
  }

  @Override
  public boolean equals(NameImpl name1, NameImpl name2) {
      return name1.getValue().equals(name2.getValue()) && name1.getType().equals(name2.getType())
          && name1.getParent().equals(name2.getParent()) && name1.getScope().equals(name2.getScope());
  }

  @Override
  public boolean equals(OccurrenceImpl occurrence1, OccurrenceImpl occurrence2) {
    return occurrence1.getValue().equals(occurrence2.getValue()) && occurrence1.getDatatype().equals(occurrence2.getDatatype())
        && occurrence1.getType().equals(occurrence2.getType()) && occurrence1.getParent().equals(occurrence2.getParent())
        && occurrence1.getScope().equals(occurrence2.getScope());
  }

  @Override
  public boolean equals(VariantImpl variant1, VariantImpl variant2) {
    // TODO Auto-generated method stub
    return false;
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
  public <T> Set<T> newSet() {
    // TODO Auto-generated method stub
    return null;
  }
}
