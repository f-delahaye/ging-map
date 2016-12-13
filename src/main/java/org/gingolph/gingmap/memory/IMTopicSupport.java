package org.gingolph.gingmap.memory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gingolph.gingmap.NameImpl;
import org.gingolph.gingmap.OccurrenceImpl;
import org.gingolph.gingmap.RoleImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.TopicSupport;
import org.gingolph.gingmap.equality.Equality;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;


public class IMTopicSupport extends IMConstructSupport implements TopicSupport {
  private Set<Locator> subjectIdentifiers;
  private Set<Locator> subjectLocators;
  private List<NameImpl> names;
  private List<OccurrenceImpl> occurrences;
  private List<RoleImpl> roles;
  private Set<TopicImpl> types;
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
      names = new ArrayList<>();
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
  public List<NameImpl> getNames() {
    return names;
  }

  @Override
  public void addOccurrence(OccurrenceImpl occurrence) {
    if (occurrences == null) {
      occurrences = new ArrayList<>();
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
  public List<OccurrenceImpl> getOccurrences() {
    return occurrences;
  }

  @Override
  public void addRolePlayed(RoleImpl role) {
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
  public List<RoleImpl> getRolesPlayed() {
    return roles;
  }

  @Override
  public void addType(TopicImpl type, Equality equality) {
    if (types == null) {
      types = equality.newSet();
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
  public Set<TopicImpl> getTypes() {
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
