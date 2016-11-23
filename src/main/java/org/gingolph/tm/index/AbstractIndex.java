package org.gingolph.tm.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.gingolph.tm.equality.Equality;
import org.gingolph.tm.event.TopicMapEventListenerSupport;
import org.tmapi.core.Topic;
import org.tmapi.index.Index;


public abstract class AbstractIndex extends TopicMapEventListenerSupport implements Index {

  boolean open = false;
  protected Equality equality;

  protected AbstractIndex(Equality equality) {
    this.equality = equality;}

  @Override
  public void open() {
    reindex();
    open = true;
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
  public void close() {
    doClose();
    open = false;
  }

  @Override
  public void reindex() {}

  protected abstract void doClose();

  protected <T, C extends T> Collection<T> unmodifiableCollection(Collection<C> source) {
    return source == null ? Collections.emptyList() : Collections.unmodifiableCollection(source);
  }

  protected <T, C extends T> Collection<T> getPropertiedObjects(
      Map<Topic, Collection<C>> cache, Function<C, Collection<Topic>> propertiesSource,
      Topic[] properties, boolean matchAll) throws IllegalArgumentException {
    if (properties == null) {
      throw new IllegalArgumentException("Null not allowed");
    }
    if (properties.length == 0) {
      return Collections.emptyList();
    }
    
    Collection<T> result;
    if (matchAll) {
      List<Topic> remainingProperties = Arrays.asList(properties).subList(1, properties.length);
      Collection<C> propertiedObjects = cache.get(properties[0]);
      if (propertiedObjects == null) {
        return Collections.emptyList();
      }
      result = propertiedObjects.stream().filter(propertiedObject -> propertiesSource
          .apply(propertiedObject).containsAll(remainingProperties)).collect(Collectors.toList());
    } else {
      result = new ArrayList<>();
      for (Topic theme : properties) {
        result.addAll(cache.getOrDefault(theme, Collections.emptyList()));
      }
    }
    Set<T> set = equality.newSet();
    set.addAll(result);
    return Collections.unmodifiableSet(set);
  }

  protected <T> void onPropertyChanged(Map<Topic, Collection<T>> cache, T propertied,
      Topic propertyToAdd, Topic propertyToRemove) {
    onPropertyRemoved(cache, propertied, propertyToRemove);
    onPropertyAdded(cache, propertied, propertyToAdd);
  }

  protected <T> void onPropertyRemoved(Map<Topic, Collection<T>> cache, T propertied,
      Topic property) {
    if (property != null) {
      Collection<T> previousPropertiedObjects = cache.get(property);
      if (previousPropertiedObjects != null) {
        previousPropertiedObjects.remove(propertied);
      }
    }
  }

  protected <T> void onPropertyAdded(Map<Topic, Collection<T>> cache, T propertied,
      Topic property) {
    if (property != null) {
      Collection<T> propertiedObjects = cache.get(property);
      if (propertiedObjects == null) {
        propertiedObjects = new ArrayList<>();
        cache.put(property, propertiedObjects);
      }
      propertiedObjects.add(propertied);
    }
  }

  protected static <T> Collection<T> removeAndNullifyIfEmpty(Collection<T> collection, T toRemove) {
    collection.remove(toRemove);
    return collection.isEmpty() ? null : collection; 
  }

}
