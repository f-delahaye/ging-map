package org.gingolph.tm.hg;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import org.gingolph.tm.AbstractTopicMapSystemFactory;
import org.gingolph.tm.AssociationSupport;
import org.gingolph.tm.LocatorImpl;
import org.gingolph.tm.NameSupport;
import org.gingolph.tm.OccurrenceSupport;
import org.gingolph.tm.RoleSupport;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.TopicMapImpl;
import org.gingolph.tm.TopicMapSupport;
import org.gingolph.tm.TopicMapSystemSupport;
import org.gingolph.tm.TopicSupport;
import org.gingolph.tm.VariantSupport;
import org.hypergraphdb.HGEnvironment;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HGTypeSystem;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.atom.HGRelType;
import org.hypergraphdb.indexing.ByPartIndexer;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;


public class HGTopicMapSystemFactory extends AbstractTopicMapSystemFactory implements TopicMapSystemSupport, Serializable  {

    public static final String INFERRED_TYPES_RESOURCE = "/org/gingolph/tm/hg/inferredTypes.properties";

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                HyperGraph graph = getHypergraphInstance();
                if (graph != null) {
                    graph.close();
                }                
            }
        });        
    }
    
    public HGTopicMapSystemFactory() {
        features.put(AUTOMERGE, Boolean.FALSE);
        features.put(MODEL, Boolean.FALSE);
        features.put(MERGE, Boolean.FALSE);
        features.put(NOTATION, Boolean.FALSE);
        features.put(READONLY, Boolean.FALSE);
        features.put(TYPE_INSTANCE_AS_ASSOCIATIONS, Boolean.FALSE);        
    }
    
    @Override
    public NameSupport createNameSupport(Name name) {
        return new HGNameSupport(name);
    }

    @Override
    public AssociationSupport createAssociationSupport(Association association) {
        return new HGAssociationSupport(association);
    }

    @Override
    public OccurrenceSupport createOccurrenceSupport(Occurrence occurrence) {
        return new HGOccurrenceSupport(occurrence);
    }

    @Override
    public VariantSupport createVariantSupport(Variant variant) {
        return new HGVariantSupport(variant);
    }

    @Override
    public RoleSupport createRoleSupport(Role role) {
        return new HGRoleSupport(role);
    }    

    @Override
    public TopicSupport createTopicSupport(TopicImpl topic) {
        return new HGTopicSupport(topic);
    }
    
    @Override
    public TopicMapSupport createTopicMapSupport(TopicMap topicMap) {
        HyperGraph graph = getHypergraphInstance();        
        final HGTopicMapSupport topicMapSupport = new HGTopicMapSupport((TopicMapImpl)topicMap, graph, this);
        graph.add(topicMapSupport);
        return topicMapSupport;
    }

    protected static HyperGraph getHypergraphInstance() {
        String path = getTopicMapPath();        
        boolean exists = HGEnvironment.exists(path);
        HyperGraph graph = HGEnvironment.get(path);
        if (!exists) {
            defineTypes(graph);
        }
        return graph;
    }

    protected static String getTopicMapPath() {
//        final String path = Integer.toString(locator.hashCode())+"/"+locator.getReference().replaceAll("[^A-Za-z0-9]", "");
//        return path;
        return "topicmaps";
    }

    @Override
    protected TopicMapSystemSupport getTopicMapSystemSupport() {
        return this;
    }
    

    private static void defineTypes(HyperGraph graph) {
        HGTypeSystem ts = graph.getTypeSystem();
        graph.getTransactionManager().beginTransaction();
        try {
            defineTypeClasses(graph, INFERRED_TYPES_RESOURCE);

            HGHandle[] locatorTopicTypes
                    = new HGHandle[]{ts.getTypeHandle(Locator.class), ts.getTypeHandle(HGTopicSupport.class)};
//            HGHandle[] locatorItemTypes
//                    = new HGHandle[]{ts.getTypeHandle(Locator.class), ts.getTypeHandle(HGConstructSupport.class)};
            HGHandle[] topicTypes
                    = new HGHandle[]{ts.getTypeHandle(HGTopicSupport.class), ts.getTypeHandle(HGConstructSupport.class)};
            HGHandle[] scopedTopicTypes
                    = new HGHandle[]{ts.getTypeHandle(HGScopedSupport.class), ts.getTypeHandle(HGTopicSupport.class)};
            HGHandle[] occurrenceTopicTypes
                    = new HGHandle[]{ts.getTypeHandle(HGOccurrenceSupport.class), ts.getTypeHandle(HGTopicSupport.class)};
            HGHandle[] nameTopicTypes
                    = new HGHandle[]{ts.getTypeHandle(HGNameSupport.class), ts.getTypeHandle(HGTopicSupport.class)};
            HGHandle[] variantNameTypes
                    = new HGHandle[]{ts.getTypeHandle(HGVariantSupport.class), ts.getTypeHandle(HGNameSupport.class)};
            HGHandle[] mapMemberTypes
                    = new HGHandle[]{ts.getTypeHandle(HGConstructSupport.class), ts.getTypeHandle(HGTopicMapSupport.class)};

            graph.define(HGTM.hSubjectIdentifier, new HGRelType(HGTM.SubjectIdentifier, locatorTopicTypes));
            graph.define(HGTM.hSubjectLocator, new HGRelType(HGTM.SubjectLocator, locatorTopicTypes));
            graph.define(HGTM.hItemIdentifier, new HGRelType(HGTM.ItemIdentifier, locatorTopicTypes));
//            graph.define(HGTM.hSourceLocator, new HGRelType(HGTM.SourceLocator), locatorItemTypes);
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
                HGPersistentHandle handle = graph.getHandleFactory().makeHandle(e.getValue().toString().trim());
                graph.getTypeSystem().defineTypeAtom(handle, clazz);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static void defineIndexes(HyperGraph graph) {
        HGHandle locatorType = graph.getTypeSystem().getTypeHandle(LocatorImpl.class);
        graph.getIndexManager().register(new ByPartIndexer(locatorType, "reference"));
        
        HGHandle nameType = graph.getTypeSystem().getTypeHandle(HGNameSupport.class);
        graph.getIndexManager().register(new ByPartIndexer(nameType, "value"));

        HGHandle variantType = graph.getTypeSystem().getTypeHandle(HGVariantSupport.class);
        graph.getIndexManager().register(new ByPartIndexer(variantType, "value"));
        
        HGHandle occurrenceType = graph.getTypeSystem().getTypeHandle(HGOccurrenceSupport.class);
        graph.getIndexManager().register(new ByPartIndexer(occurrenceType, "value"));
    }
    
    
    @Override
    public void removeTopicMap(TopicMap topicMap) {
        HyperGraph graph = getHypergraphInstance();
//        graph.remove(HGTMUtil.findLocatorHandle(graph, topicMap.getLocator()));
        graph.remove(HGConstructSupport.getHandle(graph, topicMap));
    }
    
    @Override
    public void close(TopicMap topic) {
    }
    
    @Override
    public void close() {
    }
}
