package org.gingolph.gingmap.support;

import org.gingolph.gingmap.AbstractConstruct;
import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.LocatorImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.TopicMapImpl;
import org.gingolph.gingmap.support.spi.TopicMapInterface;
import org.tmapi.index.Index;


public interface TopicMapSupport extends ConstructSupport, TopicMapInterface<TopicImpl, AssociationImpl> {

  /**
   * Callback method invoked by TopicMapImpl.setSupport.
   * This is the reverse relationship.
   * A topic map NEEDS a support as most of its operations are delegated to the support.
   * Conversely, in certain implementations, a support MAY need its topic map.
   * 
   * Implementations are not required to store the supplied reference if they don't need it.
   * @param owner
   */  
  void setOwner(TopicMapImpl owner);
  
  void removeAssociation(AssociationImpl association);

  <I extends Index> I getIndex(Class<I> type);

  TopicImpl getReifier();

  void setReifier(TopicImpl reifier);

  public String generateId(AbstractConstruct<?> construct);
  
  /**
   * Returns the topicmap's base locator
   * 
   * @return
   */
  public LocatorImpl getBaseLocator();

  public void setBaseLocator(LocatorImpl locator);  
}
