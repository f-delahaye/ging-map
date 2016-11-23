package org.gingolph.tm.memory;

import java.util.ArrayList;
import java.util.List;

import org.gingolph.tm.AssociationImpl;
import org.gingolph.tm.AssociationSupport;
import org.gingolph.tm.RoleImpl;
import org.gingolph.tm.TopicImpl;
import org.tmapi.core.Role;


public class IMAssociationSupport extends IMScopedSupport implements AssociationSupport {
  private TopicImpl type;
  private final List<RoleImpl> roles = new ArrayList<>();
  private TopicImpl reifier;

  @Override
  public TopicImpl getType() {
    return type;
  }

  @Override
  public void setType(TopicImpl type) {
    this.type = type;
  }

  @Override
  public List<RoleImpl> getRoles() {
    return roles;
  }

  @Override
  public void addRole(RoleImpl role) {
    this.roles.add(role);
  }

  @Override
  public void removeRole(Role role) {
    this.roles.remove(role);
  }

  @Override
  public TopicImpl getReifier() {
    return reifier;
  }

  @Override
  public void setReifier(TopicImpl reifier) {
    this.reifier = reifier;
  }

  @Override
  public void setOwner(AssociationImpl owner) {
// Noop - not needed by the in memory implementation    
  }


}
