package org.gingolph.tm.memory;

import org.gingolph.tm.RoleSupport;
import org.gingolph.tm.TopicImpl;
import org.tmapi.core.Topic;


public class IMRoleSupport extends IMConstructSupport implements RoleSupport {
  Topic type;
  Topic reifier;
  TopicImpl player;

  @Override
  public Topic getType() {
    return type;
  }

  @Override
  public void setType(Topic type) {
    this.type = type;
  }

  @Override
  public Topic getReifier() {
    return reifier;
  }

  @Override
  public void setReifier(Topic reifier) {
    this.reifier = reifier;
  }

  @Override
  public TopicImpl getPlayer() {
    return player;
  }

  @Override
  public void setPlayer(TopicImpl player) {
    this.player = player;
  }
}
