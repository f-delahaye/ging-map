package org.gingolph.tm.hg;

import static org.gingolph.tm.hg.HGConstructSupport.getHandle;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.gingolph.tm.LocatorImpl;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGLink;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.atom.HGRel;
import org.hypergraphdb.query.HGQueryCondition;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;

class HGTMUtil {

  // public static HGHandleFactory HANDLE_FACTORY = new UUIDHandleFactory();

  static String handleIRI(String iri) {
    return iri;
  }

  static HGTopicMapSupport getTopicMapOf(HyperGraph graph, HGHandle topicMapItemHandle) {
    HGHandle rel = hg.findOne(graph, hg.and(hg.type(HGTM.hMapMember),
        hg.incident(topicMapItemHandle), hg.orderedLink(topicMapItemHandle, hg.anyHandle())));
    if (rel == null) {
      return null;
    } else {
      return (HGTopicMapSupport) graph.get(((HGLink) graph.get(rel)).getTargetAt(1));
    }
  }


  static void setTopicMapOf(HyperGraph graph, HGHandle topicMapItemHandle,
      HGHandle topicMapHandle) {
    graph.add(new HGRel(HGTM.MapMember, new HGHandle[] {topicMapItemHandle, topicMapHandle}),
        HGTM.hMapMember);
  }

  static <C extends Construct, T extends HGConstructSupport<C>> List<C> findTopicMapItems(
      HyperGraph graph, Class<T> topicMapItemClass, HGHandle topicMapHandle) {
    // Stated in English, the query finds all atoms of type HGAssociation
    // that are the 1st element of a link of type HGTM.hMapMember which
    // also point to the atom 'mapHandle'
    Collection<HGHandle> handles = graph.findAll(hg.and(hg.type(topicMapItemClass), hg.apply(
        hg.linkProjection(0),
        hg.apply(hg.deref(graph), hg.and(hg.type(HGTM.hMapMember), hg.incident(topicMapHandle))))));
    return handles.stream().map(handle -> graph.<T>get(handle)).map(support -> support.getOwner())
        .collect(Collectors.toList());
  }

  private static HGHandle findLocatorHandle(HyperGraph graph, String uri) {
    return hg.findOne(graph, hg.and(hg.type(LocatorImpl.class), hg.eq("reference", uri)));
  }

  static HGHandle findLocatorHandle(HyperGraph graph, Locator locator) {
    HGHandle h = graph.getHandle(locator);
    if (h == null) {
      h = findLocatorHandle(graph, locator.getReference());
    }
    return h;
  }

  static HGHandle ensureLocatorHandle(HyperGraph graph, Locator locator) {
    HGHandle h = findLocatorHandle(graph, locator);
    if (h == null) {
      h = graph.add(locator);
    }
    return h;
  }

  static HGHandle getTypeOf(HyperGraph graph, HGHandle typedHandle) {
    HGHandle rel = hg.findOne(graph, hg.and(hg.type(HGTM.hTypeOf), hg.incident(typedHandle),
        hg.orderedLink(hg.anyHandle(), typedHandle)));
    if (rel == null) {
      return null;
    } else {
      return ((HGLink) graph.get(rel)).getTargetAt(0);
    }
  }

  static void setTypeOf(HyperGraph graph, HGHandle type, HGHandle typed) {
    HGHandle rel = hg.findOne(graph,
        hg.and(hg.type(HGTM.hTypeOf), hg.incident(typed), hg.orderedLink(hg.anyHandle(), typed)));
    if (rel != null) {
      graph.remove(rel);
    }
    if (type != null) {
      graph.add(new HGRel(HGTM.TypeOf, new HGHandle[] {type, typed}), HGTM.hTypeOf);
    }
  }

  static HGHandle getReifierOf(HyperGraph graph, HGHandle h) {
    if (h == null) {
      return null;
    }
    HGHandle rel = hg.findOne(graph,
        hg.and(hg.type(HGTM.hReifierOf), hg.incident(h), hg.orderedLink(hg.anyHandle(), h)));
    if (rel == null) {
      return null;
    } else {
      return ((HGLink) graph.get(rel)).getTargetAt(0);
    }
  }

