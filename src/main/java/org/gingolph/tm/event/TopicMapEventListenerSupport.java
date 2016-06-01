package org.gingolph.tm.event;

import org.gingolph.tm.TypedConstruct;
import org.gingolph.tm.Valued;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;


public class TopicMapEventListenerSupport implements TopicMapEventListener {

  @Override
  public void onConstructCreated(Construct construct) {}

  @Override
  public void onConstructRemoved(Construct construct) {}

  @Override
  public void onSubjectIdentifierAdded(Topic topic, Locator subjectIdentifier) {}

  @Override
  public void onSubjectIdentifierRemoved(Locator subjectIdentifier) {}

  @Override
  public void onSubjectLocatorAdded(Topic topic, Locator subjectLocator) {}

  @Override
  public void onSubjectLocatorRemoved(Locator subjectLocator) {}

  @Override
  public void onItemIdentifierAdded(Construct construct, Locator itemIdentifier) {}

  @Override
  public void onItemIdentifierRemoved(Locator itemIdentifier) {}

  @Override
  public void onThemeChanged(Scoped scoped, Topic themeToAdd, Topic themeToRemove) {}

  @Override
  public void onTypeChanged(TypedConstruct typed, Topic typeToAdd, Topic typeToRemove) {}

  @Override
  public void onValueChanged(Valued valued, String valueSet, String valueRemoved) {}
}
