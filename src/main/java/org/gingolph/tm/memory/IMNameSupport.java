package org.gingolph.tm.memory;

import java.util.HashSet;
import java.util.Set;
import org.gingolph.tm.NameSupport;
import org.gingolph.tm.TopicImpl;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;


public class IMNameSupport extends IMScopedSupport implements NameSupport {
  private TopicImpl reifier;
  private String value;
  private Set<Variant> variants;
  private Topic type;

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
  public Set<Variant> getVariants() {
    return variants;
  }

  @Override
  public void addVariant(Variant variant) {
    if (variants == null) {
      variants = new HashSet<>();
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
  public Topic getType() {
    return type;
  }

  @Override
  public void setType(Topic type) {
    this.type = type;
  }
}
