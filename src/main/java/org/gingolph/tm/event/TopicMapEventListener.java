package org.gingolph.tm.event;

import org.gingolph.tm.TypedConstruct;
import org.gingolph.tm.Valued;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;


public interface TopicMapEventListener {
    public void onConstructCreated(Construct construct);
    public void onConstructRemoved(Construct construct);

    public void onSubjectIdentifierAdded(Topic topic, Locator subjectIdentifier);
    public void onSubjectIdentifierRemoved(Locator subjectIdentifier);
    
    public void onSubjectLocatorAdded(Topic topic, Locator subjectLocator);
    public void onSubjectLocatorRemoved(Locator subjectLocator);
    
    public void onItemIdentifierAdded(Construct construct, Locator itemIdentifier);
    public void onItemIdentifierRemoved(Locator itemIdentifier);

    public void onThemeChanged(Scoped scoped, Topic themeAdded, Topic themeRemoved);
    public void onTypeChanged(TypedConstruct typed, Topic typeAdded, Topic typeRemoved);
    public void onValueChanged(Valued valued, String valueSet, String valueRemoved);
}
