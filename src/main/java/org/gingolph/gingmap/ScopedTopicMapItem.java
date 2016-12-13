package org.gingolph.gingmap;

import org.tmapi.core.Construct;
import org.tmapi.core.Scoped;

public abstract class ScopedTopicMapItem<P extends Construct, S extends ConstructSupport> extends TopicMapItem<P, S> implements Scoped {

  public ScopedTopicMapItem(TopicMapImpl topicMap, P parent) {
    super(topicMap, parent);
  }

}
