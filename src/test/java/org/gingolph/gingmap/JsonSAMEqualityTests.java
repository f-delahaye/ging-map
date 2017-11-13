package org.gingolph.gingmap;

import org.gingolph.gingmap.AbstractGingMapSystemFactory;
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
    System.setProperty(AbstractGingMapSystemFactory.EQUALITY_PROPERTY, AbstractGingMapSystemFactory.SAM_EQUALITY);
  }
}
