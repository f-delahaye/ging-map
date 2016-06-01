package org.gingolph.tm.hg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.gingolph.tm.AssociationImpl;
import org.gingolph.tm.AssociationSupport;
import org.gingolph.tm.RoleImpl;
import org.gingolph.tm.RoleSupport;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.annotation.HGIgnore;
import org.tmapi.core.Association;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

public class HGAssociationSupport extends HGScopedSupport<Association>
    implements AssociationSupport, HGLink {

  transient List<HGHandle> roles;
  Topic type; // For some weird reason, if type is not stored locally, Topic.mergeIn fails for
              // topics which have roles.
  // See TestTopicMerge.testRolePlaying and testDuplicateSuppressionAssociation
  // TODO fix this

  public HGAssociationSupport(Association association) {
    super(association);
    roles = new ArrayList<>();
  }

  protected HGAssociationSupport(HGHandle[] handles) {
    this.roles = Arrays.asList(handles);
  }

  public HGTopicMapSupport getTopicMapSupport() {
    return HGTMUtil.getTopicMapOf(hyperGraph, getHandle(hyperGraph, this));
  }

  @Override
  public void addRole(Role role) {
    final HyperGraph graph = getGraph();
    RoleSupport support = ((RoleImpl) role).getSupport();
    roles.add(add(graph, support));
    graph.update(this);
  }

  @Override
  public void removeRole(Role role) {
    HyperGraph graph = getGraph();
    final HGHandle roleHandle = getHandle(graph, role);
    if (roleHandle != null) {
      roles.remove(roleHandle);
      graph.update(this);
      graph.remove(roleHandle, true);
    }
  }

  @Override
  public Set<Role> getRoles() {
    return new RoleSet(getGraph(), this);
  }

  @HGIgnore
  @Override
  public Topic getReifier() {
    final HyperGraph graph = getGraph();
    HGHandle h = HGTMUtil.getReifierOf(graph, getHandle(graph, this));
    return h != null ? ((HGTopicSupport) graph.get(h)).getOwner() : null;
  }

  @HGIgnore
  @Override
  public void setReifier(Topic topic) {
    final HyperGraph graph = getGraph();
    HGTMUtil.setReifierOf(graph, getHandle(graph, this),
        topic == null ? null : getHandle(graph, topic));
  }

  @HGIgnore
  @Override
  public Topic getType() {
    if (type == null) {
      final HGHandle thisHandle = getHandle(hyperGraph, this);
      HGHandle h = HGTMUtil.getTypeOf(hyperGraph, thisHandle);
      type = h != null ? ((HGTopicSupport) hyperGraph.get(h)).getOwner() : null;
    }
    return type;
  }

  @HGIgnore
  @Override
  public void setType(Topic type) {
    this.type = type;
    HGTMUtil.setTypeOf(hyperGraph, getHandle(hyperGraph, type), getHandle(hyperGraph, this));
  }


  @Override
  public int getArity() {
    return roles.size();
  }

  @Override
  public HGHandle getTargetAt(int i) {
    return roles.get(i);
  }

  @Override
  public void notifyTargetHandleUpdate(int i, HGHandle handle) {
    roles.set(i, handle);
  }

  @Override
  public void notifyTargetRemoved(int i) {
    roles.remove(i);
  }

  @Override
  protected Association createOwner() {
    final AssociationImpl association = new AssociationImpl(getTopicMapSupport().getOwner());
    association.setSupport(this);
    return association;
  }
}
