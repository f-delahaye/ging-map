package org.gingolph.gingmap.support.spi;

import java.util.List;
import java.util.Set;


public interface TopicInterface<T, R, N, O> {

  void addName(N name);
  
  void removeName(N name);

  List<N> getNames();

  void addRolePlayed(R role);

  void removeRolePlayed(R role);
  
  List<R> getRolesPlayed();
  
  
  void addOccurrence(O occurrence);

  List<O> getOccurrences();

  void removeOccurrence(O occurrence);

  void addType(T type);

  Set<T> getTypes();

  boolean removeType(T type);


}
