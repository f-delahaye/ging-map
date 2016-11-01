package org.gingolph.tm;

import org.tmapi.core.Topic;


public interface OccurrenceSupport
    extends ConstructSupport, ScopedSupport, TypedSupport, DatatypeAwareSupport {

  TopicImpl getReifier();

  Topic getType();

  void setReifier(TopicImpl reifier);

  void setType(Topic type);

}
