package org.gingolph.gingmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.gingolph.gingmap.event.TopicMapEventListener;
import org.gingolph.gingmap.event.TopicMapEventListenerSupport;
import org.gingolph.gingmap.support.ConstructSupport;
import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;


public abstract class AbstractConstruct<S extends ConstructSupport> extends TopicMapEventListenerSupport implements Construct {

  S support;

  protected AbstractConstruct() {}

  public S getSupport() {
    return support;
  }

  public void setSupport(S support) {
    this.support = support;
    notifyOwner();
  }
  
  protected abstract void notifyOwner();
  
  @Override
  public abstract TopicMapImpl getTopicMap();

  @Override
  public Set<Locator> getItemIdentifiers() {
    Set<Locator> itemIdentifiers = support.getItemIdentifiers();
    return itemIdentifiers == null ? Collections.emptySet() : itemIdentifiers;
  }

  @Override
  public void addItemIdentifier(Locator identifier) throws ModelConstraintException {
    checkForAddItemIdentifier(identifier);
    importItemIdentifier(identifier);
  }

  protected void checkForAddItemIdentifier(Locator identifier) throws IdentityConstraintException {
    if (identifier == null) {
      throw new ModelConstraintException(this, "Null identifier not allowed");
    }
    Construct existingItemIdentifier = getTopicMap().getConstructByItemIdentifier(identifier);
    if (existingItemIdentifier != null) {
      throw new IdentityConstraintException(this, existingItemIdentifier, identifier,
          "Duplicate item identifiers not allowed");
    }
  }

  public void importItemIdentifier(Locator identifier) {
    support.addItemIdentifier(identifier);
    getTopicMap().notifyListeners(
        (TopicMapEventListener listener) -> listener.onItemIdentifierAdded(this, identifier));
  }

  @Override
  public void removeItemIdentifier(Locator identifier) {
    support.removeItemIdentifier(identifier);
    getTopicMap().notifyListeners(
        (TopicMapEventListener listener) -> listener.onItemIdentifierRemoved(identifier));
  }

  @Override
  public void remove() {
    doRemove();
  }

  protected final void doRemove() {
    getTopicMap().notifyListeners(listener -> listener.onConstructRemoved(this));
    new ArrayList<>(getItemIdentifiers()).forEach(identifier -> removeItemIdentifier(identifier));
    customRemove();
  }

  protected abstract void customRemove();
  
  public final boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    if (((AbstractConstruct<?>)obj).getId().equals(getId())) {
      return true;
    }
    return equalsFromEquality(obj);
  }
  
  protected abstract boolean equalsFromEquality(Object otherObjectOfSameClass);
  
  @Override
  public final int hashCode() {
    return hashCodeFromEquality();
  }

  protected abstract int hashCodeFromEquality();
}
