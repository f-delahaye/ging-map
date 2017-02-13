package org.gingolph.gingmap.processing;

import org.gingolph.gingmap.NameImpl;
import org.gingolph.gingmap.OccurrenceImpl;
import org.gingolph.gingmap.RoleImpl;
import org.gingolph.gingmap.VariantImpl;

public interface TopicVisitor {
  
  public void onName(NameImpl name);
  public void onOccurrence(OccurrenceImpl occurrence);
  public void onVariant(VariantImpl variant);
  
  public void onRolePlayed(RoleImpl role);
}
