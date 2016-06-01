package org.gingolph.tm.hg;

import org.gingolph.tm.OccurrenceImpl;
import org.gingolph.tm.OccurrenceSupport;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.annotation.HGIgnore;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;

public class HGOccurrenceSupport extends HGScopedSupport<Occurrence> implements OccurrenceSupport {
  private String value;
  private Locator datatype;

  protected HGOccurrenceSupport() {}

  public HGOccurrenceSupport(Occurrence occurrence) {
    super(occurrence);
  }

  protected HGTopicSupport getParent() {
    return HGTMUtil.getOneRelated(hyperGraph, HGTM.hOccurrenceOf, getHandle(hyperGraph, this),
        null);
  }

  @Override
  protected Occurrence createOwner() {
    HGTopicSupport parent = getParent();
    OccurrenceImpl occurrence =
        new OccurrenceImpl(parent.getTopicMapSupport().getOwner(), parent.getOwner());
    occurrence.setSupport(this);
    return occurrence;
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
  public void setReifier(Topic topic) {
    HyperGraph graph = getGraph();
    HGTMUtil.setReifierOf(graph, getHandle(graph, this), getHandle(graph, topic));
  }

  @HGIgnore
  @Override
  public Topic getType() {
    HyperGraph graph = getGraph();
    HGHandle type = HGTMUtil.getTypeOf(graph, getHandle(graph, this));
    return type != null ? ((HGTopicSupport) graph.get(type)).getOwner() : null;
  }

  @HGIgnore
  @Override
  public void setType(Topic type) {
    HGTMUtil.setTypeOf(hyperGraph, getHandle(hyperGraph, type), getHandle(hyperGraph, this));
  }


  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
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
