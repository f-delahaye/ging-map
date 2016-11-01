package org.gingolph.tm;

import java.util.Collection;
import java.util.Set;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;


public class OccurrenceImpl extends AbstractDatatypeAware<TopicImpl, OccurrenceSupport>
    implements Occurrence, TypedConstruct {

  public OccurrenceImpl(TopicMapImpl topicMap, TopicImpl parent) {
    super(topicMap, parent);
  }
  
  @Override
  protected void notifyOwner() {
    support.setOwner(this);
  }

  @Override
  public void customRemove() {
    getParent().removeOccurrence(this);
  }

  @Override
  public Topic getType() {
    return support.getType();
  }

  @Override
  public void setType(Topic type) throws ModelConstraintException {
    TypedInstanceHelper.setType(this, type, this::doSetType);
  }

  protected void doSetType(Topic type) {
    support.setType(type);
  }

  @Override
  public Set<Topic> getScope() {
    return ScopedHelper.getScope(support.getScope());
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
  public boolean equals(Object other) {
    return other instanceof Occurrence && equals((Occurrence) other);
  }

  protected boolean equals(Occurrence other) {
    return getValue().equals(other.getValue()) && getDatatype().equals(other.getDatatype())
        && getType().equals(other.getType()) && getParent().equals(other.getParent())
        && getScope().equals(other.getScope());
  }

  void importIn(Occurrence otherOccurrence, boolean merge) {
    if (otherOccurrence.getReifier() != null) {
      setReifier(otherOccurrence.getReifier());
    }
    otherOccurrence.getItemIdentifiers().forEach(identifier -> importItemIdentifier(identifier));
    otherOccurrence.remove();
  }
}
