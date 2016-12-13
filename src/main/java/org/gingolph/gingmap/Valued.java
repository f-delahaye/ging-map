package org.gingolph.gingmap;

/**
 * A non standard interface which mirrors Typed and Scoped and defines a common parent for classes
 * which may be stored in an IdentifierIndex.
 * 
 * @author fdel
 */
public interface Valued {
  String getValue();
}
