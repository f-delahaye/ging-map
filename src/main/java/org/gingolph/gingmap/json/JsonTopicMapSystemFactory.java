package org.gingolph.gingmap.json;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.gingolph.gingmap.AbstractTopicMapSystemFactory;
import org.gingolph.gingmap.AssociationSupport;
import org.gingolph.gingmap.LocatorImpl;
import org.gingolph.gingmap.NameSupport;
import org.gingolph.gingmap.OccurrenceSupport;
import org.gingolph.gingmap.RoleSupport;
import org.gingolph.gingmap.TopicMapImpl;
import org.gingolph.gingmap.TopicMapSupport;
import org.gingolph.gingmap.TopicMapSystemSupport;
import org.gingolph.gingmap.TopicSupport;
import org.gingolph.gingmap.VariantSupport;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;

public class JsonTopicMapSystemFactory extends AbstractTopicMapSystemFactory
implements TopicMapSystemSupport, Serializable {

  private static final long serialVersionUID = 1L;
  
  private final Map<LocatorImpl, TopicMap> topicMaps = new LinkedHashMap<>();  
  
  public JsonTopicMapSystemFactory() {
    features.put(AUTOMERGE, Boolean.FALSE);
    features.put(MODEL, Boolean.FALSE);
    features.put(MERGE, Boolean.FALSE);
    features.put(NOTATION, Boolean.FALSE);
    features.put(READONLY, Boolean.FALSE);
    features.put(TYPE_INSTANCE_AS_ASSOCIATIONS, Boolean.FALSE);    
  }
  
  @Override
  public AssociationSupport createAssociationSupport() {
    return new TopicMapSupportJson();
  }

  @Override
  public NameSupport createNameSupport() {
    return new TopicMapSupportJson();
  }

  @Override
  public OccurrenceSupport createOccurrenceSupport() {
    return new TopicMapSupportJson();
  }

  @Override
  public RoleSupport createRoleSupport() {
    return new TopicMapSupportJson();
  }

  @Override
  public VariantSupport createVariantSupport() {
    return new TopicMapSupportJson();
  }

  @Override
  public TopicMapSupport createTopicMapSupport() {
    return new TopicMapSupportJson();
  }

  @Override
  public TopicSupport createTopicSupport() {
    return new TopicMapSupportJson();
  }

  @Override
  public Locator createLocator(String value) {
    return new LocatorImpl(value);
  }

  @Override
  public void close(TopicMap topicMap) {
  }

  @Override
  public void close() {
  }

  @Override
  protected TopicMapSystemSupport getTopicMapSystemSupport() {
    return this;
  }
  
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

  @Override
  public void removeTopicMap(TopicMap topicMap) {
    topicMaps.remove(topicMap.getLocator());
  }    
}
