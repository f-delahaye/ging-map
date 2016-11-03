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
    TopicImpl player = (TopicImpl) getPlayer();
    player.getSupport().removeRolePlayed(this);
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
  public int hashCode() {
    return getId().hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Role && equals((Role) other);
  }

  // According to the TopicMap specs, roles are deemed equals if they have the same player, type and
  // parent.
  // But I can't see how TestTopic.testRoleAssociationFilter can work with that (role1 and role2
  // have the same player, type and parent so getRolesPlayed(), which returns a set, should haved 1
  // item, not 2)
  protected boolean equals(Role otherRole) {
    return this == otherRole || getId().equals(otherRole.getId());
  }

  protected void importIn(Role otherRole, boolean merge) {
    this.id = otherRole.getId();
    otherRole.getItemIdentifiers().forEach(identifier -> importItemIdentifier(identifier));
  }
}
