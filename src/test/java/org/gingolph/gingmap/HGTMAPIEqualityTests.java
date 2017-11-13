package org.gingolph.gingmap;

import java.io.File;

import org.gingolph.gingmap.AbstractGingMapSystemFactory;
import org.gingolph.gingmap.hg.HGTopicMapSystemFactory;
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
    System.setProperty(AbstractGingMapSystemFactory.EQUALITY_PROPERTY, AbstractGingMapSystemFactory.TMAPI_EQUALITY);
    String pathToStorageFile = "test-suite";
    File path = new File(pathToStorageFile);
    if (!path.exists()) {
      path.mkdirs();
    }
    if (!path.isDirectory()) {
      throw new IllegalArgumentException(path+" must be a directory");
    }
    // Internal representation of Types are stored in the database. We drop all the files to force a new 
    // representation to be recreated so that tests run against the latest version of the code (and not a 3 year old version of the types)
    for (File file: path.listFiles()) {
      file.delete();
    }
//    path.delete();   
    System.setProperty(HGTopicMapSystemFactory.HG_STORAGE_PATH_PROPERTY, pathToStorageFile);
    System.setProperty(HGTopicMapSystemFactory.HG_STORAGE_CLOSE_ON_EXIT_PROPERTY, "false");
  }
}
