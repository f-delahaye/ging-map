package org.gingolph.gingmap;

public interface ConstructSupportFactory {

  AssociationSupport createAssociationSupport();

  NameSupport createNameSupport();

  OccurrenceSupport createOccurrenceSupport();

  RoleSupport createRoleSupport();

  VariantSupport createVariantSupport();

  TopicMapSupport createTopicMapSupport();

  TopicSupport createTopicSupport();
}
