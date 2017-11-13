package org.gingolph.gingmap.memory;

import java.util.Set;

import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.equality.Equality;
import org.gingolph.gingmap.support.ScopedSupport;
import org.tmapi.core.Topic;

public class IMScopedSupport extends IMConstructSupport implements ScopedSupport {

  private Set<TopicImpl> scope;

  protected Set<TopicImpl> nullSafeScope(Equality equality) {
    if (scope == null) {
      scope = equality.newSet();
    }
    return scope;
  }


  @Override
  public final Set<TopicImpl> getScope() {
    return scope;
  }

  @Override
  public final void addTheme(TopicImpl theme, Equality equality) {
    nullSafeScope(equality).add(theme);
  }

  @Override
  public final void removeTheme(Topic theme) {
    if (scope != null) {
      scope.remove(theme);
    }
  }

}
