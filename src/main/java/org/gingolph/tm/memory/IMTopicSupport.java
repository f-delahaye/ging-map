package org.gingolph.tm.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.gingolph.tm.NameImpl;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.TopicSupport;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;


public class IMTopicSupport extends IMConstructSupport implements TopicSupport {
  private Set<Locator> subjectIdentifiers;
  private Set<Locator> subjectLocators;
  private Set<NameImpl> names;
  private Set<Topic> types;
  private Set<Occurrence> occurrences;
  private Collection<Role> roles;
  private Reifiable reified;

  public IMTopicSupport() {}

  @Override
  public void addSubjectLocator(Locator locator) {
    if (subjectLocators == null) {
      subjectLocators = new HashSet<>();
    }
    subjectLocators.add(locator);
  }

  @Override
  public void removeSubjectLocator(Locator locator) {
    if (subjectLocators != null) {
      subjectLocators.remove(locator);
    }
  }

  @Override
  public void addSubjectIdentifier(Locator identifier) {
    if (subjectIdentifiers == null) {
      subjectIdentifiers = new HashSet<>();
    }
    subjectIdentifiers.add(identifier);
  }

  @Override
  public void removeSubjectIdentifier(Locator identifier) {
    if (subjectIdentifiers != null) {
      subjectIdentifiers.remove(identifier);
    }
  }

  /**
   * @return the subjectIdentifiers
   */
  @Override
  public Set<Locator> getSubjectIdentifiers() {
    return subjectIdentifiers;
  }

  /**
   * @return the subjectLocators
   */
  @Override
  public Set<Locator> getSubjectLocators() {
    return subjectLocators;
  }

  @Override
  public void addName(NameImpl name) {
    if (names == null) {
      names = new HashSet<>();
    }
    names.add(name);
  }

  @Override
  public void removeName(NameImpl name) {
    if (names != null) {
      names.remove(name);
    }
  }

  @Override
  public Set<NameImpl> getNames() {
    return names;
  }

  @Override
  public void addOccurrence(Occurrence occurrence) {
    if (occurrences == null) {
      occurrences = new HashSet<>();
    }
    occurrences.add(occurrence);
  }

  @Override
  public void removeOccurrence(Occurrence occurrence) {
    if (occurrences != null) {
      occurrences.remove(occurrence);
    }
  }

  @Override
  public Set<Occurrence> getOccurrences() {
    return occurrences;
  }

  @Override
  public void addRolePlayed(Role role) {
    if (roles == null) {
      roles = new ArrayList<>();
    }
    roles.add(role);
  }

  @Override
  public void removeRolePlayed(Role role) {
    if (roles != null) {
      roles.remove(role);
    }
  }

  @Override
  public Set<Role> getRolesPlayed() {
    // roles cannot be a HashSet.
    // Role.id may be modified by role.importIn() and role.id is used in hashCode() which could lead
    // to inconsistent behavior of roles (as a general rule, its not safe to put mutable objects in
    // Set's)
    // In particular, one of the tests of the standard TMAPI test suite fails because of this
    // (I suspect IMAssociationSupport.getRoles() would also fail in
    // IMAssociationSupport.getRoles().contains(xxx) was tested)
    // So we store roles as a list and create a hashset off it when called.
    // For better performance, we could use Guava's immutable set instead
    // TODO if we create a new HashSet here, we no longer need to wrap result of this method in
    // Collections.unmodifiableSet
    return roles == null ? null : new HashSet<>(roles);
  }

  @Override
  public void addType(Topic type) {
    if (types == null) {
      types = new HashSet<>();
    }
    types.add(type);
  }

  @Override
  public boolean removeType(Topic type) {
    if (types != null) {
      types.remove(type);
      return true;
    }
    return false;
  }

  @Override
  public Set<Topic> getTypes() {
    return types;
  }

  @Override
  public Reifiable getReified() {
    return reified;
  }

  @Override
  public void setReified(Reifiable reified) {
    this.reified = reified;
  }

  @Override
  public void setOwner(TopicImpl owner) {
 // Noop - not needed by the in memory implementation    
  }  
}
