package org.gingolph.gingmap.memory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.gingolph.gingmap.LocatorImpl;
import org.gingolph.gingmap.TopicMapImpl;
import org.gingolph.gingmap.support.AssociationSupport;
import org.gingolph.gingmap.support.NameSupport;
import org.gingolph.gingmap.support.OccurrenceSupport;
import org.gingolph.gingmap.support.RoleSupport;
import org.gingolph.gingmap.support.TopicMapSupport;
import org.gingolph.gingmap.support.TopicMapSystemSupport;
import org.gingolph.gingmap.support.TopicSupport;
import org.gingolph.gingmap.support.VariantSupport;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;

public class IMTopicMapSystemSupport implements TopicMapSystemSupport {

  private final Map<LocatorImpl, TopicMap> topicMaps = new LinkedHashMap<>();

  @Override
  public NameSupport createNameSupport() {
    return new IMNameSupport();
  }

  @Override
  public AssociationSupport createAssociationSupport() {
    return new IMAssociationSupport();
  }

  @Override
  public OccurrenceSupport createOccurrenceSupport() {
    return new IMOccurrenceSupport();
  }

  @Override
  public VariantSupport createVariantSupport() {
    return new IMVariantSupport();
  }

  @Override
  public RoleSupport createRoleSupport() {
    return new IMRoleSupport();
  }

  @Override
  public TopicMapSupport createTopicMapSupport() {
    return new IMTopicMapSupport();
  }

  @Override
  public TopicSupport createTopicSupport() {
    return new IMTopicSupport();
  }

  @Override
  public Locator createLocator(String value) {
    return new LocatorImpl(value);
  }

  @Override
  public void removeTopicMap(TopicMap topicMap) {
    this.topicMaps.remove(topicMap.getLocator());
  }

  @Override
  public void close(TopicMap topicMap) { // NoOp
  }

  @Override
  public void close() {}

  @Override
  public Set<LocatorImpl> getLocators() {
    return topicMaps.keySet();
  }

  @Override
  public TopicMap getTopicMap(Locator locator) {
    return topicMaps.get(locator);
  }

  @Override
  public void addTopicMap(TopicMapImpl topicMap) {
    topicMaps.put(topicMap.getLocator(), topicMap);
  }

}
