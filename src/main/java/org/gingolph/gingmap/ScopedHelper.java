package org.gingolph.gingmap;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.gingolph.gingmap.equality.Equality;
import org.gingolph.gingmap.support.ScopedSupport;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;


public class ScopedHelper {

  public static <T extends ScopedTopicMapItem<?,?>> void setScope(T scoped, Collection<Topic> scope,
      ScopedSupport support, Equality equality) {
      scope.forEach(theme -> addTheme(scoped, theme, support, equality));
  }

  public static Set<Topic> getScope(Set<? extends Topic> scope) {
    return scope == null ? Collections.emptySet() : Collections.unmodifiableSet(scope);
  }

  public static <T extends ScopedTopicMapItem<?,?>> void addTheme(T scoped, Topic theme,
      ScopedSupport support, Equality equality) throws ModelConstraintException {
    if (theme == null) {
      throw new ModelConstraintException(scoped, "Null theme not allowed");
    }
    if (scoped.getTopicMap() != theme.getTopicMap()) {
      throw new ModelConstraintException(scoped, "Different topic maps not allowed");
    }
    support.addTheme((TopicImpl)theme, equality);
    scoped.getTopicMap().notifyListeners(listener -> listener.onThemeChanged(scoped, theme, null));
  }

  public static <T extends ScopedTopicMapItem<?,?>> void removeTheme(T scoped, Topic theme,
      ScopedSupport support) {
    support.removeTheme(theme);
    scoped.getTopicMap().notifyListeners(listener -> listener.onThemeChanged(scoped, null, theme));
  }
}
