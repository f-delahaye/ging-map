package org.gingolph.tm;

import java.util.Set;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;


public interface AssociationSupport extends ConstructSupport, ScopedSupport, TypedSupport {

  TopicImpl getReifier();

  Set<Role> getRoles();

  Topic getType();

  void setReifier(TopicImpl reifier);

  void addRole(Role role);

  void removeRole(Role role);

  void setType(Topic type);

}
