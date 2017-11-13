package org.gingolph.gingmap;

import java.util.Collections;
import java.util.Set;

import org.gingolph.gingmap.support.TopicMapSupport;
import org.gingolph.gingmap.support.TopicMapSystemSupport;
import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

public class GingMapSystemImpl implements TopicMapSystem {

  private final TopicMapSystemSupport support;
  private TopicMapSystemFactory factory;

  public GingMapSystemImpl(TopicMapSystemFactory factory, TopicMapSystemSupport support) {
    this.factory = factory;
    this.support = support;
  }

  @Override
  public TopicMap getTopicMap(String locator) {
    return getTopicMap(createLocator(locator));
  }

  @Override
  public TopicMap getTopicMap(Locator locator) {
    return support.getTopicMap(locator);
  }

  @Override
  public Set<Locator> getLocators() {
    return Collections.unmodifiableSet(support.getLocators());
  }

  @Override
  public Locator createLocator(String reference) throws MalformedIRIException {
    if (reference == null) {
      throw new IllegalArgumentException("null reference not allowed");
    }
    return support.createLocator(reference);
  }

  @Override
  public TopicMap createTopicMap(Locator locator) throws TopicMapExistsException {
    if (getTopicMap(locator) != null) {
      throw new TopicMapExistsException("A topic map with locator " + locator + " already exists");
    }
    TopicMapSupport topicMapSupport = support.createTopicMapSupport();
    topicMapSupport.setBaseLocator((LocatorImpl)locator);
    TopicMapImpl topicMap = createTopicMap(topicMapSupport);
    return topicMap;
  }

  public TopicMapImpl createTopicMap(TopicMapSupport topicMapSupport) {
    TopicMapImpl topicMap = new TopicMapImpl(this, isAutoMerge(), support);
    topicMap.setSupport(topicMapSupport);
    support.addTopicMap(topicMap);
    return topicMap;
  }

  @Override
  public TopicMap createTopicMap(String locator) throws TopicMapExistsException {
    return createTopicMap(createLocator(locator));
  }

  void removeTopicMap(TopicMapImpl topicMap) {
    support.removeTopicMap(topicMap);
  }

  void closeTopicMap(TopicMap topicMap) {
    support.close(topicMap);
  }

  @Override
  public boolean getFeature(String featureName) throws FeatureNotRecognizedException {
    return factory.getFeature(featureName);
  }

  public boolean isAutoMerge() {
    try {
      return getFeature(AbstractGingMapSystemFactory.AUTOMERGE);
    } catch (FeatureNotRecognizedException ex) {
      throw new TMAPIRuntimeException(
          "As per specification, the automerge feature should be supported");
    }
  }

  @Override
  public Object getProperty(String propertyName) {
    return factory.getProperty(propertyName);
  }

  @SuppressWarnings("unchecked")
  public <T> T getProperty(String propertyName, T defaultValue) {
    Object property = getProperty(propertyName);
    return property == null ? defaultValue : (T) property;
  }

  @Override
  public void close() {
    support.close();
  }
}
