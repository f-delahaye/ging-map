package org.gingolph.tm.equality;

import java.util.Set;

import org.gingolph.tm.AssociationImpl;
import org.gingolph.tm.NameImpl;
import org.gingolph.tm.OccurrenceImpl;
import org.gingolph.tm.RoleImpl;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.VariantImpl;

public interface Equality {
    public boolean equals(TopicImpl topic1, TopicImpl topic2);
    
    public boolean equals(AssociationImpl association1, AssociationImpl association2);
    
    public boolean equals(RoleImpl role1, RoleImpl role2);
    
    public boolean equals(NameImpl name1, NameImpl name2);
    
    public boolean equals(OccurrenceImpl occurrence1, OccurrenceImpl occurrence2);
    
    public boolean equals(VariantImpl variant1, VariantImpl variant2);
    
    
    public int hashCode(TopicImpl topic);
    
    public int hashCode(AssociationImpl association);
    
    public int hashCode(RoleImpl role);
    
    public int hashCode(NameImpl name);
    
    public int hashCode(OccurrenceImpl occurrence);
    
    public int hashCode(VariantImpl variant);
    
    
    public Set<TopicImpl> newTopicSet();

    public Set<AssociationImpl> newAssociationSet();
    
    public Set<RoleImpl> newRoleSet();
    
    public Set<NameImpl> newNameSet();
    
    public Set<OccurrenceImpl> newOccurrenceSet();
    
    public Set<VariantImpl> newVariantSet();    
}
