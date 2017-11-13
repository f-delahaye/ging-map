package org.gingolph.gingmap.support.spi;

import java.util.List;


public interface TopicMapInterface<T, A> {

  List<T> getTopics();

  void addTopic(T topic);

  void removeTopic(T topic);

  List<A> getAssociations();

  void addAssociation(A association);
  
  void removeAssociation(A association);

}
