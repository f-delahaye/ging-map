package org.gingolph.tm.memory;

import org.gingolph.tm.TopicSupport;
import org.gingolph.tm.AbstractTopicMapSystemFactory;
import org.gingolph.tm.AssociationSupport;
import org.gingolph.tm.NameSupport;
import org.gingolph.tm.OccurrenceSupport;
import org.gingolph.tm.RoleSupport;
import org.gingolph.tm.TopicMapSupport;
import org.gingolph.tm.TopicMapSystemSupport;
import org.gingolph.tm.VariantSupport;
import org.tmapi.core.TopicMap;

public class IMTopicMapSystemFactory extends AbstractTopicMapSystemFactory
    implements TopicMapSystemSupport {

  public IMTopicMapSystemFactory() {
    features.put(AUTOMERGE, Boolean.FALSE);
    features.put(MODEL, Boolean.FALSE);
    features.put(MERGE, Boolean.FALSE);
    features.put(NOTATION, Boolean.FALSE);
    features.put(READONLY, Boolean.FALSE);
    features.put(TYPE_INSTANCE_AS_ASSOCIATIONS, Boolean.FALSE);
  }

  @Override
  public NameSupport createNameSupport() {
    return new IMNameSupport();
  }

  @Override
  public AssociationSupport createAssociationSupport() {
    return new IMAssociationSupport();
  }

  @Override
  public OccurrenceSupport createOccurrenceSupport() {
    return new IMOccurrenceSupport();
  }

  @Override
  public VariantSupport createVariantSupport() {
    return new IMVariantSupport();
  }

  @Override
  public RoleSupport createRoleSupport() {
    return new IMRoleSupport();
  }

  @Override
  public TopicMapSupport createTopicMapSupport() {
    return new IMTopicMapSupport();
  }

  @Override
  public TopicSupport createTopicSupport() {
    return new IMTopicSupport();
  }

  @Override
  protected TopicMapSystemSupport getTopicMapSystemSupport() {
    return this;
  }

  @Override
  public void removeTopicMap(TopicMap topicMap) { // NoOp
  }

  @Override
  public void close(TopicMap topicMap) { // NoOp
  }

  @Override
  public void close() {}
}
