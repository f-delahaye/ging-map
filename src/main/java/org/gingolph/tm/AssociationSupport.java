package org.gingolph.tm;

import java.util.Set;
import org.tmapi.core.Role;


public interface AssociationSupport extends ConstructSupport, ScopedSupport, TypedSupport {

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

  Set<Role> getRoles();

  TopicImpl getType();

  void setReifier(TopicImpl reifier);

  void addRole(Role role);

  void removeRole(Role role);

  void setType(TopicImpl type);

}
