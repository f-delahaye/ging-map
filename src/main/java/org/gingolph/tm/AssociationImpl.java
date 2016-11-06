package org.gingolph.tm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.gingolph.tm.equality.Equality;
import org.gingolph.tm.equality.SAMEquality;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;



public class AssociationImpl extends TopicMapItem<TopicMapImpl, AssociationSupport>
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
    return new UnmodifiableArraySet<>(getNullSafeRoleImpls());
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
    Set<TopicImpl> roleTypes = new IdentityHashSet<>();
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
    ScopedHelper.setScope(this, scope, support);
  }

  @Override
  public void addTheme(Topic theme) throws ModelConstraintException {
    ScopedHelper.addTheme(this, theme, support);
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
  protected boolean equalTo(Object otherObjectOfSameClass) {
    return getTopicMap().getEquality().equals(this, (AssociationImpl)otherObjectOfSameClass);
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
  
  void importIn(AssociationImpl otherAssociation, boolean merge) {
    final Collection<RoleImpl> otherRoles = new ArrayList<>(otherAssociation.getNullSafeRoleImpls());
    final Set<Locator> itemIdentifiers = otherAssociation.getItemIdentifiers();
    final Topic otherReifier = otherAssociation.getReifier();
    if (merge) {
      otherAssociation.doRemove();
    }
    
    for (RoleImpl otherRole: otherRoles) {
      Optional<RoleImpl> equivalentRole = getNullSafeRoleImpls().stream().filter(candidateRole -> getTopicMap().getEqualityForMerge().equals(candidateRole, otherRole)).findAny();
      if (equivalentRole.isPresent()) {
        equivalentRole.get().importIn(otherRole, merge);
      } else {
        otherRole.setParent(this);        
        support.addRole(otherRole);
      }
    }
    
    itemIdentifiers.forEach(identifier -> importItemIdentifier(identifier));
    if (getReifier() == null) {
      setReifier(otherReifier);
    } else if (otherReifier != null) {
      getReifier().mergeIn(otherReifier);
    }
  }
  
}
