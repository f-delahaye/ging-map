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
import org.gingolph.tm.equality.Equality;
import org.tmapi.core.Construct;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;
import org.tmapi.index.LiteralIndex;


public class LiteralIndexImpl extends AbstractIndex implements LiteralIndex, TopicMapEventListener {

  public LiteralIndexImpl(Equality equality) {
    super(equality);
  }

  Map<String, Collection<Valued>> cache = new LinkedHashMap<>();


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
        .filter(occurrence -> LocatorImpl.XSD_ANY_URI.equals(datatype) || datatype.equals(occurrence.getDatatype())).collect(Collectors.toList());
  }

  @Override
  public Collection<Occurrence> getOccurrences(String value) {
    return getOccurrences(value, LocatorImpl.XSD_STRING);
  }

  @Override
  public Collection<Occurrence> getOccurrences(Locator value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not supported");
    }    
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
    if (value == null) {
      throw new IllegalArgumentException("Null value not supported");
    }
    return getVariants(value.getReference(), LocatorImpl.XSD_ANY_URI);
  }

  @Override
  public Collection<Variant> getVariants(String value, Locator datatype) {
    return getDatatypeAwares(Variant.class, value, datatype);
  }

  @Override
  public Collection<Name> getNames(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not supported");
    }    
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
      cache.computeIfAbsent(valueSet, key -> new ArrayList<>()).add(valued);
    }
    if (valueRemoved != null) {
      cache.computeIfPresent(valueRemoved, (k, v) -> removeAndNullifyIfEmpty(v, valued));
    }
  }
  

//  @Override
//  public void onConstructCreated(Construct construct) {
//      if (construct instanceof Valued) {
//        Valued valued = (Valued) construct;
//        cache.computeIfAbsent(valued.getValue(), key -> new ArrayList<>()).add(valued);
//    }
//  }

  @Override
  public void onConstructRemoved(Construct construct) {
    if (construct instanceof Valued) {
      Valued valued = (Valued) construct;
      cache.computeIfPresent(valued.getValue(), (k, v) -> removeAndNullifyIfEmpty(v, valued));
    }  
  }
}
