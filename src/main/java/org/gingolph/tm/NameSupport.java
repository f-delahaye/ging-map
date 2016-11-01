package org.gingolph.tm;

import java.util.Set;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;


public interface NameSupport extends ConstructSupport, ScopedSupport, TypedSupport {

  /**
   * Callback method invoked by NameImpl.setSupport.
   * This is the reverse relationship.
   * A name NEEDS a support as most of its operations are delegated to the support.
   * Conversely, in certain implementations, a support MAY need its name.
   * 
   * Implementations are not required to store the supplied reference if they don't need it.
   * @param owner
   */  
  void setOwner(NameImpl owner);
  
  TopicImpl getReifier();

  void setReifier(TopicImpl reifier);

  Topic getType();

  void setType(Topic type);

  String getValue();

  void setValue(String value);

  Set<Variant> getVariants();

  void addVariant(Variant variant);

  void removeVariant(Variant variant);
}
