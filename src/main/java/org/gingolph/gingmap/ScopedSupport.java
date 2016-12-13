package org.gingolph.gingmap;

import java.util.Set;

import org.gingolph.gingmap.equality.Equality;
import org.tmapi.core.Topic;


public interface ScopedSupport {

  void addTheme(TopicImpl t, Equality equality);

  Set<TopicImpl> getScope();

  void removeTheme(Topic t);

}
