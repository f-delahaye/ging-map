package org.gingolph.gingmap.processing;

import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.NameImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.TopicMapImpl;

/**
 * Walks through a TopicMap, notifying the visitor as elements are visited.
 * 
 * Exposing methods like TopicMap.topics(), Topic.names() allows this logic to be moved outside of the elements themselves which makes things clearer imo.

 * @author frederic
 *
 */
public class TopicMapWalker {
  
  public TopicMapWalker() {
  }
  
  public void visitTopicMap(TopicMapImpl topicMap, TopicMapVisitor visitor) {
    topicMap.topics().forEach(topic -> visitTopic(topic, visitor));
    topicMap.associations().forEach(association -> visitAssociation(association, visitor));
  }
  
  private void visitTopic(TopicImpl topic, TopicMapVisitor visitor) {
    visitor.onTopic(topic);
    topic.names().forEach(name -> visitName(name, visitor));
    topic.occurrences().forEach(visitor::onOccurrence);
  }
  
  private void visitName(NameImpl name, TopicMapVisitor visitor) {
    visitor.onName(name);
    name.variants().forEach(visitor::onVariant);;
  }
  
  private void visitAssociation(AssociationImpl association, TopicMapVisitor visitor) {
    visitor.onAssociation(association);
    association.getNullSafeRoleImpls().forEach(visitor::onRole);
  }
}
