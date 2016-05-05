package org.gingolph.tm;

import java.util.Set;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;


public interface NameSupport extends ConstructSupport, ScopedSupport, TypedSupport {

    Topic getReifier();

    void setReifier(TopicImpl reifier);
    
    Topic getType();

    void setType(Topic type);
    
    String getValue();

    void setValue(String value);
    
    Set<Variant> getVariants();

    void addVariant(Variant variant);

    void removeVariant(Variant variant);
}
