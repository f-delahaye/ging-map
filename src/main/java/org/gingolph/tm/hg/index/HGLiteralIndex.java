package org.gingolph.tm.hg.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.gingolph.tm.LocatorImpl;
import org.gingolph.tm.hg.HGConstructSupport;
import org.gingolph.tm.hg.HGNameSupport;
import org.gingolph.tm.hg.HGOccurrenceSupport;
import org.gingolph.tm.hg.HGVariantSupport;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.indexing.HGIndexer;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;
import org.tmapi.index.LiteralIndex;


public class HGLiteralIndex extends HGAbstractIndex implements LiteralIndex {

  public HGLiteralIndex(HyperGraph graph) {
    super(graph);

    List<HGIndexer<?, ?>> indexers = new ArrayList<>();

    HGHandle nameType = graph.getTypeSystem().getTypeHandle(HGNameSupport.class);
    indexers.addAll(graph.getIndexManager().getIndexersForType(nameType));

    HGHandle occurrenceType = graph.getTypeSystem().getTypeHandle(HGOccurrenceSupport.class);
    indexers.addAll(graph.getIndexManager().getIndexersForType(occurrenceType));

    HGHandle variantType = graph.getTypeSystem().getTypeHandle(HGVariantSupport.class);
    indexers.addAll(graph.getIndexManager().getIndexersForType(variantType));

    indexers.forEach(indexer -> indexes.add(graph.getIndexManager().getIndex(indexer)));
  }

  @Override
  public Collection<Occurrence> getOccurrences(String value) {
    return getOccurrences(value, LocatorImpl.XSD_STRING);
  }

  @Override
  public Collection<Occurrence> getOccurrences(Locator value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not supported");
    }
    return getOccurrences(value.getReference(), LocatorImpl.XSD_ANY_URI);
  }

  @Override
  public Collection<Occurrence> getOccurrences(String value, Locator datatype) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not supported");
    }
    if (datatype == null) {
      throw new IllegalArgumentException("Null datatype not supported");
    }
    List<HGOccurrenceSupport> occurrenceSupports =
        hg.getAll(graph, hg.and(hg.type(HGOccurrenceSupport.class), hg.eq("value", value),
            hg.eq("datatype", datatype)));
    return Collections.unmodifiableCollection(getIndexResults(occurrenceSupports));
  }

  @Override
  public Collection<Variant> getVariants(String value) {
    return getVariants(value, LocatorImpl.XSD_STRING);
  }

  @Override
  public Collection<Variant> getVariants(Locator value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not supported");
    }
    return getVariants(value.getReference(), LocatorImpl.XSD_ANY_URI);
  }

  @Override
  public Collection<Variant> getVariants(String value, Locator datatype) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not supported");
    }
    if (datatype == null) {
      throw new IllegalArgumentException("Null datatype not supported");
    }
    List<HGVariantSupport> variantSupports = hg.getAll(graph, hg
        .and(hg.type(HGVariantSupport.class), hg.eq("value", value), hg.eq("datatype", datatype)));
    return Collections.unmodifiableCollection(getIndexResults(variantSupports));
  }

  @Override
  public Collection<Name> getNames(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not supported");
    }
    List<HGNameSupport> nameSupports =
        hg.getAll(graph, hg.and(hg.type(HGNameSupport.class), hg.eq("value", value)));
    return Collections.unmodifiableCollection(getIndexResults(nameSupports));
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
