package org.gingolph.tm;

import org.tmapi.core.Topic;


public interface RoleSupport extends ConstructSupport, TypedSupport {

  Topic getPlayer();

  TopicImpl getReifier();

  Topic getType();

  void setPlayer(TopicImpl player);

  void setReifier(TopicImpl reifier);

  void setType(Topic type);
}
