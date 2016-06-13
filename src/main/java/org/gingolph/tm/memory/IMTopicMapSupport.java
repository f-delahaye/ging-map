package org.gingolph.tm.memory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.gingolph.tm.ArraySet;
import org.gingolph.tm.IdentifiedConstruct;
import org.gingolph.tm.LocatorImpl;
import org.gingolph.tm.TopicMapImpl;
import org.gingolph.tm.TopicMapSupport;
import org.gingolph.tm.index.IdentifierIndex;
import org.gingolph.tm.index.LiteralIndexImpl;
import org.gingolph.tm.index.ScopedIndexImpl;
import org.gingolph.tm.index.TypeInstanceIndexImpl;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;


public class IMTopicMapSupport extends IMConstructSupport implements TopicMapSupport {
  Set<Topic> topics = new ArraySet<>(Objects::equals);
  Set<Association> associations = new ArraySet<>(Objects::equals);
  Topic reifier;
  TopicMapImpl topicMap;
  Locator baseLocator;

  static AtomicLong counter = new AtomicLong();
  Map<Class<?>, Index> indexes = new LinkedHashMap<>();

  IMTopicMapSupport(TopicMap topicMap) {
    setTopicMap(topicMap);
  }

  public final void setTopicMap(TopicMap topicMap) {
    this.topicMap = (TopicMapImpl) topicMap;
  }

  @Override
  public Set<Topic> getTopics() {
    return topics;
  }

  @Override
  public void addTopic(Topic topic) {
    topics.add(topic);
  }

  @Override
  public void removeTopic(Topic topic) {
    topics.remove(topic);
  }

  @Override
  public Set<Association> getAssociations() {
    return associations;
  }

  @Override
  public void addAssociation(Association association) {
    associations.add(association);
  }

  @Override
  public void removeAssociation(Association association) {
    associations.remove(association);
  }

  @Override
  public Topic getReifier() {
    return reifier;
  }

  @Override
  public void setReifier(Topic reifier) {
    this.reifier = reifier;
  }

  @Override
  public <I extends Index> I getIndex(Class<I> type) {
    Index index = indexes.get(type);
    if (index == null) {
      if (LiteralIndex.class.isAssignableFrom(type)) {
        index = topicMap.registerListener(new LiteralIndexImpl());
      } else if (IdentifierIndex.class.isAssignableFrom(type)) {
        index = topicMap
            .registerListener(new IdentifierIndex(topicMap, getTopics(), getAssociations()));
      } else if (ScopedIndex.class.isAssignableFrom(type)) {
        index = topicMap.registerListener(new ScopedIndexImpl(getTopics(), getAssociations()));
      } else if (TypeInstanceIndex.class.isAssignableFrom(type)) {
        index =
            topicMap.registerListener(new TypeInstanceIndexImpl(getTopics(), getAssociations()));
      } else {
        throw new UnsupportedOperationException("Unknown index " + type);
      }
      indexes.put(type, index);
    }
    return (I) index;
  }

  @Override
  public String generateId(IdentifiedConstruct construct) {
    return String.valueOf(counter.getAndIncrement());
  }

  @Override
  public Locator createLocator(String value) {
    return new LocatorImpl(value);
  }

  @Override
  public Locator getBaseLocator() {
    return baseLocator;
  }

  @Override
  public void setBaseLocator(Locator baseLocator) {
    this.baseLocator = baseLocator;
  }
}
