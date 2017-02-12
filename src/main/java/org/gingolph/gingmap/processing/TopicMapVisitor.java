package org.gingolph.gingmap.processing;

import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.NameImpl;
import org.gingolph.gingmap.OccurrenceImpl;
import org.gingolph.gingmap.RoleImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.VariantImpl;

public interface TopicMapVisitor {
  
  public void onTopic(TopicImpl topic);
  public void onAssociation(AssociationImpl association);
  public void onRole(RoleImpl role);
  public void onName(NameImpl name);
  public void onOccurrence(OccurrenceImpl occurrence);
  public void onVariant(VariantImpl variant);
}
