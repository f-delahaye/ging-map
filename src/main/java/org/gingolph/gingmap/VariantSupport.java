package org.gingolph.gingmap;

import org.tmapi.core.Topic;


public interface VariantSupport extends ConstructSupport, ScopedSupport, DatatypeAwareSupport {

  /**
   * Callback method invoked by VariantImpl.setSupport.
   * This is the reverse relationship.
   * A variant NEEDS a support as most of its operations are delegated to the support.
   * Conversely, in certain implementations, a support MAY need its variant.
   * 
   * Implementations are not required to store the supplied reference if they don't need it.
   * @param owner
   */  
  void setOwner(VariantImpl owner);
  
  Topic getReifier();

  void setReifier(TopicImpl reifier);
  
}
