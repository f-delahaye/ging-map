package org.gingolph.tm;

import java.util.Set;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;


public interface AssociationSupport extends ConstructSupport, ScopedSupport, TypedSupport {

  Topic getReifier();

  Set<Role> getRoles();

  Topic getType();

  void setReifier(Topic reifier);

  void addRole(Role role);

  void removeRole(Role role);

  void setType(Topic type);

}
