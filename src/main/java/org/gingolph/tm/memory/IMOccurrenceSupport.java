package org.gingolph.tm.memory;

import org.gingolph.tm.OccurrenceImpl;
import org.gingolph.tm.OccurrenceSupport;
import org.gingolph.tm.TopicImpl;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;


public class IMOccurrenceSupport extends IMScopedSupport implements OccurrenceSupport {
  private TopicImpl type;
  private TopicImpl reifier;
  private String value;
  private Locator datatype;

  @Override
  public TopicImpl getType() {
    return type;
  }

  @Override
  public void setType(TopicImpl type) {
    this.type = type;
  }

  @Override
  public TopicImpl getReifier() {
    return reifier;
  }

  @Override
  public void setReifier(TopicImpl reifier) {
    this.reifier = reifier;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public Locator getDatatype() {
    return datatype;
  }

  @Override
  public void setDatatype(Locator datatype) {
    this.datatype = datatype;
  }

  @Override
  public void setOwner(OccurrenceImpl owner) {
 // Noop - not needed by the in memory implementation    
  }
}
