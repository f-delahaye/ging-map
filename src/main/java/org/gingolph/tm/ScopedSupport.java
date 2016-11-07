package org.gingolph.tm;

import java.util.Set;

import org.gingolph.tm.equality.Equality;
import org.tmapi.core.Topic;


public interface ScopedSupport {

  void addTheme(TopicImpl t, Equality equality);

  Set<TopicImpl> getScope();

  void removeTheme(Topic t);

}
