package org.gingolph.gingmap.memory;

import java.util.ArrayList;
import java.util.List;

import org.gingolph.gingmap.NameImpl;
import org.gingolph.gingmap.NameSupport;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.VariantImpl;
import org.tmapi.core.Variant;

public class IMNameSupport extends IMScopedSupport implements NameSupport {
  private TopicImpl reifier;
  private String value;
  private List<VariantImpl> variants;
  private TopicImpl type;

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
  public List<VariantImpl> getVariants() {
    return variants;
  }

  @Override
  public void addVariant(VariantImpl variant) {
    if (variants == null) {
      variants = new ArrayList<>();
    }
    this.variants.add(variant);
  }

  @Override
  public void removeVariant(Variant variant) {
    if (variants != null) {
      variants.remove(variant);
    }
  }

  @Override
  public TopicImpl getType() {
    return type;
  }

  @Override
  public void setType(TopicImpl type) {
    this.type = type;
  }

  @Override
  public void setOwner(NameImpl owner) {
 // Noop - not needed by the in memory implementation    
  }
}
