package org.gingolph.tm.hg;

import java.util.Set;
import java.util.stream.Collectors;
import org.gingolph.tm.NameImpl;
import org.gingolph.tm.NameSupport;
import org.gingolph.tm.TopicImpl;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.annotation.HGIgnore;
import org.hypergraphdb.atom.HGRel;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

public class HGNameSupport extends HGScopedSupport<Name> implements NameSupport {

  String value;

  // For compliance with Javabeans standard (which allows NameSupport to be persisted as a
  // JavabeansType)
  protected HGNameSupport() {}

  public HGNameSupport(Name name) {
    super(name);
  }

  @Override
  public void addVariant(Variant v) {
    HGHandle h = add(hyperGraph, v);
    hyperGraph.add(new HGRel(HGTM.VariantOf, new HGHandle[] {h, getHandle(hyperGraph, this)}),
        HGTM.hVariantOf);
  }

  @Override
  public void removeVariant(Variant v) {
    hyperGraph.remove(getHandle(hyperGraph, v), false);
  }

  @HGIgnore
  @Override
  public Topic getReifier() {
    final HGHandle handle = getHandle(hyperGraph, this);
    HGHandle h = HGTMUtil.getReifierOf(hyperGraph, handle);
    return h != null ? ((HGTopicSupport) hyperGraph.get(h)).getOwner() : null;
  }

  @HGIgnore
  @Override
  public void setReifier(TopicImpl reifier) {
    HGTMUtil.setReifierOf(hyperGraph, getHandle(hyperGraph, this), getHandle(hyperGraph, reifier));
  }

  @HGIgnore
  @Override
  public Topic getType() {
    final HGHandle thisHandle = getHandle(hyperGraph, this);
    HGHandle h = HGTMUtil.getTypeOf(hyperGraph, thisHandle);
    return h != null ? ((HGTopicSupport) hyperGraph.get(h)).getOwner() : null;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public Set<Variant> getVariants() {
    final HGHandle handle = getHandle(hyperGraph, this);
    return handle == null ? null
        : HGTMUtil.<HGVariantSupport>getRelatedObjects(hyperGraph, HGTM.hVariantOf, null, handle)
            .stream().map(support -> support.getOwner()).collect(Collectors.toSet());
  }

  @HGIgnore
  @Override
  public void setType(Topic type) {
    HGTMUtil.setTypeOf(hyperGraph, getHandle(hyperGraph, type), getHandle(hyperGraph, this));
  }

  @Override
  public void setValue(String value) {
    this.value = value;
    if (hyperGraph != null) {
      HGHandle handle = getHandle(hyperGraph, this);
      if (handle != null) {
        hyperGraph.replace(handle, this);
      }
    }
  }

  protected HGTopicSupport getParent() {
    return HGTMUtil.getOneRelated(hyperGraph, HGTM.hNameOf, getHandle(hyperGraph, this), null);
  }

  @Override
  protected Name createOwner() {
    final HGTopicSupport parent = getParent();
    NameImpl name = new NameImpl(parent.getTopicMapSupport().getOwner(), parent.getOwner());
    name.setSupport(this);
    return name;
  }
}
