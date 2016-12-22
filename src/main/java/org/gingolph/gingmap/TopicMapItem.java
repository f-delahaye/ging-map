package org.gingolph.gingmap;

import org.tmapi.core.Construct;

public abstract class TopicMapItem<P extends Construct, S extends ConstructSupport>
    extends AbstractConstruct<S> {
  TopicMapImpl topicMap;
  P parent;

  public TopicMapItem(TopicMapImpl topicMap, P parent) {
    super();
    this.topicMap = topicMap;
    this.parent = parent;
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
    String id = support.getId();
    if (id == null) {
      id = topicMap.generateId(this);
      support.setId(id);
    }
    return id;
  }
}
