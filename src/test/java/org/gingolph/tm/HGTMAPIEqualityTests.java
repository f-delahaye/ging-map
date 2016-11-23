package org.gingolph.tm;

import java.io.File;

import org.gingolph.tm.hg.HGTopicMapSystemFactory;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.tmapi.AllTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AllTests.class} )
public class HGTMAPIEqualityTests {
  @BeforeClass
  public static void before() {
    System.setProperty("org.tmapi.core.TopicMapSystemFactory", HGTopicMapSystemFactory.class.getName());
    System.setProperty(AbstractTopicMapSystemFactory.EQUALITY_PROPERTY, AbstractTopicMapSystemFactory.TMAPI_EQUALITY);
    String pathToStorageFile = "test-suite";
//    String pathToStorageFile = "topicmaps";    
    File path = new File(pathToStorageFile);
    for (File file: path.listFiles()) {
      file.delete();
    }
    path.delete();   
    System.setProperty(HGTopicMapSystemFactory.HG_STORAGE_PATH_PROPERTY, pathToStorageFile);
    System.setProperty(HGTopicMapSystemFactory.HG_STORAGE_KEEP_OPEN_PROPERTY, "true");
  }
}
