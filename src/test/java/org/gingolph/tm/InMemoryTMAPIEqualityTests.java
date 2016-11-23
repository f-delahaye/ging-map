package org.gingolph.tm;

import org.gingolph.tm.memory.IMTopicMapSystemFactory;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.tmapi.AllTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AllTests.class} )
public class InMemoryTMAPIEqualityTests {
  @BeforeClass
  public static void before() {
    System.setProperty("org.tmapi.core.TopicMapSystemFactory", IMTopicMapSystemFactory.class.getName());
    System.setProperty(AbstractTopicMapSystemFactory.EQUALITY_PROPERTY, AbstractTopicMapSystemFactory.TMAPI_EQUALITY);
  }
}
