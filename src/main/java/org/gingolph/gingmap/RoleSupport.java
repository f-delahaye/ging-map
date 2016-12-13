package org.gingolph.gingmap;

public interface RoleSupport extends ConstructSupport, TypedSupport {

  /**
   * Callback method invoked by RoleImpl.setSupport.
   * This is the reverse relationship.
   * A role NEEDS a support as most of its operations are delegated to the support.
   * Conversely, in certain implementations, a support MAY need its role.
   * 
   * Implementations are not required to store the supplied reference if they don't need it.
   * @param owner
   */
  void setOwner(RoleImpl owner);
  
  TopicImpl getPlayer();

  TopicImpl getReifier();

  TopicImpl getType();

  void setPlayer(TopicImpl player);

  void setReifier(TopicImpl reifier);

  void setType(TopicImpl type);
}
