package org.gingolph.gingmap.support;

import java.util.Set;

import org.gingolph.gingmap.NameImpl;
import org.gingolph.gingmap.OccurrenceImpl;
import org.gingolph.gingmap.RoleImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.support.spi.TopicInterface;
import org.tmapi.core.Locator;
import org.tmapi.core.Reifiable;


public interface TopicSupport extends ConstructSupport, TopicInterface<TopicImpl, RoleImpl, NameImpl, OccurrenceImpl> {

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

  
  Reifiable getReified();

  void setReified(Reifiable reified);

}
