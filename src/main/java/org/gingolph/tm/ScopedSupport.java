package org.gingolph.tm;

import java.util.Set;
import org.tmapi.core.Topic;


public interface ScopedSupport {

    void addTheme(Topic t);

    Set<Topic> getScope();

    void removeTheme(Topic t);

}
