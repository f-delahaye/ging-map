package org.gingolph.tm.hg;

import java.util.Set;
import java.util.stream.Collectors;

import org.gingolph.tm.IdentityHashSet;
import org.gingolph.tm.ScopedSupport;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.annotation.HGIgnore;
import org.hypergraphdb.atom.HGRel;
import org.tmapi.core.Construct;
import org.tmapi.core.Topic;

public abstract class HGScopedSupport<T extends Construct> extends HGConstructSupport<T>
    implements ScopedSupport {

  private static final long serialVersionUID = 1L;

  protected HGScopedSupport() {}

  @Override
  public void addTheme(Topic theme) {
    HyperGraph graph = getGraph();
    final HGHandle scopedHandle = getHandle(graph, this);
    final HGHandle themeHandle = getHandle(graph, theme);
    HGHandle rel = hg.findOne(graph, hg.and(hg.type(HGTM.hScopeOf), hg.incident(scopedHandle),
        hg.incident(themeHandle), hg.orderedLink(scopedHandle, themeHandle)));
    if (rel == null) {
      graph.add(new HGRel(HGTM.ScopeOf, new HGHandle[] {scopedHandle, themeHandle}), HGTM.hScopeOf);
    }
  }

  @HGIgnore
  @Override
  public Set<Topic> getScope() {
    HyperGraph graph = getGraph();
    final HGHandle handle = getHandle(graph, this);
    return handle == null ? null
        : new IdentityHashSet<>(HGTMUtil.<HGTopicSupport>getRelatedObjects(graph, HGTM.hScopeOf, handle, null).stream()
            .map(support -> support.getOwner()).collect(Collectors.toList()));
  }

  @Override
  public void removeTheme(Topic theme) {
    HyperGraph graph = getGraph();
    HGHandle rel = hg.findOne(graph, hg.and(hg.type(HGTM.hScopeOf),
        hg.orderedLink(getHandle(graph, this), getHandle(graph, theme))));
    if (rel != null) {
      graph.remove(rel);
    }
  }
}
