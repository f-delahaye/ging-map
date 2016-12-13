package org.gingolph.tm;

import java.util.List;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.index.Index;


public interface TopicMapSupport extends ConstructSupport {

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
  
  List<TopicImpl> getTopics();

  void addTopic(TopicImpl topic);

  void removeTopic(Topic topic);

  List<AssociationImpl> getAssociations();

  void addAssociation(AssociationImpl association);

  void removeAssociation(Association association);

  <I extends Index> I getIndex(Class<I> type);

  TopicImpl getReifier();

  void setReifier(TopicImpl reifier);

  public String generateId(AbstractConstruct<?> construct);

  Locator createLocator(String value);
  
  /**
   * Returns the topicmap's base locator
   * 
   * @return
   */
  public Locator getBaseLocator();

  public void setBaseLocator(Locator locator);  
}
