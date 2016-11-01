package org.gingolph.tm.hg;

import org.gingolph.tm.NameImpl;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.VariantImpl;
import org.gingolph.tm.VariantSupport;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.annotation.AtomReference;
import org.hypergraphdb.annotation.HGIgnore;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

public class HGVariantSupport extends HGScopedSupport<Variant> implements VariantSupport {

  private String value;
  @AtomReference("symbolic")
  private Locator datatype;

  protected HGVariantSupport() {}

  protected HGNameSupport getParent() {
    return HGTMUtil.getOneRelated(hyperGraph, HGTM.hVariantOf, hyperGraph.getHandle(this), null);
  }

  @Override
  public void setOwner(VariantImpl owner) {
      this.owner = owner;
  }
  
  @Override
  protected Variant createOwner() {
    HGNameSupport parent = getParent();
    VariantImpl variant = new VariantImpl(parent.getParent().getTopicMapSupport().getOwner(),
        (NameImpl) parent.getOwner());
    variant.setSupport(this);
    return variant;
  }



  @HGIgnore
  @Override
  public Topic getReifier() {
    HyperGraph graph = getGraph();
    HGHandle h = HGTMUtil.getReifierOf(graph, getHandle(graph, this));
    return h != null ? ((HGTopicSupport) graph.get(h)).getOwner() : null;
  }

  @HGIgnore
  @Override
  public void setReifier(TopicImpl topic) {
    HyperGraph graph = getGraph();
    HGTMUtil.setReifierOf(graph, getHandle(graph, this), getHandle(graph, topic));
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
    // let the index know about this change
    final HyperGraph graph = getGraph();
    if (graph != null) {
      HGHandle handle = getHandle(graph, this);
      if (handle != null) {
        graph.replace(handle, this);
      }
    }
  }

  @Override
  public Locator getDatatype() {
    return datatype;
  }

  @Override
  public void setDatatype(Locator datatype) {
    this.datatype = datatype;
  }

}
