package org.gingolph.tm;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;


public class RoleImpl extends TopicMapItem<AssociationImpl, RoleSupport>
    implements Role, TypedConstruct {


  public RoleImpl(TopicMapImpl topicMap, AssociationImpl parent) {
    super(topicMap, parent);
  }

  @Override
  protected void notifyOwner() {
    support.setOwner(this);
  }

  @Override
  public void customRemove() {
    ((TopicImpl) getPlayer()).getSupport().removeRolePlayed(this);
    getParent().removeRole(this);
  }

  @Override
  public Topic getPlayer() {
    return support.getPlayer();
  }

  @Override
  public void setPlayer(Topic player) {
    if (player == null) {
      throw new ModelConstraintException(this, "Null player not allowed");
    }
    if (getTopicMap() != player.getTopicMap()) {
      throw new ModelConstraintException(this, "Different topic maps not allowed");
    }
    final TopicImpl playerImpl = (TopicImpl) player;
    Topic currentPlayer = getPlayer();
    if (currentPlayer != null) {
      ((TopicImpl) currentPlayer).removeRolePlayed(this);
    }
    support.setPlayer(playerImpl);
    playerImpl.addRolePlayed(this);
  }

  @Override
  public Topic getType() {
    return support.getType();
  }

  @Override
  public void setType(Topic type) {
    TypedInstanceHelper.setType(this, type, this::doSetType);
  }

  protected void doSetType(Topic type) {
    support.setType(type);
  }

  @Override
  public Topic getReifier() {
    return support.getReifier();
  }

  @Override
  public void setReifier(Topic reifier) throws ModelConstraintException {
    ReifierHelper.setReifier(this, reifier, this::doSetReifier);
  }

  protected void doSetReifier(TopicImpl reifier) {
    support.setReifier(reifier);
  }

  @Override
  protected boolean equalTo(Object otherObjectOfSameClass) {
    return getTopicMap().getEquality().equals(this, (RoleImpl)otherObjectOfSameClass);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Role && equals((Role) other);
  }
  
  protected boolean equals(Role otherRole) {
//    return this == otherRole || getId().equals(otherRole.getId());
    return getParent().equals(otherRole.getParent()) && equalsNoParent(this, otherRole);
  }

  // specific method to be called when we know for sure (or don't care that) other.parent = this.parent  
  public static boolean equalsNoParent(Role role, Role otherRole) {
    return role.getPlayer().equals(otherRole.getPlayer()) && role.getType().equals(otherRole.getType());    
  }

  @Override
  public String toString() {
    return "[type="+getType()+", player="+getPlayer()+"]";
  }
  
  protected void importIn(Role otherRole, boolean merge) {
    this.id = otherRole.getId();
    otherRole.getItemIdentifiers().forEach(identifier -> importItemIdentifier(identifier));
  }
}
