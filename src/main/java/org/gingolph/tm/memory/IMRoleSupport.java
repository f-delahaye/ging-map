package org.gingolph.tm.memory;

import org.gingolph.tm.RoleImpl;
import org.gingolph.tm.RoleSupport;
import org.gingolph.tm.TopicImpl;
import org.tmapi.core.Topic;


public class IMRoleSupport extends IMConstructSupport implements RoleSupport {
  Topic type;
  TopicImpl reifier;
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
  public TopicImpl getReifier() {
    return reifier;
  }

  @Override
  public void setReifier(TopicImpl reifier) {
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

  @Override
  public void setOwner(RoleImpl owner) {
 // Noop - not needed by the in memory implementation    
  }  
}
