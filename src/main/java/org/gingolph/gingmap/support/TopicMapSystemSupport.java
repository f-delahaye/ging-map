package org.gingolph.gingmap.support;

import java.util.Set;

import org.gingolph.gingmap.LocatorImpl;
import org.gingolph.gingmap.TopicMapImpl;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;


public interface TopicMapSystemSupport extends ConstructSupportFactory {

  public Locator createLocator(String reference);
  
  public Set<LocatorImpl> getLocators();
  
  public TopicMap getTopicMap(Locator locator);
  
  public void addTopicMap(TopicMapImpl topicMap);
  
  public void removeTopicMap(TopicMap topicMap);

  public void close(TopicMap topicMap);

  public void close();

}