  static void setReifierOf(HyperGraph graph, HGHandle reified, HGHandle reifier) {
    HGHandle rel = hg.findOne(graph, hg.and(hg.type(HGTM.hReifierOf), hg.incident(reified),
        hg.orderedLink(hg.anyHandle(), reified)));
    if (rel != null) {
      graph.remove(rel);
    }
    if (reifier != null) {
      graph.add(new HGRel(HGTM.ReifierOf, new HGHandle[] {reifier, reified}), HGTM.hReifierOf);
    }
  }

  static <C extends Construct> List<C> getRelatedObjects(HGConstructSupport<?> support,
      HGHandle relType, boolean supportAsFirstArgument) {
    HyperGraph graph = support.getGraph();
    HGHandle supportHandle = getHandle(graph, support);
    if (supportHandle == null) {
      return null;
    }
    List<HGConstructSupport<C>> relatedSupports =
        HGTMUtil.getRelatedObjects(graph, relType, supportAsFirstArgument ? supportHandle : null,
            supportAsFirstArgument ? null : supportHandle);
    return relatedSupports.stream().map(relatedSupport -> relatedSupport.getOwner())
        .collect(Collectors.toList());
  }

  /**
   * Get all 'first' related to a given 'second' in an ordered link or, vice-versa, all 'second'
   * related to a given 'first' in an ordered link. Whether first or second is required is indicated
   * by putting null in the corresponding parameter.
   */
  static <T> List<T> getRelatedObjects(HyperGraph graph, HGHandle relType, HGHandle first,
      HGHandle second) {
    HGQueryCondition relQuery = hg.and(hg.type(relType),
        hg.incident(first == null ? second : first), hg.orderedLink(new HGHandle[] {
            first == null ? hg.anyHandle() : first, second == null ? hg.anyHandle() : second}));
    int idx = (first == null ? 0 : 1);
    return hg.findAll(graph, hg.apply(hg.deref(graph),
        hg.apply(hg.linkProjection(idx), hg.apply(hg.deref(graph), relQuery))));
  }

  static void removeRelations(HyperGraph graph, HGHandle relType, HGHandle first, HGHandle second) {
    Collection<HGHandle> relations = graph.findAll(hg.and(hg.type(relType),
        hg.incident(first == null ? second : first), hg.orderedLink(new HGHandle[] {
            first == null ? hg.anyHandle() : first, second == null ? hg.anyHandle() : second})));
    for (HGHandle h : relations) {
      graph.remove(h, false);
    }
  }

  /**
   * Get the 'first' related to a given 'second' in an ordered link or, vice-versa, the 'second'
   * related to a given 'first' in an ordered link. Whether first or second is required is indicated
   * by putting null in the corresponding parameter.
   */
  static <T> T getOneRelated(HyperGraph graph, HGHandle relType, HGHandle first, HGHandle second) {
    HGHandle h = hg.findOne(graph,
        hg.and(hg.type(relType), hg.incident(first == null ? second : first),
            hg.orderedLink(new HGHandle[] {first == null ? hg.anyHandle() : first,
                second == null ? hg.anyHandle() : second})));
    int idx = (first == null ? 0 : 1);
    if (h != null) {
      return graph.get(((HGLink) graph.get(h)).getTargetAt(idx));
    } else {
      return null;
    }
  }

  static void detachFromMap(HyperGraph graph, HGHandle topicMapItemToDetach) {
    List<HGHandle> all =
        hg.findAll(graph, hg.and(hg.type(HGTM.hMapMember), hg.incident(topicMapItemToDetach),
            hg.orderedLink(new HGHandle[] {topicMapItemToDetach, hg.anyHandle()})));
    all.stream().forEach((h) -> {
      graph.remove(h);
    });
  }
}
