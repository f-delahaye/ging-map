package org.gingolph.gingmap;

import org.tmapi.core.Construct;

public abstract class TopicMapItem<P extends Construct, S extends ConstructSupport>
    extends AbstractConstruct<S> {
  TopicMapImpl topicMap;
  P parent;
  String id;

  public TopicMapItem(TopicMapImpl topicMap, P parent) {
    super();
    this.topicMap = topicMap;
    this.parent = parent;
    // this.id = topicMap.generateId(this);
  }

  @Override
  public P getParent() {
    return parent;
  }

  @Override
  public TopicMapImpl getTopicMap() {
    return topicMap;
  }


  @Override
  public String getId() {
    if (id == null) {
      id = topicMap.generateId(this);
    }
    return id;
  }
}
