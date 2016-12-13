package org.gingolph.gingmap.memory;

import org.gingolph.gingmap.AbstractTopicMapSystemFactory;
import org.gingolph.gingmap.AssociationSupport;
import org.gingolph.gingmap.NameSupport;
import org.gingolph.gingmap.OccurrenceSupport;
import org.gingolph.gingmap.RoleSupport;
import org.gingolph.gingmap.TopicMapSupport;
import org.gingolph.gingmap.TopicMapSystemSupport;
import org.gingolph.gingmap.TopicSupport;
import org.gingolph.gingmap.VariantSupport;
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
