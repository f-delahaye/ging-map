package org.gingolph.tm.hg;

import org.gingolph.tm.OccurrenceImpl;
import org.gingolph.tm.OccurrenceSupport;
import org.gingolph.tm.TopicImpl;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.annotation.HGIgnore;
import org.tmapi.core.Locator;

public class HGOccurrenceSupport extends HGScopedSupport<OccurrenceImpl> implements OccurrenceSupport {

  private static final long serialVersionUID = 1L;
  private String value;
  private Locator datatype;

  protected HGOccurrenceSupport() {}

  protected HGTopicSupport getParent() {
    return HGTMUtil.getOneRelated(hyperGraph, HGTM.hOccurrenceOf, getHandle(hyperGraph, this),
        null);
  }

  @Override
  public void setOwner(OccurrenceImpl owner) {
      this.owner = owner;
  }
  
  @Override
  protected OccurrenceImpl createOwner() {
    HGTopicSupport parent = getParent();
    OccurrenceImpl occurrence =
        new OccurrenceImpl(parent.getTopicMapSupport().getOwner(), parent.getOwner());
    occurrence.setSupport(this);
    return occurrence;
  }

  @HGIgnore
  @Override
  public TopicImpl getReifier() {
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

  @HGIgnore
  @Override
  public TopicImpl getType() {
    HyperGraph graph = getGraph();
    HGHandle type = HGTMUtil.getTypeOf(graph, getHandle(graph, this));
    return type != null ? ((HGTopicSupport) graph.get(type)).getOwner() : null;
  }

  @HGIgnore
  @Override
  public void setType(TopicImpl type) {
    HGTMUtil.setTypeOf(hyperGraph, getHandle(hyperGraph, type), getHandle(hyperGraph, this));
  }


  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
    if (hyperGraph != null) {
      hyperGraph.update(this);
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
