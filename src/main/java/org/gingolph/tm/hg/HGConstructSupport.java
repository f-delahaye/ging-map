package org.gingolph.tm.hg;

import java.io.Serializable;
import java.util.Set;
import org.gingolph.tm.ConstructSupport;
import org.gingolph.tm.IdentifiedConstruct;
import org.hypergraphdb.HGGraphHolder;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.annotation.HGIgnore;
import org.hypergraphdb.atom.HGRel;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;


public abstract class HGConstructSupport<T extends Construct>
    implements ConstructSupport, HGGraphHolder, Serializable {

  private static final long serialVersionUID = 1L;

  @HGIgnore
  T owner;

  @HGIgnore
  transient HyperGraph hyperGraph;

  protected HGConstructSupport(T owner) {
    this.owner = owner;
  }

  protected HGConstructSupport() {}

  protected abstract T createOwner();

  public T getOwner() {
    if (owner == null) {
      owner = createOwner();
    }
    return owner;
  }

  protected void addItemIdentifier(HGHandle locator) throws ModelConstraintException {
    HyperGraph graph = getGraph();
    graph.add(new HGRel(HGTM.ItemIdentifier, new HGHandle[] {locator, graph.getHandle(this)}),
        HGTM.hItemIdentifier);
    // }
  }

  @Override
  public void addItemIdentifier(Locator l) throws ModelConstraintException {
    HyperGraph graph = getGraph();
    HGHandle lh = HGTMUtil.ensureLocatorHandle(graph, l);
    addItemIdentifier(lh);
  }

  @Override
  public Set<Locator> getItemIdentifiers() {
    HyperGraph graph = getGraph();
    final HGHandle handle = graph.getHandle(this);
    return handle == null ? null
        : HGTMUtil.getRelatedObjects(graph, HGTM.hItemIdentifier, null, handle);
  }

  @Override
  public void removeItemIdentifier(Locator l) {
    HGHandle lh = HGTMUtil.findLocatorHandle(getGraph(), l);
    if (lh != null) {
      HyperGraph graph = getGraph();
      HGHandle rel = hg.findOne(graph,
          hg.and(hg.type(HGTM.hItemIdentifier), hg.orderedLink(lh, graph.getHandle(this))));
      if (rel != null) {
        graph.remove(rel);
      }
      // If this locator is not used in anything else, we may remove it.
      if (graph.getIncidenceSet(lh).size() == 0) {
        graph.remove(lh, false);
      }
    }
  }

  public HyperGraph getGraph() {
    // if (hyperGraph == null) {
    // hyperGraph =
    // ((HGTopicMapSupport)((TopicMapImpl)owner.getTopicMap()).getSupport()).getGraph();
    // }
    return hyperGraph;
  }

  @Override
  public void setHyperGraph(HyperGraph graph) {
    this.hyperGraph = graph;
  }

  public static HGHandle getHandle(HyperGraph graph, Construct construct) {
    return construct == null ? null
        : graph.getHandle(((IdentifiedConstruct) construct).getSupport());
  }

  public static HGHandle getHandle(HyperGraph graph, ConstructSupport support) {
    return graph.getHandle(support);
  }

  public static HGHandle add(HyperGraph graph, Construct construct) {
    return add(graph, ((IdentifiedConstruct) construct).getSupport());
  }

  public static HGHandle add(HyperGraph graph, ConstructSupport support) {
    return graph.add(support);
  }
}
