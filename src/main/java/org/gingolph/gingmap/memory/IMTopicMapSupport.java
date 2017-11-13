package org.gingolph.gingmap.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.gingolph.gingmap.AbstractConstruct;
import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.LocatorImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.TopicMapImpl;
import org.gingolph.gingmap.index.IdentifierIndex;
import org.gingolph.gingmap.index.LiteralIndexImpl;
import org.gingolph.gingmap.index.ScopedIndexImpl;
import org.gingolph.gingmap.index.TypeInstanceIndexImpl;
import org.gingolph.gingmap.support.TopicMapSupport;
import org.tmapi.core.Association;
import org.tmapi.core.Topic;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;


public class IMTopicMapSupport extends IMConstructSupport implements TopicMapSupport {
  List<TopicImpl> topics = new ArrayList<>();
  List<AssociationImpl> associations = new ArrayList<>();
  TopicImpl reifier;
  TopicMapImpl topicMap;
  private LocatorImpl baseLocator;

  static AtomicLong counter = new AtomicLong();

  IMTopicMapSupport() {}

  @Override
  public void setOwner(TopicMapImpl owner) {
    this.topicMap = owner;
  }
  
  @Override
  public List<TopicImpl> getTopics() {
    return topics;
  }

  @Override
  public void addTopic(TopicImpl topic) {
    topics.add(topic);
  }

  @Override
  public void removeTopic(TopicImpl topic) {
    topics.remove(topic);
  }

  @Override
  public List<AssociationImpl> getAssociations() {
    return associations;
  }

  @Override
  public void addAssociation(AssociationImpl association) {
    associations.add(association);
  }

  @Override
  public void removeAssociation(AssociationImpl association) {
    associations.remove(association);
  }

  @Override
  public TopicImpl getReifier() {
    return reifier;
  }

  @Override
  public void setReifier(TopicImpl reifier) {
    this.reifier = reifier;
  }

  @Override
  public <I extends Index> I getIndex(Class<I> type) {
    Index index;
    if (LiteralIndex.class.isAssignableFrom(type)) {
      index = topicMap.registerListener(new LiteralIndexImpl(topicMap.getEquality()));
    } else if (IdentifierIndex.class.isAssignableFrom(type)) {
      index =
          topicMap.registerListener(new IdentifierIndex(topicMap, getTopics(), getAssociations()));
    } else if (ScopedIndex.class.isAssignableFrom(type)) {
      index = topicMap.registerListener(new ScopedIndexImpl(topicMap.getEquality(), getTopics(), getAssociations()));
    } else if (TypeInstanceIndex.class.isAssignableFrom(type)) {
      index = topicMap.registerListener(new TypeInstanceIndexImpl(topicMap.getEquality(), getTopics(), getAssociations()));
    } else {
      throw new UnsupportedOperationException("Unknown index " + type);
    }
    return type.cast(index);
  }

  @Override
  public String generateId(AbstractConstruct<?> construct) {
    return String.valueOf(counter.getAndIncrement());
  }

  @Override
  public LocatorImpl getBaseLocator() {
    return baseLocator;
  }

  @Override
  public void setBaseLocator(LocatorImpl baseLocator) {
    this.baseLocator = baseLocator;
  }
  
}
