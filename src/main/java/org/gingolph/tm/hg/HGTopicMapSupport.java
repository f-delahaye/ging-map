package org.gingolph.tm.hg;

import java.util.List;

import org.gingolph.tm.AbstractConstruct;
import org.gingolph.tm.AbstractTopicMapSystemFactory;
import org.gingolph.tm.AssociationImpl;
import org.gingolph.tm.LocatorImpl;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.TopicMapImpl;
import org.gingolph.tm.TopicMapSupport;
import org.gingolph.tm.TopicMapSystemImpl;
import org.gingolph.tm.TopicMapSystemSupport;
import org.gingolph.tm.hg.index.HGLiteralIndex;
import org.gingolph.tm.hg.index.HGScopedIndex;
import org.gingolph.tm.hg.index.HGTypeInstanceIndex;
import org.gingolph.tm.index.IdentifierIndex;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.annotation.HGIgnore;
import org.tmapi.core.Association;
import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

public class HGTopicMapSupport extends HGConstructSupport<TopicMapImpl> implements TopicMapSupport {

  private Locator baseLocator;
  private TopicMapSystemSupport parent;
  private transient HyperGraph graph;

  public HGTopicMapSupport() {}

  public HGTopicMapSupport(HyperGraph graph, TopicMapSystemSupport parent) {
    this.graph = graph;
    this.parent = parent;
  }

  @Override
  protected TopicMapImpl createOwner() {
    boolean autoMerge;
    try {
      autoMerge = parent.getFeature(AbstractTopicMapSystemFactory.AUTOMERGE);
    } catch (FeatureNotRecognizedException ex) {
      autoMerge = false;
    }
    TopicMapImpl topicMap = new TopicMapImpl(new TopicMapSystemImpl(parent), autoMerge, parent);
    topicMap.setSupport(this);
    return topicMap;
  }

  @Override
  public HyperGraph getGraph() {
    return graph;
  }

  @Override
  public void addAssociation(AssociationImpl association) {
    HGHandle associationHandle = add(graph, association);
    HGTMUtil.setTopicMapOf(hyperGraph, associationHandle, getHandle(graph, this));
  }

  @Override
  public void addTopic(TopicImpl topic) {
    HGHandle topicHandle = add(graph, topic);
    HGTMUtil.setTopicMapOf(hyperGraph, topicHandle, getHandle(graph, this));
  }

  @Override
  public Locator createLocator(String value) {
    Locator locator = new LocatorImpl(value);
    graph.add(locator);
    return locator;
  }

  @Override
  public Locator getBaseLocator() {
    return baseLocator;
  }

  @Override
  public void setBaseLocator(Locator locator) {
    this.baseLocator = locator;
  }

  @Override
  public List<AssociationImpl> getAssociations() {
    HGHandle topicMapHandle = getHandle(graph, this);
    return HGTMUtil.findTopicMapItems(graph, HGAssociationSupport.class, topicMapHandle);
  }

  @Override
  public List<TopicImpl> getTopics() {
    HGHandle topicMapHandle = getHandle(graph, this);
    return HGTMUtil.findTopicMapItems(graph, HGTopicSupport.class, topicMapHandle);
  }

  @HGIgnore
  @Override
  public TopicImpl getReifier() {
    HGHandle h = HGTMUtil.getReifierOf(graph, graph.getHandle(this));
    return h != null ? ((HGTopicSupport) graph.get(h)).getOwner() : null;
  }

  @HGIgnore
  @Override
  public void setReifier(TopicImpl t) {
    HGTMUtil.setReifierOf(graph, getHandle(graph, this), t == null ? null : getHandle(graph, t));
  }

  @Override
  public String generateId(AbstractConstruct construct) {
    HGHandle handle = graph.getHandle(construct.getSupport());
    return handle == null ? null : graph.getPersistentHandle(handle).toString();
  }

  @Override
  public void removeTopic(Topic topic) {
    final HGHandle handle = getHandle(graph, topic);
    if (handle != null) {
      graph.remove(handle, false);
    }
  }

  @Override
  public void removeAssociation(Association association) {
    final HGHandle handle = getHandle(graph, association);
    if (handle != null) {
      graph.remove(handle, false);
    }
  }

  @Override
  public void setOwner(TopicMapImpl owner) {
    this.owner = owner;
  }

  @Override
  public <I extends Index> I getIndex(Class<I> type) {
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
    return (I) index;
  }
}
