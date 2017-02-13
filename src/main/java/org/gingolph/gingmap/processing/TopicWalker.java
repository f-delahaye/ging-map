package org.gingolph.gingmap.processing;

import org.gingolph.gingmap.NameImpl;
import org.gingolph.gingmap.TopicImpl;

public class TopicWalker {
  public void visitTopic(TopicImpl topic, TopicVisitor visitor) {
    topic.names().forEach(name -> visitName(name, visitor));
    topic.occurrences().forEach(visitor::onOccurrence);
    topic.rolesPlayed().forEach(visitor::onRolePlayed);
  }
  
  private void visitName(NameImpl name, TopicVisitor visitor) {
    visitor.onName(name);
    name.variants().forEach(visitor::onVariant);;
  }
  
}
