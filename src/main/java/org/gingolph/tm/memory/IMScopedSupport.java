package org.gingolph.tm.memory;

import java.util.HashSet;
import java.util.Set;
import org.gingolph.tm.ScopedSupport;
import org.tmapi.core.Topic;


public class IMScopedSupport extends IMConstructSupport implements ScopedSupport {

  private Set<Topic> scope;

  protected Set<Topic> nullSafeScope() {
    if (scope == null) {
      scope = new HashSet<>();
    }
    return scope;
  }


  @Override
  public final Set<Topic> getScope() {
    return scope;
  }

  @Override
  public final void addTheme(Topic theme) {
    nullSafeScope().add(theme);
  }

  @Override
  public final void removeTheme(Topic theme) {
    if (scope != null) {
      scope.remove(theme);
    }
  }

}
