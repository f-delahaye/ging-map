package org.gingolph.gingmap.hg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.RoleImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.support.AssociationSupport;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.annotation.HGIgnore;
import org.tmapi.core.Role;

public class HGAssociationSupport extends HGScopedSupport<AssociationImpl>
    implements AssociationSupport, HGLink {

  private static final long serialVersionUID = 1L;
  transient List<HGHandle> roles;

  public HGAssociationSupport() {
    roles = new ArrayList<>();
  }

  protected HGAssociationSupport(HGHandle[] handles) {
    this.roles = Arrays.asList(handles);
  }

  public HGTopicMapSupport getTopicMapSupport() {
    return HGTMUtil.getTopicMapOf(hyperGraph, getHandle(hyperGraph, this));
  }

  @Override
  public void addRole(RoleImpl role) {
    final HyperGraph graph = getGraph();
    roles.add(add(graph, role));
    graph.update(this);
  }

  @Override
  public void removeRole(RoleImpl role) {
    HyperGraph graph = getGraph();
    final HGHandle roleHandle = getHandle(graph, role);
    if (roleHandle != null) {
      roles.remove(roleHandle);
      graph.update(this);
      graph.remove(roleHandle, true);
    }
  }

  @Override
  public List<RoleImpl> getRoles() {
    return this.roles.stream().map(role -> (RoleImpl)((HGRoleSupport) getGraph().get(role)).getOwner()).collect(Collectors.toList());
  }

  @HGIgnore
  @Override
  public TopicImpl getReifier() {
    final HyperGraph graph = getGraph();
    HGHandle h = HGTMUtil.getReifierOf(graph, getHandle(graph, this));
    return h != null ? ((HGTopicSupport) graph.get(h)).getOwner() : null;
  }

  @HGIgnore
  @Override
  public void setReifier(TopicImpl topic) {
    final HyperGraph graph = getGraph();
    HGTMUtil.setReifierOf(graph, getHandle(graph, this),
        topic == null ? null : getHandle(graph, topic));
  }

  @HGIgnore
  @Override
  public TopicImpl getType() {
      final HGHandle thisHandle = getHandle(hyperGraph, this);
      HGHandle h = HGTMUtil.getTypeOf(hyperGraph, thisHandle);
      return h != null ? ((HGTopicSupport) hyperGraph.get(h)).getOwner() : null;
  }

  @HGIgnore
  @Override
  public void setType(TopicImpl type) {
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
  protected AssociationImpl createOwner() {
    final AssociationImpl association = new AssociationImpl(getTopicMapSupport().getOwner());
    association.setSupport(this);
    return association;
  }
  
  @Override
  public void setOwner(AssociationImpl owner) {
      this.owner = owner;
  }
    
}
