package org.gingolph.tm;

import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Variant;


public interface ConstructSupportFactory {

  AssociationSupport createAssociationSupport(AssociationImpl association);

  NameSupport createNameSupport(Name name);

  OccurrenceSupport createOccurrenceSupport(Occurrence occurrence);

  RoleSupport createRoleSupport(Role role);

  VariantSupport createVariantSupport(Variant variant);

  TopicMapSupport createTopicMapSupport(TopicMapImpl topicMap);

  TopicSupport createTopicSupport(TopicImpl topic);
}
