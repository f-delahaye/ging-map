package org.gingolph.tm.memory;

import java.util.HashSet;
import java.util.Set;
import org.gingolph.tm.ConstructSupport;
import org.tmapi.core.Locator;


public class IMConstructSupport implements ConstructSupport {
  private Set<Locator> itemIdentifiers;

  @Override
  public Set<Locator> getItemIdentifiers() {
    return itemIdentifiers;
  }

  @Override
  public void addItemIdentifier(Locator identifier) {
    if (itemIdentifiers == null) {
      itemIdentifiers = new HashSet<>();
    }
    itemIdentifiers.add(identifier);
  }

  @Override
  public void removeItemIdentifier(Locator identifier) {
    if (itemIdentifiers != null) {
      itemIdentifiers.remove(identifier);
    }
  }
}
