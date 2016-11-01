package org.gingolph.tm.hg;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.util.HGUtils;
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

  Role get(int i) {
    Role r = ((HGRoleSupport) graph.get(ass.roles.get(i))).owner;
    return r;
  }

  @Override
  public boolean contains(Object o) {
    for (int i = 0; i < size(); i++) {
      if (HGUtils.eq(o, get(i))) {
        return true;
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