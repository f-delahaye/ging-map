package org.gingolph.tm;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;


public class TopicMapSystemImpl implements TopicMapSystem, Serializable {

    private final Map<Locator, TopicMap> topicMaps = new LinkedHashMap<>();
    private final TopicMapSystemSupport support;

    public TopicMapSystemImpl(TopicMapSystemSupport support) {
        this.support = support;
    }
    
    @Override
    public TopicMap getTopicMap(String locator) {
        return getTopicMap(createLocator(locator));
    }

    @Override
    public TopicMap getTopicMap(Locator locator) {
        return topicMaps.get(locator);
    }

    @Override
    public Set<Locator> getLocators() {
        return Collections.unmodifiableSet(topicMaps.keySet());
    }

    @Override
    public Locator createLocator(String locator) throws MalformedIRIException {
        return new LocatorImpl(locator);
    }

    @Override
    public TopicMap createTopicMap(Locator locator) throws TopicMapExistsException {
        if (topicMaps.containsKey(locator)) {
            throw new TopicMapExistsException("A topic map with locator "+locator+" already exists");
        }
        TopicMapImpl topicMap = new TopicMapImpl(this, isAutoMerge(), support);
        topicMap.setSupport(support.createTopicMapSupport(topicMap));
        topicMap.setLocator(locator);
        topicMaps.put(locator, topicMap);
        return topicMap;
    }

    @Override
    public TopicMap createTopicMap(String locator) throws TopicMapExistsException {
        return createTopicMap(createLocator(locator));
    }

    void removeTopicMap(TopicMapImpl topicMap) {
        this.topicMaps.remove(topicMap.getLocator());
        support.removeTopicMap(topicMap);
    }

    void closeTopicMap(TopicMap topicMap) {
        support.close(topicMap);
    }
    
    @Override
    public boolean getFeature(String featureName) throws FeatureNotRecognizedException {
        Boolean feature = support.getFeature(featureName);
        if (feature == null) {
            throw new FeatureNotRecognizedException(featureName + " not supported");
        }
        return feature;
    }
    
    public boolean isAutoMerge()  {
        try {
            return getFeature(AbstractTopicMapSystemFactory.AUTOMERGE);
        } catch (FeatureNotRecognizedException ex) {
            throw new TMAPIRuntimeException("As per specification, the automerge feature should be supported");
        }
    }

    @Override
    public Object getProperty(String propertyName) {
        return support.getProperty(propertyName);
    }

    @Override
    public void close() {
        support.close();
    }
}
