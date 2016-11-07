package org.gingolph.tm.memory;

import java.util.Set;

import org.gingolph.tm.ScopedSupport;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.equality.Equality;
import org.tmapi.core.Topic;

public class IMScopedSupport extends IMConstructSupport implements ScopedSupport {

  private Set<TopicImpl> scope;

  protected Set<TopicImpl> nullSafeScope(Equality equality) {
    if (scope == null) {
      scope = equality.newTopicSet();
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
