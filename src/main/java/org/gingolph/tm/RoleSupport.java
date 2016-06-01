package org.gingolph.tm;

import org.tmapi.core.Topic;


public interface RoleSupport extends ConstructSupport, TypedSupport {

  Topic getPlayer();

  Topic getReifier();

  Topic getType();

  void setPlayer(TopicImpl player);

  void setReifier(Topic reifier);

  void setType(Topic type);
}
