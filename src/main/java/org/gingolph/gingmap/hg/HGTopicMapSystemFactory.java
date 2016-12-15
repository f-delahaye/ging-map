package org.gingolph.gingmap.hg;

import org.gingolph.gingmap.AbstractTopicMapSystemFactory;
import org.gingolph.gingmap.TopicMapSystemSupport;


public class HGTopicMapSystemFactory extends AbstractTopicMapSystemFactory {

  protected static final String HG_BASE_URL = GINGOLPH_BASE_URL + "hg.";
  protected static final String HG_STORAGE_BASE_PROPERTY = HG_BASE_URL + "storage.";
  public static final String HG_STORAGE_PATH_PROPERTY = HG_STORAGE_BASE_PROPERTY + "path";
  // A String that indicates whether the hypergraphdb instance should be closed or kept open when HGTopicMapSystemFactory.close() is called.
  // If the property is "false", then the hypergraphdb will be left open, however, HG automatically registers a shutdown hook that will close it when the JVM terminates.
  // If you have control over when HGTopicMapSystemFactory.close() is called then this flag should not be set (or set to true).
  public static final String HG_STORAGE_CLOSE_ON_EXIT_PROPERTY = HG_STORAGE_BASE_PROPERTY + "close-on-exit";
  
  public HGTopicMapSystemFactory() {
    features.put(AUTOMERGE, Boolean.FALSE);
    features.put(MODEL, Boolean.FALSE);
    features.put(MERGE, Boolean.FALSE);
    features.put(NOTATION, Boolean.FALSE);
    features.put(READONLY, Boolean.FALSE);
    features.put(TYPE_INSTANCE_AS_ASSOCIATIONS, Boolean.FALSE);
    
    setProperty(HG_STORAGE_CLOSE_ON_EXIT_PROPERTY, "true");
  }
  
  public HGTopicMapSystemFactory withStoragePath(String storagePath) {
    setProperty(HG_STORAGE_PATH_PROPERTY, storagePath);
    return this;
  }

  public HGTopicMapSystemFactory withCloseOnExit(boolean closeOnExit) {
    setProperty(HG_STORAGE_CLOSE_ON_EXIT_PROPERTY, Boolean.toString(closeOnExit));
    return this;
  }
  
  @Override
  protected TopicMapSystemSupport getTopicMapSystemSupport() {
    return new HGTopicMapSystemSupport(getStoragePath(), Boolean.valueOf((String)getProperty(HG_STORAGE_CLOSE_ON_EXIT_PROPERTY)));
  }
  
  protected  String getStoragePath() {
    // final String path =
    // Integer.toString(locator.hashCode())+"/"+locator.getReference().replaceAll("[^A-Za-z0-9]",
    // "");
    // return path;
    return (String) getProperty(HG_STORAGE_PATH_PROPERTY);
  }

}
