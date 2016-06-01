package org.gingolph.tm.hg;

import org.gingolph.tm.AssociationImpl;
import org.gingolph.tm.RoleImpl;
import org.gingolph.tm.RoleSupport;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.TopicMapImpl;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.annotation.HGIgnore;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

/**
 * 
 * <p>
 * Implements a topic map association role. An association role is implemented as an ordered link
 * between three items (in that order): the role player, the role type and the parent association.
 * </p>
 * 
 * @author Borislav Iordanov
 *
 */
public class HGRoleSupport extends HGConstructSupport<Role> implements RoleSupport, HGLink {
  private transient final HGHandle[] targetSet;

  public HGRoleSupport(Role owner) {
    this(owner, new HGHandle[3]);
  }

  private HGRoleSupport(Role owner, HGHandle[] targetSet) {
    super(owner);
    this.targetSet = targetSet;
    // HGRoleSupport does not have the same needs as other constructs:
    // usually graph is injected to Constructs when they get persisted (by virtue of them
    // implementing HoldingContainer).
    // However, in order to persist a HGRoleConstruct, we need a graph to lookup handles of player
    // and type, so it has to be provided externally.
    setHyperGraph(
        ((HGTopicMapSupport) ((TopicMapImpl) owner.getTopicMap()).getSupport()).getGraph());
    targetSet[2] = getHandle(getGraph(), owner.getParent());
  }

  @Override
  protected Role createOwner() {
    HGAssociationSupport parent = hyperGraph.get(targetSet[2]);
    RoleImpl role =
        new RoleImpl(parent.getTopicMapSupport().getOwner(), (AssociationImpl) parent.getOwner());
    role.setSupport(this);
    return role;
  }

  @HGIgnore
  @Override
  public Topic getPlayer() {
    final HGHandle playerHandle = targetSet[0];
    return playerHandle == null ? null : ((HGTopicSupport) getGraph().get(playerHandle)).getOwner();
  }

  @Override
  public void setPlayer(TopicImpl player) {
    final HyperGraph graph = getGraph();
    targetSet[0] = getHandle(graph, player);
    HGHandle thisHandle = graph.getHandle(this);
    if (thisHandle != null) {
      // If setPlayer is called when the role is being created, it won't be in the graph (yet) and
      // therefore update will fail.
      // If setPlayer is called to update an existing role, on the other hand, we need to let the
      // graph know.
      graph.replace(thisHandle, this);
    }
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
  public void setReifier(Topic t) {
    final HyperGraph graph = getGraph();
    HGTMUtil.setReifierOf(graph, getHandle(graph, this), getHandle(graph, t));
  }

  @HGIgnore
  @Override
  public Topic getType() {
    final HGHandle typeHandle = targetSet[1];
    return typeHandle == null ? null : ((HGTopicSupport) getGraph().get(typeHandle)).getOwner();
  }

  @Override
  public void setType(Topic type) {
    targetSet[1] = getHandle(getGraph(), type);
  }

  @Override
  public int getArity() {
    return targetSet.length;
  }

  @Override
  public HGHandle getTargetAt(int i) {
    return targetSet[i];
  }

  @Override
  public void notifyTargetHandleUpdate(int i, HGHandle handle) {
    targetSet[i] = handle;
  }

  @Override
  public void notifyTargetRemoved(int i) {
    throw new IllegalArgumentException("Illegal attempt to remove a HGAssociationRole target.");
  }
}
