package org.gingolph.tm;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;


public interface ConstructSupportFactory {

    AssociationSupport createAssociationSupport(Association association);

    NameSupport createNameSupport(Name name);

    OccurrenceSupport createOccurrenceSupport(Occurrence occurrence);

    RoleSupport createRoleSupport(Role role);

    VariantSupport createVariantSupport(Variant variant);

    TopicMapSupport createTopicMapSupport(TopicMap topicMap);
    
    TopicSupport createTopicSupport(TopicImpl topic);
}
