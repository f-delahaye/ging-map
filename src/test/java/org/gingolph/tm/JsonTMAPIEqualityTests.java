package org.gingolph.tm;

import org.gingolph.tm.json.JsonTopicMapSystemFactory;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.tmapi.AllTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AllTests.class} )
public class JsonTMAPIEqualityTests {
  @BeforeClass
  public static void before() {
    System.setProperty("org.tmapi.core.TopicMapSystemFactory", JsonTopicMapSystemFactory.class.getName());
    System.setProperty(AbstractTopicMapSystemFactory.EQUALITY_PROPERTY, AbstractTopicMapSystemFactory.TMAPI_EQUALITY);
  }
}
