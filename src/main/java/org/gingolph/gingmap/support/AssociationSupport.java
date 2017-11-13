package org.gingolph.gingmap.support;

import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.RoleImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.support.spi.AssociationInterface;
import org.tmapi.core.Role;


public interface AssociationSupport extends ConstructSupport, ScopedSupport, TypedSupport, AssociationInterface<TopicImpl, RoleImpl> {

  /**
   * Callback method invoked by AssociationImpl.setSupport.
   * This is the reverse relationship.
   * An association NEEDS a support as most of its operations are delegated to the support.
   * Conversely, in certain implementations, a support MAY need its association.
   * 
   * Implementations are not required to store the supplied reference if they don't need it.
   * @param owner
   */  
  void setOwner(AssociationImpl owner);
  
  TopicImpl getReifier();

  void setReifier(TopicImpl reifier);

  void setType(TopicImpl type);

}
