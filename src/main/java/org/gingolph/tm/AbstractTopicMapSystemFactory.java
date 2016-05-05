package org.gingolph.tm;

import java.util.LinkedHashMap;
import java.util.Map;
import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;


public abstract class AbstractTopicMapSystemFactory extends TopicMapSystemFactory {

    protected final Map<String, Object> properties = new LinkedHashMap<>();
    protected final Map<String, Boolean> features = new LinkedHashMap<>();

    private static final String FEATURE_BASE_URL = "http://tmapi.org/features/";
    
    // True if type instances are modelized as associations.
    public static final String TYPE_INSTANCE_AS_ASSOCIATIONS = FEATURE_BASE_URL+"type-instance-associations";
    public static final String AUTOMERGE = FEATURE_BASE_URL+"automerge";
    public static final String MODEL = FEATURE_BASE_URL+"model"; // - Topic Maps Model Features
    public static final String MERGE = FEATURE_BASE_URL+"merge"; // - Merging Support Features
    public static final String NOTATION = FEATURE_BASE_URL+"notation"; // - Locator Address Notation Features
    public static final String READONLY = FEATURE_BASE_URL+"readOnly"; // - Read-only System

    public AbstractTopicMapSystemFactory() {
    }
    
    @Override
    public boolean hasFeature(String featureName) {
        return features.containsKey(featureName);
    }

    @Override
    public boolean getFeature(String featureName) throws FeatureNotRecognizedException {
        Boolean feature = features.get(featureName);
        if (feature == null) {
            throw new FeatureNotRecognizedException(featureName+" not supported");
        }
        return feature;
    }

    @Override
    public void setFeature(String featureName, boolean value) throws FeatureNotSupportedException, FeatureNotRecognizedException {
        features.put(featureName, value);
    }

    @Override
    public Object getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        properties.put(propertyName, value);
    }

    @Override
    public TopicMapSystem newTopicMapSystem() throws TMAPIException {
        return new TopicMapSystemImpl(getTopicMapSystemSupport());
    }
    
    protected abstract TopicMapSystemSupport getTopicMapSystemSupport();
}
