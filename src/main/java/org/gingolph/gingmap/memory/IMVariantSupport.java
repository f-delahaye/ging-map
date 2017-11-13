package org.gingolph.gingmap.memory;

import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.VariantImpl;
import org.gingolph.gingmap.support.VariantSupport;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;


public class IMVariantSupport extends IMScopedSupport implements VariantSupport {
  private Topic reifier;
  private String value;
  private Locator datatype;

  @Override
  public Topic getReifier() {
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
  public void setOwner(VariantImpl owner) {
 // Noop - not needed by the in memory implementation    
  }

}
