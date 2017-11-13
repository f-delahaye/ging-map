package org.gingolph.gingmap.support.spi;

import java.util.List;


public interface AssociationInterface<T, R> {

  T getType();
  
  List<R> getRoles();

  void addRole(R role);

  void removeRole(R role);
}
