package org.gingolph.tm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.gingolph.tm.event.TopicMapEventListener;
import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;


public abstract class IdentifiedConstruct<S extends ConstructSupport>
    extends SupportedConstruct<S> {

  protected IdentifiedConstruct() {}

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

  protected void importItemIdentifier(Locator identifier) {
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
}
