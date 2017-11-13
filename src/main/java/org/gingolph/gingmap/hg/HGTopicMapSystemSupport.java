package org.gingolph.gingmap.hg;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.gingolph.gingmap.LocatorImpl;
import org.gingolph.gingmap.TopicMapImpl;
import org.gingolph.gingmap.support.AssociationSupport;
import org.gingolph.gingmap.support.NameSupport;
import org.gingolph.gingmap.support.OccurrenceSupport;
import org.gingolph.gingmap.support.RoleSupport;
import org.gingolph.gingmap.support.TopicMapSupport;
import org.gingolph.gingmap.support.TopicMapSystemSupport;
import org.gingolph.gingmap.support.TopicSupport;
import org.gingolph.gingmap.support.VariantSupport;
import org.hypergraphdb.HGEnvironment;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HGTypeSystem;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.atom.HGRelType;
import org.hypergraphdb.indexing.ByPartIndexer;
import org.hypergraphdb.HyperGraph;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;

/**
 * Unlike others backend stacks, which have their TopicMapSystemFactory also implement
 * TopicMapSystemSupport, HG uses a separate class so as to have a graph property that wouldn't make
 * sense in the system factory (or would it??)
 * 
 * @author frederic
 *
 */
public class HGTopicMapSystemSupport implements TopicMapSystemSupport {


  private static final String INFERRED_TYPES_RESOURCE =
      "/org/gingolph/gingmap/hg/inferredTypes.properties";
  
  // Lazily created because TestFeatureStrings doesn't extend TMAPITestCase, so the storage system property is not copied over which causes NPEs.
  // So instead we store all the relevant pp and if they are not set thats fine as long as we dont do anything with the graph.
  private HyperGraph graph;

  private final String storagePath;

  private final boolean closeOnExit;

  public HGTopicMapSystemSupport(String storagePath, boolean closeOnExit) {
    super();
    this.storagePath = storagePath;
    this.closeOnExit = closeOnExit;
  }

  HyperGraph getGraph() {
    if (graph == null) {
      graph = getHypergraphInstance();
    }
    return graph;
  }

  @Override
  public NameSupport createNameSupport() {
    return new HGNameSupport();
  }

  @Override
  public AssociationSupport createAssociationSupport() {
    return new HGAssociationSupport();
  }

  @Override
  public OccurrenceSupport createOccurrenceSupport() {
    return new HGOccurrenceSupport();
  }

  @Override
  public VariantSupport createVariantSupport() {
    return new HGVariantSupport();
  }

  @Override
  public RoleSupport createRoleSupport() {
    return new HGRoleSupport();
  }

  @Override
  public TopicSupport createTopicSupport() {
    return new HGTopicSupport();
  }

  @Override
  public TopicMapSupport createTopicMapSupport() {
    return new HGTopicMapSupport(getGraph(), this);
  }


  @Override
  public void removeTopicMap(TopicMap topicMap) {
    HGConstructSupport.remove(getGraph(), topicMap);
    HGConstructSupport.remove(getGraph(), topicMap.getLocator());
  }


  @Override
  public Locator createLocator(String value) {
    Locator locator = new LocatorImpl(value);
    getGraph().add(locator);
    return locator;
  }
    
  @Override
  public Set<LocatorImpl> getLocators() {
    List<HGTopicMapSupport> topicMaps = hg.findAll(getGraph(), hg.apply(hg.deref(getGraph()), hg.type(HGTopicMapSupport.class)));
//    for (HGHandle h : hg.<HGHandle>findAll(graph,
//        new AtomProjectionCondition("baseLocator", hg.type(HGTopicMapSupport.class)))) {
//      result.add(graph.get(h));
//    }
    return topicMaps.stream()/*.map(graph::<HGTopicMapSupport>get)*/.map(HGTopicMapSupport::getBaseLocator).collect(Collectors.toSet());
  }

  @Override
  public void addTopicMap(TopicMapImpl topicMap) {
    HGConstructSupport.add(getGraph(), topicMap);
  }

  @Override
  public TopicMap getTopicMap(Locator locator) {
    HGHandle h =
        hg.findOne(getGraph(), hg.and(hg.type(HGTopicMapSupport.class), hg.eq("baseLocator", locator)));
    if (h == null) {
      return null;
    }
    HGTopicMapSupport map = (HGTopicMapSupport) getGraph().get(h);
    map.setParent(this);
    return map.getOwner();
  }

  @Override
  public void close(TopicMap topic) {}

  @Override
  public void close() {
    if (closeOnExit) {
      getGraph().close();
    }
  }
  
