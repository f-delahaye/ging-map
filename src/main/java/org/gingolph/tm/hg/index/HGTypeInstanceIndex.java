package org.gingolph.tm.hg.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.gingolph.tm.ArraySet;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.TopicSupport;
import org.gingolph.tm.TypedSupport;
import org.gingolph.tm.hg.HGAssociationSupport;
import org.gingolph.tm.hg.HGConstructSupport;
import org.gingolph.tm.hg.HGNameSupport;
import org.gingolph.tm.hg.HGOccurrenceSupport;
import org.gingolph.tm.hg.HGRoleSupport;
import org.gingolph.tm.hg.HGTM;
import org.gingolph.tm.hg.HGTopicSupport;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.HGAtomPredicate;
import org.hypergraphdb.query.HGQueryCondition;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.index.TypeInstanceIndex;


public class HGTypeInstanceIndex extends HGAbstractIndex implements TypeInstanceIndex {
  // HGTMUtil.getType / setType is defined as {type, typed}
  private final static Untyped untyped = new Untyped();

  private static class Untyped implements HGAtomPredicate, HGQueryCondition {

    @Override
    public boolean satisfies(HyperGraph graph, HGHandle handle) {
      Object atom = graph.get(handle);
      return atom instanceof TopicSupport
          ? org.assertj.core.util.Collections.isNullOrEmpty(((TopicSupport) atom).getTypes())
          : atom instanceof TypedSupport ? ((TypedSupport) atom).getType() == null : false;
    }
  }

  protected <T extends Construct> Collection<T> getInstances(Topic type,
      Class<? extends HGConstructSupport<T>> instanceSupportClass, HGHandle typeHandle) {
    Collection<T> typed;
    if (type == null) {
      // all unscoped associations
      typed =
          hg.<T>findAll(graph, hg.apply(supportHandleToOwnerMapping(graph, instanceSupportClass),
              hg.and(hg.type(graph.getTypeSystem().getTypeHandle(instanceSupportClass)), untyped)));
    } else {
      typed = getInstances(new Topic[] {type}, false, instanceSupportClass, typeHandle);
    }
    return typed;
  }

  protected <T extends Construct> Set<T> getInstances(Topic[] types, boolean matchAll,
      Class<? extends HGConstructSupport<T>> instanceSupportClass, HGHandle typeHandle) {
    if (types == null) {
      throw new IllegalArgumentException("Null types not allowed");
    }
    Collection<T> typedConstructs;
    HGQueryCondition relQuery = null;
    for (Topic type : types) {
      TopicSupport typedSupport = ((TopicImpl) type).getSupport();
      HGHandle typedHandle = graph.getHandle(typedSupport);
      HGQueryCondition typeQuery = hg.and(hg.type(typeHandle), hg.incident(typedHandle),
          hg.orderedLink(typedHandle, hg.anyHandle()), linkTargetSubsumed(1, instanceSupportClass));
      if (relQuery == null) {
        relQuery = typeQuery;
      } else {
        relQuery = hg.or(relQuery, typeQuery);
      }
    }
    typedConstructs =
        hg.findAll(graph, hg.apply(supportHandleToOwnerMapping(graph, instanceSupportClass),
            hg.apply(hg.linkProjection(1), hg.apply(hg.deref(graph), relQuery))));
    return matchAll ? filterMatchAll(typedConstructs, types) : new ArraySet<>(typedConstructs, Objects::equals);
  }

  protected <T extends Construct> Set<T> filterMatchAll(Collection<T> typedConstructs,
      Topic[] types) {
    // we have collected all the scoped linked to any of the specified themes.
    // now we need to find those which are linked to all themes.
    Map<T, Long> scopedCount = new HashMap<>();
    typedConstructs.forEach(scopedConstruct -> scopedCount.compute(scopedConstruct,
        (key, currentCount) -> currentCount == null ? 1 : ++currentCount));
    // TODO: try another solution that does not rely on comparing count with themes.length ... not
    // very reliable as it depends on whether we use a set or not, or whether a given contrutct may
    // have multiple times the same theme.
    return new ArraySet<>(scopedCount.entrySet().stream().filter(entry -> entry.getValue() == types.length)
        .map(entry -> entry.getKey()).collect(Collectors.toList()), Objects::equals);
  }

  protected Collection<Topic> getTypes(
      Class<? extends HGConstructSupport<? extends Construct>> typedSupportClass,
      HGHandle typeHandle) {
    HGQueryCondition query = hg.and(hg.type(typeHandle), linkTargetSubsumed(1, typedSupportClass));
    return hg.<HGHandle>findAll(graph, query).stream().map(handle -> graph.<HGLink>get(handle))
        .map(link -> graph.<HGTopicSupport>get(link.getTargetAt(0)).getOwner())
        .collect(Collectors.toList());
  }

  public HGTypeInstanceIndex(HyperGraph graph) {
    super(graph);
  }


  @Override
  public Collection<Topic> getTopics(Topic type) {
    return Collections
        .unmodifiableCollection(getInstances(type, HGTopicSupport.class, HGTM.hTypeOf));
  }

  @Override
  public Collection<Topic> getTopics(Topic[] types, boolean matchAll) {
    return Collections
        .unmodifiableCollection(getInstances(types, matchAll, HGTopicSupport.class, HGTM.hTypeOf));
  }

  @Override
  public Collection<Topic> getTopicTypes() {
    return getTypes(HGTopicSupport.class, HGTM.hTypeOf);
  }

  @Override
  public Collection<Association> getAssociations(Topic type) {
    return getInstances(type, HGAssociationSupport.class, HGTM.hTypeOf);
  }

  @Override
  public Collection<Topic> getAssociationTypes() {
    return getTypes(HGAssociationSupport.class, HGTM.hTypeOf);
  }

  @Override
  public Collection<Role> getRoles(Topic type) {
    HGHandle roleSupportTypeHandle = graph.getTypeSystem().getTypeHandle(HGRoleSupport.class);
    HGQueryCondition query = hg.and(hg.type(roleSupportTypeHandle));
    return hg.<HGHandle>findAll(graph, query).stream()
        .map(roleSupportHandle -> graph.get(roleSupportHandle))
        .map(roleSupport -> (HGRoleSupport) roleSupport)
        .filter(roleSupport -> roleSupport.getType().equals(type))
        .map(roleSupport -> roleSupport.getOwner()).collect(Collectors.toList());
  }

  @Override
  public Collection<Topic> getRoleTypes() {
    HGHandle roleTypeHandle = graph.getTypeSystem().getTypeHandle(HGRoleSupport.class);
    HGQueryCondition query = hg.and(hg.type(roleTypeHandle));
    return hg.<HGHandle>findAll(graph, query).stream()
        .map(handle -> graph.<HGRoleSupport>get(handle)).map(role -> role.getType())
        .collect(Collectors.toList());
  }

  @Override
  public Collection<Occurrence> getOccurrences(Topic type) {
    return getInstances(type, HGOccurrenceSupport.class, HGTM.hTypeOf);
  }

  @Override
  public Collection<Topic> getOccurrenceTypes() {
    return getTypes(HGOccurrenceSupport.class, HGTM.hTypeOf);
  }

  @Override
  public Collection<Name> getNames(Topic type) {
    return getInstances(type, HGNameSupport.class, HGTM.hTypeOf);
  }

  @Override
  public Collection<Topic> getNameTypes() {
    return getTypes(HGNameSupport.class, HGTM.hTypeOf);
  }
}
