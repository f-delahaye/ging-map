package org.gingolph.tm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;


public class NameImpl extends TopicMapItem<TopicImpl, NameSupport>
    implements Name, Valued, TypedConstruct {

  public NameImpl(TopicMapImpl topicMap, TopicImpl parent) {
    super(topicMap, parent);
  }
  
  @Override
  protected void notifyOwner() {
    support.setOwner(this);
  }

  @Override
  public String getValue() {
    return support.getValue();
  }

  @Override
  public void setValue(String value) throws ModelConstraintException {
    if (value == null) {
      throw new ModelConstraintException(this, "Null value not allowed");
    }
    support.setValue(value);
  }

  @Override
  public Set<Variant> getVariants() {
    List<VariantImpl> variants = support.getVariants();
    return variants == null ? Collections.emptySet() : new UnmodifiableArraySet<>(variants);
  }

  @Override
  public Variant createVariant(String value, Topic... scope) throws ModelConstraintException {
    if (scope == null) {
      throw new ModelConstraintException(this, "Null scope not allowed");
    }
    return createVariant(value, Arrays.asList(scope));
  }

  @Override
  public Variant createVariant(String value, Collection<Topic> scope)
      throws ModelConstraintException {
    return createVariant(value, LocatorImpl.XSD_STRING, scope);
  }

  @Override
  public Variant createVariant(Locator value, Topic... scope) throws ModelConstraintException {
    if (scope == null) {
      throw new ModelConstraintException(this, "Null scope not allowed");
    }
    return createVariant(value, Arrays.asList(scope));
  }

  @Override
  public Variant createVariant(Locator value, Collection<Topic> scope)
      throws ModelConstraintException {
    return createVariant(LocatorImpl.XSD_ANY_URI, value, scope);
  }

  @Override
  public Variant createVariant(String value, Locator dataType, Topic... scope)
      throws ModelConstraintException {
    if (scope == null) {
      throw new ModelConstraintException(this, "Null scope not allowed");
    }
    return createVariant(value, dataType, Arrays.asList(scope));
  }

  @Override
  public Variant createVariant(String value, Locator dataType, Collection<Topic> scope)
      throws ModelConstraintException {
    return createVariant(dataType, value, scope);
  }

  private Variant createVariant(Locator datatype, Object value, Collection<Topic> scope)
      throws ModelConstraintException {
    if (scope == null) {
      throw new ModelConstraintException(this, "Null scope not allowed");
    }
    if (scope.isEmpty()) {
      throw new ModelConstraintException(this, "Empty scope not allowed");
    }
    if (this.getScope().containsAll(scope)) {
      throw new ModelConstraintException(this, "Scope is not a superset of parent's scope");
    }
    VariantImpl variant = getTopicMap().createVariant(this, datatype, value);
    support.addVariant(variant);
    // Collection<Topic> parentAndSelfScope = new HashSet<>();
    // parentAndSelfScope.addAll(scope);
    // parentAndSelfScope.addAll(getScope());
    variant.setScope(scope);
    getTopicMap().notifyListeners(listener -> listener.onConstructCreated(variant));
    return variant;
  }

  @Override
  public TopicImpl getType() {
    return support.getType();
  }

  @Override
  public void setType(Topic type) {
    TypedInstanceHelper.setType(this, type, this::doSetType);
  }

  protected final void doSetType(Topic type) {
    support.setType((TopicImpl)type);
  }

  @Override
  public void customRemove() {
    Collection<Variant> variants = new ArrayList<>(getVariants());
    variants.forEach(variant -> variant.remove());

    getParent().removeName(this);
  }

  void removeVariant(VariantImpl variant) {
    support.removeVariant(variant);
  }

  @Override
  public Set<Topic> getScope() {
    return ScopedHelper.getScope(support.getScope());
  }

  protected final void setScope(Collection<Topic> scope) {
    ScopedHelper.setScope(this, scope, getSupport());
  }

  @Override
  public void addTheme(Topic theme) throws ModelConstraintException {
    ScopedHelper.addTheme(this, theme, getSupport());
    // getVariants().stream().forEach((variant) -> {
    // variant.addTheme(theme);
    // });
  }

  @Override
  public void removeTheme(Topic theme) {
    ScopedHelper.removeTheme(this, theme, getSupport());
    // getVariants().stream().forEach((variant) -> {
    // variant.removeTheme(theme);
    // });

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
    return getTopicMap().getEquality().equals(this, (NameImpl)otherObjectOfSameClass);
  }

  // consistent with equals and avoid too much overhead calculating hashCodes of Type and Scope ... sounds like a reasonable default.
  @Override
  public int hashCode() {
    return Objects.hashCode(getValue());
  }
  
  void importIn(Name otherName, boolean merge) {
    Collection<Variant> otherVariants = otherName.getVariants();
    otherVariants.forEach(otherVariant -> createVariant(otherVariant.getValue(),
        otherVariant.getDatatype(), otherVariant.getScope()));
    otherName.getItemIdentifiers().forEach(identifier -> importItemIdentifier(identifier));
    if (getReifier() == null) {
      setReifier(otherName.getReifier());
    } else if (otherName.getReifier() != null) {
      ((TopicImpl) getReifier()).importIn(otherName.getReifier(), true);
    }
    if (merge) {
      otherName.remove();
    }
  }
  
  @Override
  public String toString() {
    return "[value="+getValue()+"], [variants="+getVariants()+"], type="+getType();
  }
}
