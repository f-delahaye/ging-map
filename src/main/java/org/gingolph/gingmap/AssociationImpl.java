package org.gingolph.gingmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.tmapi.core.Association;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;



public class AssociationImpl extends ScopedTopicMapItem<TopicMapImpl, AssociationSupport>
    implements Association, TypedConstruct {

  public AssociationImpl(TopicMapImpl topicMap) {
    super(topicMap, topicMap);
  }

  @Override
  protected void notifyOwner() {
    support.setOwner(this);
  }
  
  @Override
  protected void customRemove() {
    new ArrayList<>(getNullSafeRoleImpls()).forEach(role -> ((RoleImpl) role).doRemove());
    getParent().removeAssociation(this);
  }

  @Override
  public Set<Role> getRoles() {
    return new UnmodifiableCollectionSet<>(getNullSafeRoleImpls());
  }

  public List<RoleImpl> getNullSafeRoleImpls() {
    List<RoleImpl> roles = support.getRoles();
    return roles == null?Collections.emptyList():roles;
  }

  @Override
  public Set<Role> getRoles(Topic type) {
    if (type == null) {
      throw new IllegalArgumentException("Null type not allowed");
    }
    return getNullSafeRoleImpls().stream().filter(role -> role.getType() == type)
        .collect(Collectors.toSet());
  }

  @Override
  public Set<Topic> getRoleTypes() {
    Set<TopicImpl> roleTypes = getTopicMap().getEquality().newSet();
    getNullSafeRoleImpls().forEach((role) -> {
      roleTypes.add(role.getType());
    });
    return Collections.unmodifiableSet(roleTypes);
  }

  @Override
  public RoleImpl createRole(Topic type, Topic player) throws ModelConstraintException {
    if (player == null) {
      throw new ModelConstraintException(this, "Null player not allowed");
    }
    RoleImpl role = new RoleImpl(topicMap, this);
    role.setSupport(getTopicMap().createRoleSupport());
    role.setPlayer(player);
    role.setType(type);
    support.addRole(role);
    getTopicMap().notifyListeners(listener -> listener.onConstructCreated(role));
    return role;
  }

  void removeRole(RoleImpl role) {
    support.removeRole(role);
  }

  @Override
  public TopicImpl getType() {
    return support.getType();
  }

  @Override
  public void setType(Topic type) throws ModelConstraintException {
    TypedInstanceHelper.setType(this, type, this::doSetType);
  }

  protected void doSetType(Topic type) {
    support.setType((TopicImpl)type);
  }

  @Override
  public Set<Topic> getScope() {
    return ScopedHelper.getScope(support.getScope());
  }

  protected final void setScope(Collection<Topic> scope) {
    ScopedHelper.setScope(this, scope, support, getTopicMap().getEquality());
  }

  @Override
  public void addTheme(Topic theme) throws ModelConstraintException {
    ScopedHelper.addTheme(this, theme, support, getTopicMap().getEquality());
  }

  @Override
  public void removeTheme(Topic theme) {
    ScopedHelper.removeTheme(this, theme, support);
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
    return getTopicMap().getEquality().equals(this, (AssociationImpl)otherObjectOfSameClass);
  }
  
  @Override
  protected int hashCodeFromEquality() {
    return getTopicMap().getEquality().hashCode(this);
  }  

  // Consistent with equals and not too much overhead calculating lots of hashCodes ... but probably has poor distribution
  // TODO: should all the roles' hashcodes be included as well?
//  @Override
//  public int hashCode() {
//    return getType().getId().hashCode();
//  }

  public String toString() {
    return "[type="+getType()+", roles="+getRoles()+"]";
  }
    
}
