package org.gingolph.tm.equality;

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
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hashCode(AssociationImpl association) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hashCode(RoleImpl role) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hashCode(NameImpl name) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hashCode(OccurrenceImpl occurrence) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hashCode(VariantImpl variant) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Set<TopicImpl> newTopicSet() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<AssociationImpl> newAssociationSet() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<RoleImpl> newRoleSet() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<NameImpl> newNameSet() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<OccurrenceImpl> newOccurrenceSet() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<VariantImpl> newVariantSet() {
    // TODO Auto-generated method stub
    return null;
  }

}
