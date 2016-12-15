package org.gingolph.gingmap.json;

import org.gingolph.gingmap.TopicMapImpl;
import org.gingolph.gingmap.json.JsonTopicMapSystemFactory;
import org.gingolph.gingmap.json.JsonTopicMapSupport;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import mjson.Json;

public class TopicMapSupportJsonTest {

  @BeforeClass
  public static void loadFactory() throws Exception {
    // This should load the class and hence configure the Json.factory
    Class.forName(JsonTopicMapSupport.class.getName());    
  }
  
  @Test
  public void create() throws Exception {
    TopicMapImpl topicMap = (TopicMapImpl) new JsonTopicMapSystemFactory().newTopicMapSystem().createTopicMap("json");
    topicMap.createTopic();
    topicMap.createTopic();
    
    System.out.println(topicMap.getSupport().toString());
  }
  
  @Test
  public void read() {
    String json = "{\"associations\":[],\"topics\":[{\"occurrences\":[],\"names\":[],\"subject.identifiers\":[],\"item.identifiers\":[\"internal-0\"],\"subject.locators\":[]},{\"occurrences\":[],\"names\":[],\"subject.identifiers\":[],\"item.identifiers\":[\"internal-4\"],\"subject.locators\":[]}],\"item.identifiers\":[]}";
    Json support = Json.read(json);
    Assert.assertTrue(support instanceof JsonTopicMapSupport);
    Assert.assertEquals(json, support.toString());
  }
  
}
