package org.gingolph.gingmap;

public interface OccurrenceSupport
    extends ConstructSupport, ScopedSupport, TypedSupport, DatatypeAwareSupport {

  /**
   * Callback method invoked by OccurrenceImpl.setSupport.
   * This is the reverse relationship.
   * An occurrence NEEDS a support as most of its operations are delegated to the support.
   * Conversely, in certain implementations, a support MAY need its occurrence.
   * 
   * Implementations are not required to store the supplied reference if they don't need it.
   * @param owner
   */  
  void setOwner(OccurrenceImpl owner);
  
  TopicImpl getReifier();

  TopicImpl getType();

  void setReifier(TopicImpl reifier);

  void setType(TopicImpl type);

}
