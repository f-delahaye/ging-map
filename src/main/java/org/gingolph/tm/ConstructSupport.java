package org.gingolph.tm;

import java.util.Set;
import org.tmapi.core.Locator;

public interface ConstructSupport {

  void addItemIdentifier(Locator identifier);

  Set<Locator> getItemIdentifiers();

  void removeItemIdentifier(Locator identifier);
}
