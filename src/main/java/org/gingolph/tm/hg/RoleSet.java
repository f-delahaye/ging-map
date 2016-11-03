package org.gingolph.tm.hg;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

import org.gingolph.tm.RoleImpl;
import org.hypergraphdb.HyperGraph;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIRuntimeException;

/**
 *
 * <p>
 * This exposes the target set of an association's roles as a Java <code>Set</code> as required by
 * the TMAPI.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
class RoleSet extends AbstractSet<Role> {

  HyperGraph graph;
  HGAssociationSupport ass;

  RoleSet(HyperGraph graph, HGAssociationSupport ass) {
    this.graph = graph;
    this.ass = ass;

  }

  RoleImpl get(int i) {
    RoleImpl r = (RoleImpl)((HGRoleSupport) graph.get(ass.roles.get(i))).getOwner();
    return r;
  }

  @Override
  public boolean contains(Object other) {
    if (other instanceof RoleImpl) {
      RoleImpl otherRole = (RoleImpl)other;
      for (int i = 0; i < size(); i++) {
        if (RoleImpl.equalsNoParent(get(i), otherRole)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean isEmpty() {
    return ass.roles.isEmpty();
  }

  @Override
  public Iterator<Role> iterator() {
    return new Iterator<Role>() {
      int i = 0;

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasNext() {
        return i < ass.roles.size();
      }

      @Override
      public Role next() {
        return get(i++);
      }
    };
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    Role[] roles = new Role[ass.roles.size()];
    for (int i = 0; i < roles.length; i++) {
      roles[i] = get(i);
    }
    for (Role r : roles) {
      try {
        r.remove();
      } catch (Exception ex) {
        throw new TMAPIRuntimeException(ex);
      }
    }
    return true;
  }

  @Override
  public int size() {
    return ass.roles.size();
  }
}
