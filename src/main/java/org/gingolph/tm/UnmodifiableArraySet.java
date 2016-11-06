package org.gingolph.tm;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * Implementation of Set which does NOT check for duplicates.
 * 
 *  To honour the Set contract, this class is <b>NOT</> modifiable, and <b>MUST</> backed by a List which does not contain duplicates.
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
 * To have a modifiable set of Topics which still respects the selected Equality, Equality.newTopicSet(TopicMap.getTopics()) could be used.
 * 
 * @param <E>
 */
public class UnmodifiableArraySet<E> extends AbstractSet<E> implements Set<E> {

  final List<E> delegate;
  final BiPredicate<E, E> equals;

  /**
   * Allows to specify the list that will be used to store elements.
   * An already populated list may be supplied (as long as it doesn't contain duplicates), but the main interest of this contructor is to pass
   * a custom List implementation.
   * 
   * @param equals
   */
  public UnmodifiableArraySet(Collection<? extends E> c) {
    this(c, Objects::equals);
  }
  
  public UnmodifiableArraySet(Collection<? extends E> c, BiPredicate<E, E> equals) {
    this.delegate = c instanceof List?(List<E>)c:new ArrayList<E>(c);
    this.equals = equals;
  }
    
  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return indexOf(o) != -1;
  }


  @Override
  public Iterator<E> iterator() {
    return delegate.iterator();
  }

  @Override
  public Object[] toArray() {
    return delegate.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return delegate.toArray(a);
  }
  protected int indexOf(Object o) {
    @SuppressWarnings("unchecked")
    E element = (E)o;
    for (int i=0; i<delegate.size(); i++) {
      E candidate = delegate.get(i);
      if (equals.test(candidate, element)) {
        return i;
      }
    }
    return -1;
    
  }

  @Override
  public boolean add(E e) {
    throw new UnsupportedOperationException();  
  }
  
  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

}
