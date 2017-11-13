package org.gingolph.gingmap.memory;

import org.gingolph.gingmap.AbstractGingMapSystemFactory;
import org.gingolph.gingmap.support.TopicMapSystemSupport;

public class IMTopicMapSystemFactory extends AbstractGingMapSystemFactory {

  public IMTopicMapSystemFactory() {
    features.put(AUTOMERGE, Boolean.FALSE);
    features.put(MODEL, Boolean.FALSE);
    features.put(MERGE, Boolean.FALSE);
    features.put(NOTATION, Boolean.FALSE);
    features.put(READONLY, Boolean.FALSE);
    features.put(TYPE_INSTANCE_AS_ASSOCIATIONS, Boolean.FALSE);
  }
  

  @Override
  protected TopicMapSystemSupport getTopicMapSystemSupport() {
    return new IMTopicMapSystemSupport();
  }
  
}
