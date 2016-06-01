package org.gingolph.tm;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;


public class ScopedHelper {

  public static <T extends TopicMapItem & Scoped> void setScope(T scoped, Collection<Topic> scope,
      ScopedSupport support) {
    if (scope != null) {
      scope.forEach(theme -> addTheme(scoped, theme, support));
    }
  }

  public static Set<Topic> getScope(Set<? extends Topic> scope) {
    return scope == null ? Collections.emptySet() : Collections.unmodifiableSet(scope);
  }

  public static <T extends TopicMapItem & Scoped> void addTheme(T scoped, Topic theme,
      ScopedSupport support) throws ModelConstraintException {
    if (theme == null) {
      throw new ModelConstraintException(scoped, "Null theme not allowed");
    }
    if (scoped.getTopicMap() != theme.getTopicMap()) {
      throw new ModelConstraintException(scoped, "Different topic maps not allowed");
    }
    support.addTheme(theme);
    scoped.getTopicMap().notifyListeners(listener -> listener.onThemeChanged(scoped, theme, null));
  }

  public static <T extends TopicMapItem & Scoped> void removeTheme(T scoped, Topic theme,
      ScopedSupport support) {
    support.removeTheme(theme);
    scoped.getTopicMap().notifyListeners(listener -> listener.onThemeChanged(scoped, null, theme));
  }


}
