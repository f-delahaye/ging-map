package org.gingolph.tm;

import org.tmapi.core.Topic;


public interface TypedSupport {

  void setType(Topic type);

  Topic getType();
}
