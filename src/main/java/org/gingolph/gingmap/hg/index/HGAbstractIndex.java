package org.gingolph.gingmap.hg.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.gingolph.gingmap.equality.Equality;
import org.gingolph.gingmap.hg.HGConstructSupport;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGIndex;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.query.HGQueryCondition;
import org.hypergraphdb.util.Mapping;
import org.tmapi.core.Construct;
import org.tmapi.index.Index;


public abstract class HGAbstractIndex implements Index {

  protected static class LinkTargetSubsumed implements HGAtomPredicate, HGQueryCondition {

    int targetIdx;
    Class<?> expectedClass;

    private LinkTargetSubsumed(int targetIdx, Class<?> expectedClass) {
      this.targetIdx = targetIdx;
      this.expectedClass = expectedClass;
    }

    @Override
    public boolean satisfies(HyperGraph graph, HGHandle handle) {
      Object atom = graph.get(handle);
      return atom instanceof HGLink && expectedClass
          .isAssignableFrom(graph.get(((HGLink) atom).getTargetAt(targetIdx)).getClass());
    }
  }

  protected static LinkTargetSubsumed linkTargetSubsumed(final int targetIdx,
      final Class<?> expectedClass) {
    return new LinkTargetSubsumed(targetIdx, expectedClass);
  }

  protected static <T extends Construct> Mapping<HGHandle, T> supportHandleToOwnerMapping(
      final HyperGraph graph, Class<? extends HGConstructSupport<T>> supportClass) {
    return (HGHandle x) -> supportClass.cast(graph.get(x)).getOwner();
  }

  boolean open = false;
  List<HGIndex<?,?>> indexes = new ArrayList<>();
  final transient HyperGraph graph;
  final transient Equality equality;

  HGAbstractIndex(HyperGraph graph, Equality equality) {
    this.graph = graph;
    this.equality = equality;
  }

  protected <C extends Construct> Collection<C> getIndexResults(
      Iterable<? extends HGConstructSupport<C>> supports) {
    List<C> target = new ArrayList<>();
    supports.forEach(support -> target.add(support.getOwner()));
    return target;
  }

  protected <C extends Construct> Collection<C> getIndexResults(
      Iterator<? extends HGHandle> supports) {
    List<C> target = new ArrayList<>();
    supports.forEachRemaining(
        handle -> target.add(graph.<HGConstructSupport<C>>get(handle).getOwner()));
    return target;
  }

  @Override
  public void open() {
    indexes.forEach(index -> index.open());
    open = true;
  }

  @Override
  public void close() {
    indexes.forEach(index -> index.close());
    open = false;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public boolean isAutoUpdated() {
    return true;
  }

  @Override
  public void reindex() {}

}
