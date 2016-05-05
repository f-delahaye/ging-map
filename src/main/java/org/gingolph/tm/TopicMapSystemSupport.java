package org.gingolph.tm;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.TopicMap;


public interface TopicMapSystemSupport extends ConstructSupportFactory {
    
    public Object getProperty(String propertyName);
    
    public boolean getFeature(String featureName) throws FeatureNotRecognizedException;
    
    public void removeTopicMap(TopicMap topicMap);
    
    public void close(TopicMap topicMap);

    public void close();
}
