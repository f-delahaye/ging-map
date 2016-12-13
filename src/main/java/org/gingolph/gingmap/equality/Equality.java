package org.gingolph.gingmap.equality;

import java.util.Set;

import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.NameImpl;
import org.gingolph.gingmap.OccurrenceImpl;
import org.gingolph.gingmap.RoleImpl;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.VariantImpl;

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
    
    
    public <T> Set<T> newSet();    
}
