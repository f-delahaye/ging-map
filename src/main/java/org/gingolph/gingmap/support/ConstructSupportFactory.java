package org.gingolph.gingmap.support;

public interface ConstructSupportFactory {

  AssociationSupport createAssociationSupport();

  NameSupport createNameSupport();

  OccurrenceSupport createOccurrenceSupport();

  RoleSupport createRoleSupport();

  VariantSupport createVariantSupport();

  TopicMapSupport createTopicMapSupport();

  TopicSupport createTopicSupport();
}