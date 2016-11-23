package org.gingolph.tm.equality;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import org.gingolph.tm.AssociationImpl;
import org.gingolph.tm.NameImpl;
import org.gingolph.tm.OccurrenceImpl;
import org.gingolph.tm.RoleImpl;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.VariantImpl;

public class TMAPIEquality implements Equality {

  @Override
  public boolean equals(TopicImpl topic1, TopicImpl topic2) {
    return topic1 == topic2;
  }

  @Override
  public boolean equals(AssociationImpl association1, AssociationImpl association2) {
    return association1 == association2;
  }

  @Override
  public boolean equals(RoleImpl role1, RoleImpl role2) {
    return role1 == role2;
  }

  @Override
  public boolean equals(NameImpl name1, NameImpl name2) {
    return name1 == name2;
  }

  @Override
  public boolean equals(OccurrenceImpl occurrence1, OccurrenceImpl occurrence2) {
    return occurrence1 == occurrence2;
  }

  @Override
  public boolean equals(VariantImpl variant1, VariantImpl variant2) {
    return variant1 == variant2;
  }

  @Override
  public int hashCode(TopicImpl topic) {
    return System.identityHashCode(topic);
  }

  @Override
  public int hashCode(AssociationImpl association) {
    return System.identityHashCode(association);    
  }

  @Override
  public int hashCode(RoleImpl role) {
    return System.identityHashCode(role);
  }

  @Override
  public int hashCode(NameImpl name) {
    return System.identityHashCode(name);
  }

  @Override
  public int hashCode(OccurrenceImpl occurrence) {
    return System.identityHashCode(occurrence);
  }

  @Override
  public int hashCode(VariantImpl variant) {
    return System.identityHashCode(variant);
  }

  @Override
  public <T> Set<T> newSet() {
    return Collections.newSetFromMap(new IdentityHashMap<>());
  }
}
