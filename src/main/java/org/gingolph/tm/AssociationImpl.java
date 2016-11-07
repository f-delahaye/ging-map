package org.gingolph.tm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
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
    Set<TopicImpl> roleTypes = getTopicMap().getEquality().newTopicSet();
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
  
  void importIn(AssociationImpl otherAssociation, boolean merge) {
    final Collection<RoleImpl> otherRoles = new ArrayList<>(otherAssociation.getNullSafeRoleImpls());
    final Set<Locator> itemIdentifiers = otherAssociation.getItemIdentifiers();
    final Topic otherReifier = otherAssociation.getReifier();
    if (merge) {
      otherAssociation.doRemove();
    }

    // This method can be called from TopicImpl.importIn when either:
    // - an existing association is imported into a newly created one
    // - or 2 existing equivalent associations are merged together
    // In the first case, we know there will never be an equivalent role, since its a newly created association with no role.
    // In the second case, we know that there always be an equivalent trole: this (amongst other things) is a requirement for 2 associations to be deemed equivalent.
    // So either the lookup of equivalentRole is not needed, or it has been done already.
    // However, code below should be cheap, and it works for both logics.
    
    for (RoleImpl otherRole: otherRoles) {
      Optional<RoleImpl> equivalentRole = getNullSafeRoleImpls().stream().filter(candidateRole -> getTopicMap().getEqualityForMerge().equals(candidateRole, otherRole)).findAny();
      RoleImpl mergee = equivalentRole.orElse(createRole(otherRole.getType(), otherRole.getPlayer()));
      mergee.importIn(otherRole, merge);
    }
    
    itemIdentifiers.forEach(identifier -> importItemIdentifier(identifier));
    if (getReifier() == null) {
      setReifier(otherReifier);
    } else if (otherReifier != null) {
      getReifier().mergeIn(otherReifier);
    }
  }
  
}