  private static void defineTypes(HyperGraph graph) {
    HGTypeSystem ts = graph.getTypeSystem();
    graph.getTransactionManager().beginTransaction();
    try {
      defineTypeClasses(graph, INFERRED_TYPES_RESOURCE);

      HGHandle[] locatorTopicTypes =
          new HGHandle[] {ts.getTypeHandle(Locator.class), ts.getTypeHandle(HGTopicSupport.class)};
      // HGHandle[] locatorItemTypes
      // = new HGHandle[]{ts.getTypeHandle(Locator.class),
      // ts.getTypeHandle(HGConstructSupport.class)};
      HGHandle[] topicTypes = new HGHandle[] {ts.getTypeHandle(HGTopicSupport.class),
          ts.getTypeHandle(HGConstructSupport.class)};
      HGHandle[] scopedTopicTypes = new HGHandle[] {ts.getTypeHandle(HGScopedSupport.class),
          ts.getTypeHandle(HGTopicSupport.class)};
      HGHandle[] occurrenceTopicTypes = new HGHandle[] {ts.getTypeHandle(HGOccurrenceSupport.class),
          ts.getTypeHandle(HGTopicSupport.class)};
      HGHandle[] nameTopicTypes = new HGHandle[] {ts.getTypeHandle(HGNameSupport.class),
          ts.getTypeHandle(HGTopicSupport.class)};
      HGHandle[] variantNameTypes = new HGHandle[] {ts.getTypeHandle(HGVariantSupport.class),
          ts.getTypeHandle(HGNameSupport.class)};
      HGHandle[] mapMemberTypes = new HGHandle[] {ts.getTypeHandle(HGConstructSupport.class),
          ts.getTypeHandle(HGTopicMapSupport.class)};

      graph.define(HGTM.hSubjectIdentifier,
          new HGRelType(HGTM.SubjectIdentifier, locatorTopicTypes));
      graph.define(HGTM.hSubjectLocator, new HGRelType(HGTM.SubjectLocator, locatorTopicTypes));
      graph.define(HGTM.hItemIdentifier, new HGRelType(HGTM.ItemIdentifier, locatorTopicTypes));
      // graph.define(HGTM.hSourceLocator, new HGRelType(HGTM.SourceLocator), locatorItemTypes);
      graph.define(HGTM.hTypeOf, new HGRelType(HGTM.TypeOf, topicTypes));
      graph.define(HGTM.hScopeOf, new HGRelType(HGTM.ScopeOf, scopedTopicTypes));
      graph.define(HGTM.hOccurrenceOf, new HGRelType(HGTM.OccurenceOf, occurrenceTopicTypes));
      graph.define(HGTM.hReifierOf, new HGRelType(HGTM.ReifierOf, scopedTopicTypes));
      graph.define(HGTM.hNameOf, new HGRelType(HGTM.NameOf, nameTopicTypes));
      graph.define(HGTM.hVariantOf, new HGRelType(HGTM.VariantOf, variantNameTypes));
      graph.define(HGTM.hMapMember, new HGRelType(HGTM.MapMember, mapMemberTypes));

      defineIndexes(graph);

      graph.getTransactionManager().commit();
    } catch (RuntimeException t) {
      graph.getTransactionManager().abort();
      throw t;
    }
  }

  private static void defineTypeClasses(HyperGraph graph, String resource) {
    try (InputStream in = HGTopicMapSystemFactory.class.getResourceAsStream(resource)) {
      Properties props = new Properties();
      props.load(in);
      for (Map.Entry<Object, Object> e : props.entrySet()) {
        Class<?> clazz = Class.forName(e.getKey().toString().trim());
        HGPersistentHandle handle =
            graph.getHandleFactory().makeHandle(e.getValue().toString().trim());
        graph.getTypeSystem().defineTypeAtom(handle, graph.getTypeSystem().getSchema().toTypeURI(clazz));
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static void defineIndexes(HyperGraph graph) {
    HGHandle locatorType = graph.getTypeSystem().getTypeHandle(LocatorImpl.class);
    graph.getIndexManager().register(new ByPartIndexer<>(locatorType, "reference"));

    HGHandle nameType = graph.getTypeSystem().getTypeHandle(HGNameSupport.class);
    graph.getIndexManager().register(new ByPartIndexer<>(nameType, "value"));

    HGHandle variantType = graph.getTypeSystem().getTypeHandle(HGVariantSupport.class);
    graph.getIndexManager().register(new ByPartIndexer<>(variantType, "value"));

    HGHandle occurrenceType = graph.getTypeSystem().getTypeHandle(HGOccurrenceSupport.class);
    graph.getIndexManager().register(new ByPartIndexer<>(occurrenceType, "value"));
  }  
  

  protected  HyperGraph getHypergraphInstance() {
    boolean exists = HGEnvironment.exists(storagePath);
    HyperGraph graph = HGEnvironment.get(storagePath);
    if (!exists) {
      defineTypes(graph);
    }
    return graph;
  }
}
