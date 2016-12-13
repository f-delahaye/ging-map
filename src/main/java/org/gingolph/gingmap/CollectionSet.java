package org.gingolph.gingmap;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of Set which is backed by a Collection.
 *
 * It is less efficient that other existing Sets but does honour the Equality selected by the client, especially the SAMEquality.
 * 
 * With that equality:
 * - HashSet is not suitable because SAMEquality does not implement hash() methods
 * - IdentityHashSet is not suitable because SAMEquality is not identity based (unlike TMAPI which works just fine with IdentityHashSet).
 * 
 * This class only has performance issues when using contains() or add() since these methods will iterate over the collection.
 * However, concerns with add() may be taken away in many methods of TMPAPI:
 * By design, TopicMaps.getAssociations() / getTopics() and Association.getRoles() can NEVER contain duplicates because existing elements cannot be added directly, instead, only objects newly created through the createXXX methods will ever be added.
 * So those methods internally use List and wrap them into an immutable ListSet's. Hence, ListSet.add is never called.
 * 
 * This solution is not suitable for Scoped.getScope() and getTypes() because it IS possible to add existing theme/type so we do need to check for duplicates.
 * If speed is a concern in those two cases, then users should default to TMAPI equality which will be more efficient (IdentityHashSet will be internally used). 
 * @param <E>
 */
public class CollectionSet<E> extends AbstractSet<E> {


  final Collection<E> delegate;

  /**
   * Allows to specify the list that will be used to store elements.
   * An already populated list may be supplied (as long as it doesn't contain duplicates), but the main interest of this contructor is to pass
   * a custom List implementation.
   * 
   * @param equals
   */
  @SuppressWarnings("unchecked")
  public CollectionSet(List<? extends E> c) {
    this.delegate = (Collection<E>)c;
  }
    
  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public Iterator<E> iterator() {
    return delegate.iterator();
  }

  @Override
  public boolean add(E element) {
    boolean exists = contains(element);
    if (exists) {
      return false;
    }
    delegate.add(element);
    return true;
  }

}
