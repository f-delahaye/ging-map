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
  public TopicImpl getPlayer() {
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
  public TopicImpl getType() {
    return support.getType();
  }

  @Override
  public void setType(Topic type) {
    TypedInstanceHelper.setType(this, type, this::doSetType);
  }

  protected void doSetType(Topic type) {
    support.setType((TopicImpl)type);
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
  protected boolean equalsFromEquality(Object otherObjectOfSameClass) {
    return getTopicMap().getEquality().equals(this, (RoleImpl)otherObjectOfSameClass);
  }
  
  @Override
  protected int hashCodeFromEquality() {
    return getTopicMap().getEquality().hashCode(this);
  }  

  @Override
  public String toString() {
    return "[type="+getType()+", player="+getPlayer()+"]";
  }
}
