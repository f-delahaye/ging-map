package org.gingolph.tm.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.gingolph.tm.event.TopicMapEventListener;
import org.gingolph.tm.LocatorImpl;
import org.gingolph.tm.Valued;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;
import org.tmapi.index.LiteralIndex;


public class LiteralIndexImpl extends AbstractIndex implements LiteralIndex, TopicMapEventListener {

  Map<String, Collection<Valued>> cache = new LinkedHashMap<>();

  public LiteralIndexImpl() {}

  @Override
  public boolean isAutoUpdated() {
    return true;
  }

  protected <T extends DatatypeAware> Collection<T> getDatatypeAwares(Class<T> valuedClass,
      String value, Locator datatype) throws IllegalArgumentException {
    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed");
    }
    if (datatype == null) {
      throw new IllegalArgumentException("Null datatype not allowed");
    }
    return getValued(value, valuedClass).stream()
        .filter(occurrence -> occurrence.getDatatype() == datatype).collect(Collectors.toList());
  }

  @Override
  public Collection<Occurrence> getOccurrences(String value) {
    return getOccurrences(value, LocatorImpl.XSD_STRING);
  }

  @Override
  public Collection<Occurrence> getOccurrences(Locator value) {
    return getOccurrences(value.getReference(), LocatorImpl.XSD_ANY_URI);
  }

  @Override
  public Collection<Occurrence> getOccurrences(String value, Locator datatype) {
    return getDatatypeAwares(Occurrence.class, value, datatype);
  }

  @Override
  public Collection<Variant> getVariants(String value) {
    return getVariants(value, LocatorImpl.XSD_STRING);
  }

  @Override
  public Collection<Variant> getVariants(Locator value) {
    return getVariants(value.getReference(), LocatorImpl.XSD_ANY_URI);
  }

  @Override
  public Collection<Variant> getVariants(String value, Locator datatype) {
    return getDatatypeAwares(Variant.class, value, datatype);
  }

  @Override
  public Collection<Name> getNames(String value) {
    return getValued(value, Name.class);
  }

  @Override
  protected void doClose() {}

  protected <T> Collection<T> getValued(String value, Class<T> valuedClass) {
    Collection<Valued> collection = cache.get(value);
    if (collection != null) {
      return collection.stream().filter(valued -> valuedClass.isAssignableFrom(valued.getClass()))
          .map(valued -> valuedClass.cast(valued)).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  @Override
  public void onValueChanged(Valued valued, String valueSet, String valueRemoved) {
    if (valueSet != null) {
      Collection<Valued> collection = cache.get(valueSet);
      if (collection == null) {
        collection = new ArrayList<>();
        cache.put(valueSet, collection);
      }
      collection.add(valued);
    }
    if (valueRemoved != null) {
      Collection<Valued> collection = cache.get(valueSet);
      if (collection != null) {
        collection.remove(valued);
      }
    }
  }
}
