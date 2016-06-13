package org.gingolph.tm;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * 
 * *Note*: this is an experimental class. Performances should be validated against benchmarks and business assumptions against specs.
 *  
 * This class serves several purposes:
 * - it does not use hashCode (unlike HashSet) so it may be used with Topics (GTM's Topic does not support hashCode()
 * - it does not use Comparators (unlike TreeSet) so it may be used with Associations (How can 2 Associations be compared??)
 * - it does not use Object.equals so it may be used with Roles (see comment in Role.equals() as to why it might cause a loop).
 * 
 * A BiPredicate must be specified, that will be used in place of equals() for operations such as contains().
 * Hence, ArraySets used for Roles may pass Role::equalsNoParent which will solve the potential loop mentioned above.
 * 
 * Since ArraySet doesn't use hash or comparator, contains() will have to iterate through all the elements. However, it is possible that the overhead is not as bad as it may seem: 
 * because ArrayList uses contiguous memory, all items may fit in the cache and iterating may be super fast. 
 * 
 * Moreover, GTM is making the assumption that it is not possible to add duplicates in an array list:
 * - TopicMap.createTopic will auto merge, or throw an exception, in case of duplicates and will never create a topic if it already exists.
 * - TMAPI don't specify what it means for 2 associations (resp. roles) to be equals. If 2 associations are of the same type and contain the same role, 
 *  then the *merging* operation will deem them equals, but they still can be added both into the TopicMap. In fact, an association is created with just a type, and then gets added in the topic map. 
 *  If more roles are added to it that makes it a duplicate of an existing association, should it just stop from being returned by TopicMap.getAssociations()?    
 *  
 * Given the above, ArraySet doesn't check for duplicates, so add() doesn't need to call contains(). It still *IS* a Set, which by virtue of the TMP APIs guarantees that no duplicate topics will exist, and no duplicate associations will exist after a merge.
 * remove() always calls contains() though.
 * 
 */
public class ArraySet<E> extends AbstractCollection<E> implements Set<E> {

  final List<E> delegate;
  final BiPredicate<E, E> equals;

  /**
   * Allows to specify the list that will be used to store elements.
   * An already populated list may be supplied (as long as it doesn't contain duplicates), but the main interest of this contructor is to pass
   * a custom List implementation.
   * 
   * @param equals
   */
  public ArraySet(List<E> c, BiPredicate<E, E> equals) {
    this.delegate = c;
    this.equals = equals;
  }

  /**
   * Creates an array set using an ArrayList as the actual storage.
   * @param equalizer
   */
  public ArraySet(BiPredicate<E, E> equalizer) {
    this(new ArrayList<>(), equalizer);
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

  @Override
  public boolean add(E e) {
    return delegate.add(e);
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
  public boolean remove(Object o) {
    int indexOf = indexOf(o);
    if (indexOf != -1) {
      delegate.remove(indexOf);
      return true;
    }
    return false;
  }

  @Override
  public void clear() {
    delegate.clear();
  }

}
