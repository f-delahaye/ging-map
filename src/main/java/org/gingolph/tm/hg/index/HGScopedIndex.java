package org.gingolph.tm.hg.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.TopicSupport;
import org.gingolph.tm.UnmodifiableArraySet;
import org.gingolph.tm.hg.HGAssociationSupport;
import org.gingolph.tm.hg.HGNameSupport;
import org.gingolph.tm.hg.HGOccurrenceSupport;
import org.gingolph.tm.hg.HGScopedSupport;
import org.gingolph.tm.hg.HGTM;
import org.gingolph.tm.hg.HGTopicSupport;
import org.gingolph.tm.hg.HGVariantSupport;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.query.HGQueryCondition;
import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;
import org.tmapi.index.ScopedIndex;


public class HGScopedIndex extends HGAbstractIndex implements ScopedIndex {

  private final static Unscoped unscoped = new Unscoped();

  private static class Unscoped implements HGAtomPredicate, HGQueryCondition {

    @Override
    public boolean satisfies(HyperGraph graph, HGHandle handle) {
      Object atom = graph.get(handle);
      return atom instanceof HGScopedSupport
          && org.assertj.core.util.Collections.isNullOrEmpty(((HGScopedSupport) atom).getScope());
    }
  }

  public HGScopedIndex(HyperGraph graph) {
    super(graph);
  }

  protected <T extends Scoped, S extends HGScopedSupport<T>> Collection<T> getScoped(Topic theme,
      Class<S> scopedSupportClass) {
    Collection<T> scoped;
    if (theme == null) {
      // all unscoped associations
      scoped = hg.<T>findAll(graph, hg.apply(supportHandleToOwnerMapping(graph, scopedSupportClass),
          hg.and(hg.type(graph.getTypeSystem().getTypeHandle(scopedSupportClass)), unscoped)));
    } else {
      scoped = getScoped(new Topic[] {theme}, false, scopedSupportClass);
    }
    return scoped;
  }

  protected <T extends Scoped, S extends HGScopedSupport<T>> Collection<T> getScoped(Topic[] themes,
      boolean matchAll, Class<S> scopedSupportClass) {
    if (themes == null) {
      throw new IllegalArgumentException("Null scope not allowed");
    }
    Collection<T> scopedConstructs;
    HGQueryCondition relQuery = null;
    for (Topic theme : themes) {
      TopicSupport themeSupport = ((TopicImpl) theme).getSupport();
      HGHandle themeHandle = graph.getHandle(themeSupport);
      HGQueryCondition themeQuery = hg.and(hg.type(HGTM.hScopeOf), hg.incident(themeHandle),
          hg.orderedLink(hg.anyHandle(), themeHandle), linkTargetSubsumed(0, scopedSupportClass));
      if (relQuery == null) {
        relQuery = themeQuery;
      } else {
        relQuery = hg.or(relQuery, themeQuery);
      }
    }
    scopedConstructs =
        hg.findAll(graph, hg.apply(supportHandleToOwnerMapping(graph, scopedSupportClass),
            hg.apply(hg.linkProjection(0), hg.apply(hg.deref(graph), relQuery))));
    if (matchAll) {
      scopedConstructs = filterMatchAll(scopedConstructs, themes);
    }
    return scopedConstructs;
  }

  protected <T extends Scoped> Collection<T> filterMatchAll(Collection<T> scopedConstructs,
      Topic[] themes) {
    // we have collected all the scoped linked to any of the specified themes.
    // now we need to find those which are linked to all themes.
    Map<T, Long> scopedCount = new HashMap<>();
    scopedConstructs.forEach(scopedConstruct -> scopedCount.compute(scopedConstruct,
        (key, currentCount) -> currentCount == null ? 1 : ++currentCount));
    // TODO: try another solution that does not rely on comparing count with themes.length ... not
    // very reliable as it depends on whether we use a set or not, or whether a given contrutct may
    // have multiple times the same theme.
    scopedConstructs =
        scopedCount.entrySet().stream().filter(entry -> entry.getValue() == themes.length)
            .map(entry -> entry.getKey()).collect(Collectors.toList());
    return scopedConstructs;
  }

  protected <S extends HGScopedSupport<?>> Collection<Topic> getScopedThemes(
      Class<S> scopedSupportClass) {
    HGQueryCondition relQuery =
        hg.and(hg.type(HGTM.hScopeOf), linkTargetSubsumed(0, scopedSupportClass));
    List<HGHandle> themeSupports = hg.findAll(graph, relQuery);
    return themeSupports.stream().map(handle -> graph.<HGLink>get(handle))
        .map(link -> graph.<HGTopicSupport>get(link.getTargetAt(1)).getOwner())
        .collect(Collectors.toList());
  }

  @Override
  public Collection<Association> getAssociations(Topic theme) {
    return Collections.unmodifiableCollection(getScoped(theme, HGAssociationSupport.class));
  }

  @Override
  public Collection<Association> getAssociations(Topic[] themes, boolean matchAll) {
    return Collections.unmodifiableCollection(getScoped(themes, matchAll, HGAssociationSupport.class));
  }

  @Override
  public Collection<Topic> getAssociationThemes() {
    return getScopedThemes(HGAssociationSupport.class);
  }

  @Override
  public Collection<Occurrence> getOccurrences(Topic theme) {
    return getScoped(theme, HGOccurrenceSupport.class);
  }

  @Override
  public Collection<Occurrence> getOccurrences(Topic[] themes, boolean matchAll) {
    return getScoped(themes, matchAll, HGOccurrenceSupport.class);
  }

  @Override
  public Collection<Topic> getOccurrenceThemes() {
    return getScopedThemes(HGOccurrenceSupport.class);
  }

  @Override
  public Collection<Name> getNames(Topic theme) {
    return getScoped(theme, HGNameSupport.class);
  }

  @Override
  public Collection<Name> getNames(Topic[] themes, boolean matchAll) {
    return getScoped(themes, matchAll, HGNameSupport.class);
  }

  @Override
  public Collection<Topic> getNameThemes() {
    return getScopedThemes(HGNameSupport.class);
  }

  @Override
  public Collection<Variant> getVariants(Topic theme) {
    if (theme == null) {
      throw new IllegalArgumentException("Null scope not allowed");
    }
    List<Variant> allVariants = new ArrayList<>();
    allVariants.addAll(getScoped(theme, HGVariantSupport.class));
    allVariants.addAll(getScoped(theme, HGNameSupport.class).stream()
        .flatMap(name -> name.getVariants().stream()).collect(Collectors.toList()));
    return new UnmodifiableArraySet<>(allVariants);
  }

  @Override
  public Collection<Variant> getVariants(Topic[] themes, boolean matchAll) {
    Collection<Variant> allVariants = new ArrayList<>();
    allVariants.addAll(getScoped(themes, false, HGVariantSupport.class));
    allVariants.addAll(getScoped(themes, false, HGNameSupport.class).stream()
        .flatMap(name -> name.getVariants().stream()).collect(Collectors.toList()));
    return matchAll ? filterMatchAll(allVariants, themes) : allVariants;
  }

  @Override
  public Collection<Topic> getVariantThemes() {
    List<Topic> allThemes = new ArrayList<>();
    allThemes.addAll(getScopedThemes(HGVariantSupport.class));
    allThemes.addAll(getScopedThemes(HGNameSupport.class));
    return new UnmodifiableArraySet<>(allThemes);
  }
}
