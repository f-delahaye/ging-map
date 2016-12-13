package org.gingolph.tm.hg;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.gingolph.tm.AbstractConstruct;
import org.gingolph.tm.ConstructSupport;
import org.gingolph.tm.TopicMapImpl;
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

  // Owner used to be passed in the constructor.
  // Its now set directly by Topic/association/role support when their callback method setOwner is invoked
  @HGIgnore
  transient T owner;

  @HGIgnore
  transient HyperGraph hyperGraph;

  protected HGConstructSupport() {}

  protected abstract T createOwner();

  public T getOwner() {
    if (owner == null) {
      owner = createOwner();
    }
    return owner;
  }

  protected final void addItemIdentifier(HGHandle locator) throws ModelConstraintException {
    HyperGraph graph = getGraph();
    graph.add(new HGRel(HGTM.ItemIdentifier, new HGHandle[] {locator, graph.getHandle(this)}),
        HGTM.hItemIdentifier);
    // }
  }

  @Override
  public final void addItemIdentifier(Locator l) throws ModelConstraintException {
    HyperGraph graph = getGraph();
    HGHandle lh = HGTMUtil.ensureLocatorHandle(graph, l);
    addItemIdentifier(lh);
  }

  @Override
  public final Set<Locator> getItemIdentifiers() {
    HyperGraph graph = getGraph();
    final HGHandle handle = graph.getHandle(this);
    return handle == null ? null
        : new HashSet<>(HGTMUtil.getRelatedObjects(graph, HGTM.hItemIdentifier, null, handle));
  }

  @Override
  public final void removeItemIdentifier(Locator l) {
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
     if (hyperGraph == null) {
     hyperGraph =
     ((HGTopicMapSupport)((TopicMapImpl)getOwner().getTopicMap()).getSupport()).getGraph();
     }
    return hyperGraph;
  }

  @Override
  public void setHyperGraph(HyperGraph graph) {
    this.hyperGraph = graph;
  }

  public static HGHandle getHandle(HyperGraph graph, Construct construct) {
    return construct == null ? null
        : graph.getHandle(((AbstractConstruct<?>) construct).getSupport());
  }

  public static HGHandle getHandle(HyperGraph graph, ConstructSupport support) {
    return graph.getHandle(support);
  }

  public static HGHandle add(HyperGraph graph, Construct construct) {
    return add(graph, ((AbstractConstruct<?>) construct).getSupport());
  }

  public static HGHandle add(HyperGraph graph, ConstructSupport support) {
    return graph.add(support);
  }
}
