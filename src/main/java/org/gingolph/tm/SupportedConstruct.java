package org.gingolph.tm;

import org.gingolph.tm.event.TopicMapEventListenerSupport;
import org.tmapi.core.Construct;


public abstract class SupportedConstruct<S extends ConstructSupport> extends TopicMapEventListenerSupport implements Construct {
    //    String id;
    S support;

    public SupportedConstruct() {
    }

    public S getSupport() {
        return support;
    }

    public void setSupport(S support) {
        this.support = support;
    }

}
