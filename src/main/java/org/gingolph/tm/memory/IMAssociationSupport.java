package org.gingolph.tm.memory;

import java.util.Set;

import org.gingolph.tm.ArraySet;
import org.gingolph.tm.AssociationImpl;
import org.gingolph.tm.AssociationSupport;
import org.gingolph.tm.RoleImpl;
import org.gingolph.tm.TopicImpl;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;


public class IMAssociationSupport extends IMScopedSupport implements AssociationSupport {
  private Topic type;
  private final Set<Role> roles = new ArraySet<>(RoleImpl::equalsNoParent);
  private TopicImpl reifier;

  @Override
  public Topic getType() {
    return type;
  }

  @Override
  public void setType(Topic type) {
    this.type = type;
  }

  @Override
  public Set<Role> getRoles() {
    return roles;
  }

  @Override
  public void addRole(Role role) {
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
