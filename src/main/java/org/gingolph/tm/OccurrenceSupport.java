package org.gingolph.tm;

import org.tmapi.core.Topic;


public interface OccurrenceSupport extends ConstructSupport, ScopedSupport, TypedSupport, DatatypeAwareSupport {

    Topic getReifier();

    Topic getType();

    void setReifier(Topic reifier);

    void setType(Topic type);

}
