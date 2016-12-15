package org.gingolph.gingmap.hg;

import java.util.List;

import org.gingolph.gingmap.AbstractConstruct;
import org.gingolph.gingmap.AbstractTopicMapSystemFactory;
import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.LocatorImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.TopicMapImpl;
import org.gingolph.gingmap.TopicMapSupport;
import org.gingolph.gingmap.TopicMapSystemImpl;
import org.gingolph.gingmap.TopicMapSystemSupport;
import org.gingolph.gingmap.hg.index.HGLiteralIndex;
import org.gingolph.gingmap.hg.index.HGScopedIndex;
import org.gingolph.gingmap.hg.index.HGTypeInstanceIndex;
import org.gingolph.gingmap.index.IdentifierIndex;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.annotation.AtomReference;
import org.hypergraphdb.annotation.HGIgnore;
import org.tmapi.core.Association;
import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Topic;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

public class HGTopicMapSupport extends HGConstructSupport<TopicMapImpl> implements TopicMapSupport {

  private static final long serialVersionUID = 1L;
  private transient TopicMapSystemSupport parent;
  @AtomReference("symbolic")
  private LocatorImpl baseLocator = null;  

  public HGTopicMapSupport() {}

  public HGTopicMapSupport(HyperGraph graph, TopicMapSystemSupport parent) {
    setHyperGraph(graph);
    this.parent = parent;
  }
  
  protected void setParent(TopicMapSystemSupport parent) {
    this.parent = parent;
  }

  @Override
  protected TopicMapImpl createOwner() {
	  HGTopicMapSystemFactory factory = new HGTopicMapSystemFactory();
    boolean autoMerge;
    try {
      autoMerge = factory.getFeature(AbstractTopicMapSystemFactory.AUTOMERGE);
    } catch (FeatureNotRecognizedException ex) {
      autoMerge = false;
    }
    TopicMapImpl topicMap = new TopicMapImpl(new TopicMapSystemImpl(factory, parent), autoMerge, parent);
    topicMap.setSupport(this);
    return topicMap;
  }

  @Override
  public void addAssociation(AssociationImpl association) {
    HyperGraph graph = getGraph();
    HGHandle associationHandle = add(graph, association);
    HGTMUtil.setTopicMapOf(hyperGraph, associationHandle, getHandle(graph, this));
  }

  @Override
  public void addTopic(TopicImpl topic) {
    HyperGraph graph = getGraph();
    HGHandle topicHandle = add(graph, topic);
    HGTMUtil.setTopicMapOf(hyperGraph, topicHandle, getHandle(graph, this));
  }

  @Override
  public LocatorImpl getBaseLocator() {
    return baseLocator;
  }

  @Override
  public void setBaseLocator(LocatorImpl locator) {
    this.baseLocator = locator;
  }

  @Override
  public List<AssociationImpl> getAssociations() {
    HyperGraph graph = getGraph();
    HGHandle topicMapHandle = getHandle(graph, this);
    return HGTMUtil.findTopicMapItems(graph, HGAssociationSupport.class, topicMapHandle);
  }

  @Override
  public List<TopicImpl> getTopics() {
    HyperGraph graph = getGraph();
    HGHandle topicMapHandle = getHandle(graph, this);
    return HGTMUtil.findTopicMapItems(graph, HGTopicSupport.class, topicMapHandle);
  }

  @HGIgnore
  @Override
  public TopicImpl getReifier() {
    HyperGraph graph = getGraph();
    HGHandle h = HGTMUtil.getReifierOf(graph, graph.getHandle(this));
    return h != null ? ((HGTopicSupport) graph.get(h)).getOwner() : null;
  }

  @HGIgnore
  @Override
  public void setReifier(TopicImpl t) {
    HyperGraph graph = getGraph();
    HGTMUtil.setReifierOf(graph, getHandle(graph, this), t == null ? null : getHandle(graph, t));
  }

  @Override
  public String generateId(AbstractConstruct<?> construct) {
    HyperGraph graph = getGraph();
    HGHandle handle = graph.getHandle(construct.getSupport());
    return handle == null ? null : graph.getPersistentHandle(handle).toString();
  }

  @Override
  public void removeTopic(Topic topic) {
    remove(getGraph(), topic);
  }

  @Override
  public void removeAssociation(Association association) {
    remove(getGraph(), association);
  }

  @Override
  public void setOwner(TopicMapImpl owner) {
    this.owner = owner;
  }

  @Override
  public <I extends Index> I getIndex(Class<I> type) {
    HyperGraph graph = getGraph();
    Index index;
    if (LiteralIndex.class.isAssignableFrom(type)) {
      index = new HGLiteralIndex(graph, getOwner().getEquality());
    } else if (IdentifierIndex.class.isAssignableFrom(type)) {
      index = ((TopicMapImpl) owner)
          .registerListener(new IdentifierIndex(getOwner(), getTopics(), getAssociations()));
    } else if (ScopedIndex.class.isAssignableFrom(type)) {
      index = new HGScopedIndex(graph, getOwner().getEquality());
    } else if (TypeInstanceIndex.class.isAssignableFrom(type)) {
      index = new HGTypeInstanceIndex(graph, getOwner().getEquality());
    } else {
      throw new UnsupportedOperationException("Unknown index " + type);
    }
    return type.cast(index);
  }
}
