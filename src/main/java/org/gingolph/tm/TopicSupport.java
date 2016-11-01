package org.gingolph.tm;

import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;


public interface TopicSupport extends ConstructSupport {

  /**
   * Callback method invoked by TopicImpl.setSupport.
   * This is the reverse relationship.
   * A Topic NEEDS a support as most of its operations are delegated to the support.
   * Conversely, in certain implementations, a support MAY need its associated topic.
   * 
   * Implementations are not required to store the supplied reference if they don't need it.
   * @param topic
   */
  void setOwner(TopicImpl topic);
  
  void addSubjectIdentifier(Locator identifier);

  Set<Locator> getSubjectIdentifiers();

  void removeSubjectIdentifier(Locator identifier);


  void addSubjectLocator(Locator locator);

  Set<Locator> getSubjectLocators();

  void removeSubjectLocator(Locator locator);


  void addName(NameImpl name);

  Set<NameImpl> getNames();

  void removeName(NameImpl name);


  void addOccurrence(Occurrence occurrence);

  Set<Occurrence> getOccurrences();

  void removeOccurrence(Occurrence occurrence);


  void addRolePlayed(Role role);

  Set<Role> getRolesPlayed();

  void removeRolePlayed(Role role);


  void addType(Topic type);

  Set<Topic> getTypes();

  boolean removeType(Topic type);


  Reifiable getReified();

  void setReified(Reifiable reified);

}
