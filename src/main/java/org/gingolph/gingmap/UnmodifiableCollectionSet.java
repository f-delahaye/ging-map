package org.gingolph.gingmap;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation of Set which does NOT check for duplicates.
 * 
 *  To honour the Set contract, this class does <b>NOT</> support add(), and <b>MUST</> backed by a List which does not contain duplicates.
 * 
 * This guarantees this Set will never contain duplicates.
 * 
 * This implementation is useful for Topic.getNames(), Association.getRoles(), TopicMap.getAssociations() and TopicMap.getTopics() which by essence, do not contain duplicates (it is not possible to add existing roles resp associations resp topics, only new instances created by the corresponding create methods may be added).
 * It is <b>NOT</b> suitable for Sets of types and scopes (Sets of themes) because these can be added existing topics multiple times, which would result in duplicates.
 * 
 * For the methods listed above, an IdentityHashSet could be used, since tmapi requires that Contruct.equals() be identity based.
 * But class allows us to call Construct.equals(), which may or may not be that of the spec, depending of the Equality algorithm selected by users.
 * So for example, users may call TopicMap.getTopics().contains(topic) and expect contains() to be compliant with their selected Equality, which would not be the case had we use an IdentityHashSet.
 * 
 * To have a modifiable set of Topics which still respects the selected Equality, new ArraySet(TopicMap.getTopics()) could be used.
 * 
 * @param <E>
 */
public class UnmodifiableCollectionSet<E> extends AbstractSet<E> implements Set<E> {

  final Collection<E> delegate;

  /**
   * Allows to specify the list that will be used to store elements.
   * An already populated list may be supplied (as long as it doesn't contain duplicates), but the main interest of this contructor is to pass
   * a custom List implementation.
   * 
   * @param equals
   */
  @SuppressWarnings("unchecked")
  public UnmodifiableCollectionSet(Collection<? extends E> c) {
    this.delegate = (Collection<E>)c;
  }
    
  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      Iterator<E> i = delegate.iterator();

      @Override
      public boolean hasNext() {
        return i.hasNext();
      }

      @Override
      public E next() {
        return i.next();
      }
      // remove() by default is not supported which is the desired behavior.
    };
  }
  
  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }  
}
