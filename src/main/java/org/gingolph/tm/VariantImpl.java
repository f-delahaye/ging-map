package org.gingolph.tm;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
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
  protected void notifyOwner() {
    support.setOwner(this);
  }
  
  @Override
  public void customRemove() {
    getParent().removeVariant(this);
  }

  @Override
  public Set<Topic> getScope() {
    Set<TopicImpl> scope = getTopicMap().getEquality().newTopicSet();
    scope.addAll((Collection<? extends TopicImpl>) ScopedHelper.getScope(getParent().getScope()));
    scope.addAll((Collection<? extends TopicImpl>) ScopedHelper.getScope(support.getScope()));
    return Collections.unmodifiableSet(scope);
//    return ScopedHelper.getScope(support.getScope());
  }

  protected final void setScope(Collection<Topic> scope) {
    ScopedHelper.setScope(this, scope, support, getTopicMap().getEquality());
  }

  @Override
  public void addTheme(Topic theme) throws ModelConstraintException {
    ScopedHelper.addTheme(this, theme, support, getTopicMap().getEquality());
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
  protected boolean equalsFromEquality(Object otherObjectOfSameClass) {
    return getTopicMap().getEquality().equals(this, (VariantImpl)otherObjectOfSameClass);
  }
  
  // consistent with equals and avoid too much overhead calculating hashCodes of Type and Scope ... sounds like a reasonable default.
//  @Override
//  public int hashCode() {
//    return Objects.hashCode(getValue());
//  }

  @Override
  protected int hashCodeFromEquality() {
    return getTopicMap().getEquality().hashCode(this);
  }  
  
}
