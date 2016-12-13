package org.gingolph.gingmap;

import org.gingolph.gingmap.AbstractTopicMapSystemFactory;
import org.gingolph.gingmap.json.JsonTopicMapSystemFactory;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.tmapi.AllTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AllTests.class} )
public class JsonSAMEqualityTests {
  @BeforeClass
  public static void before() {
    System.setProperty("org.tmapi.core.TopicMapSystemFactory", JsonTopicMapSystemFactory.class.getName());
    System.setProperty(AbstractTopicMapSystemFactory.EQUALITY_PROPERTY, AbstractTopicMapSystemFactory.SAM_EQUALITY);
  }
}
