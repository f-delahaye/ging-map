package org.gingolph.tm;

import java.util.List;

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

  TopicImpl getType();

  void setType(TopicImpl type);

  String getValue();

  void setValue(String value);

  List<VariantImpl> getVariants();

  void addVariant(VariantImpl variant);

  void removeVariant(Variant variant);
}
