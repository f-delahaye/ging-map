package org.gingolph.tm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;


public class VariantImpl extends AbstractDatatypeAware<NameImpl, VariantSupport>
    implements Variant {

  public VariantImpl(TopicMapImpl topicMap, NameImpl parent) {
    super(topicMap, parent);
  }

  @Override
  public void customRemove() {
    getParent().removeVariant(this);
  }

  @Override
  public Set<Topic> getScope() {
    HashSet<Topic> scope = new HashSet<>();
    scope.addAll(ScopedHelper.getScope(getParent().getScope()));
    scope.addAll(ScopedHelper.getScope(support.getScope()));
    return scope;
    // return ScopedHelper.getScope(scope);
  }

  protected final void setScope(Collection<Topic> scope) {
    ScopedHelper.setScope(this, scope, support);
  }

  @Override
  public void addTheme(Topic theme) throws ModelConstraintException {
    ScopedHelper.addTheme(this, theme, support);
  }

  @Override
  public void removeTheme(Topic theme) {
    ScopedHelper.removeTheme(this, theme, support);
  }

  @Override
  public Topic getReifier() {
    return support.getReifier();
  }

  @Override
  public void setReifier(Topic reifier) throws ModelConstraintException {
    ReifierHelper.setReifier(this, reifier, this::doSetReifier);
  }

  protected void doSetReifier(Topic reifier) {
    support.setReifier(reifier);
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof Variant && equals((Variant) other);
  }

  protected boolean equals(Variant other) {
    return equalsNoParent(other) && getParent().equals(other.getParent());
  }
  
  protected boolean equalsNoParent(Variant other) {
    return getValue().equals(other.getValue()) && getDatatype().equals(other.getDatatype())
        && getScope().equals(other.getScope());
  }
  
}
