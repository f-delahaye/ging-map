package org.gingolph.tm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;


public class VariantImpl extends AbstractDatatypeAware<NameImpl, VariantSupport>
    implements Variant {

  public VariantImpl(TopicMapImpl topicMap, NameImpl parent) {
    super(topicMap, parent);
  }

  @Override
  protected void notifyOwner() {
    support.setOwner(this);
  }
  
  @Override
  public void customRemove() {
    getParent().removeVariant(this);
  }

  @Override
  public Set<Topic> getScope() {
    Set<Topic> scope = new ArraySet<>(Objects::equals, true);
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

  protected void doSetReifier(TopicImpl reifier) {
    support.setReifier(reifier);
  }
  
  @Override
  protected boolean equalTo(Object otherObjectOfSameClass) {
    return getTopicMap().getEquality().equals(this, (VariantImpl)otherObjectOfSameClass);
  }
  
  // consistent with equals and avoid too much overhead calculating hashCodes of Type and Scope ... sounds like a reasonable default.
  @Override
  public int hashCode() {
    return Objects.hashCode(getValue());
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
