package org.gingolph.tm.hg;

import java.util.HashSet;
import java.util.Set;
import org.gingolph.tm.NameImpl;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.TopicSupport;

import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.atom.HGRel;
import org.hypergraphdb.util.HGUtils;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;

public class HGTopicSupport extends HGScopedSupport<TopicImpl> implements TopicSupport {
//    Set<Topic> types = null;
//    Set<Occurrence> occurrences = null;
//    Set<NameImpl> names = null;
    
    public HGTopicSupport(TopicImpl topic) {
        super(topic);
    }

    public HGTopicMapSupport getTopicMapSupport() {
        return HGTMUtil.getTopicMapOf(hyperGraph, getHandle(hyperGraph, this));
    }

    @Override
    protected TopicImpl createOwner() {
        TopicImpl topic = new TopicImpl(getTopicMapSupport().getOwner());
        topic.setSupport(this);
        return topic;
    }
    
    @Override
    public void addSubjectIdentifier(Locator subjectIdentifier) {
        HyperGraph graph = getGraph();
        final HGHandle thisHandle = getHandle(graph, this);
        HGHandle locHandle = HGTMUtil.ensureLocatorHandle(graph, subjectIdentifier);
        graph.add(new HGRel(HGTM.SubjectIdentifier, new HGHandle[]{locHandle, thisHandle}),
                HGTM.hSubjectIdentifier);
    }

    @Override
    public Set<Locator> getSubjectIdentifiers() {
        HyperGraph graph = getGraph();
        final HGHandle handle = getHandle(graph, this);
        return handle == null ? null : HGTMUtil.getRelatedObjects(graph, HGTM.hSubjectIdentifier, null, handle);
    }

    @Override
    public void removeSubjectIdentifier(Locator subjectIdentifier) {
        HyperGraph graph = getGraph();
        HGHandle locatorHandle = HGTMUtil.findLocatorHandle(graph, subjectIdentifier);
        HGHandle rel = hg.findOne(graph, hg.and(hg.type(HGTM.hSubjectIdentifier), hg.orderedLink(locatorHandle, getHandle(graph, this))));
        if (rel != null) {
            graph.remove(rel);
        // If this locator is not used in anything else, we may remove it.
            if (graph.getIncidenceSet(locatorHandle).size() == 0) {
                graph.remove(locatorHandle, false);		
            }
        }
    }

    @Override
    public void addSubjectLocator(Locator subjectLocator) throws ModelConstraintException {
        HyperGraph graph = getGraph();
        HGHandle locatorHandle = HGTMUtil.ensureLocatorHandle(graph, subjectLocator);
        graph.add(new HGRel(HGTM.SubjectLocator, new HGHandle[]{locatorHandle, getHandle(graph, this)}),
                HGTM.hSubjectLocator);
    }

    @Override
    public Set<Locator> getSubjectLocators() {
        HyperGraph graph = getGraph();
        final HGHandle handle = getHandle(graph, this);
        return handle == null ? null : HGTMUtil.getRelatedObjects(graph, HGTM.hSubjectLocator, null, handle);
    }

    @Override
    public void removeSubjectLocator(Locator subjectLocator) {
        HyperGraph graph = getGraph();
        HGHandle locatorHandle = graph.getHandle(subjectLocator);
        HGHandle rel = hg.findOne(graph, hg.and(hg.type(HGTM.hSubjectLocator), hg.orderedLink(locatorHandle, getHandle(graph, this))));
        if (rel != null) {
            graph.remove(rel);
            // If this locator is not used in anything else, we may remove it.
            if (graph.getIncidenceSet(locatorHandle).size() == 0) {
                graph.remove(locatorHandle, false);		
            }

        }
    }

    @Override
    public void addType(Topic type) {
        HyperGraph graph = getGraph();
        HGHandle tHandle = getHandle(graph, type);
        graph.add(new HGRel(HGTM.TypeOf, new HGHandle[]{tHandle, getHandle(graph, this)}),
                HGTM.hTypeOf);
    }

    @Override
    public Set<Topic> getTypes() {
        return HGTMUtil.<Topic>getRelatedObjects(this, HGTM.hTypeOf, false);
    }

    @Override
    public boolean removeType(Topic type) {
        HyperGraph graph = getGraph();
        HGHandle typeHandle = getHandle(graph, type);
        HGHandle rel = hg.findOne(graph, hg.and(hg.type(HGTM.hTypeOf), hg.orderedLink(typeHandle, getHandle(graph, this))));
        if (rel != null) {
            graph.remove(rel);
            return true;
        }
        return false;
    }

    @Override
    public void addOccurrence(Occurrence occurrence) {
        HyperGraph graph = getGraph();
        HGHandle occurrenceHandle = add(graph, occurrence);
        graph.add(new HGRel(HGTM.OccurenceOf, new HGHandle[]{occurrenceHandle, getHandle(graph, this)}),
                HGTM.hOccurrenceOf);
    }

    @Override
    public Set<Occurrence> getOccurrences() {
        return HGTMUtil.getRelatedObjects(this, HGTM.hOccurrenceOf, false);
    }

    @Override
    public void removeOccurrence(Occurrence occurrence) {
        HyperGraph graph = getGraph();
        graph.remove(getHandle(graph, occurrence), false);
    }

    @Override
    public Reifiable getReified() {
        HyperGraph graph = getGraph();
        final HGConstructSupport reifiedSupport = (HGConstructSupport) HGTMUtil.getOneRelated(graph, HGTM.hReifierOf, getHandle(graph, this), null);
        return reifiedSupport == null ? null : (Reifiable) reifiedSupport.getOwner();
    }

    @Override
    public void setReified(Reifiable reifiable) {
    }

    @Override
    public void addRolePlayed(Role role) {
        
    }

    @Override
    public Set<Role> getRolesPlayed() {
        Set<Role> result = new HashSet<Role>();
        HGSearchResult<HGRoleSupport> rs = null;
        try {
            HyperGraph graph = getGraph();
            HGHandle thisH = getHandle(graph, this);
            if (thisH == null) {
                return null;
            }
            rs = graph.find(hg.apply(hg.deref(graph),
                    hg.and(hg.type(HGRoleSupport.class),
                            hg.incident(thisH))));
            while (rs.hasNext()) {
                HGRoleSupport role = rs.next();
                if (thisH.equals(role.getTargetAt(0))) {
                    result.add(role.getOwner());
                }
            }
            return result;
        } finally {
            HGUtils.closeNoException(rs);
        }
    }

    @Override
    public void removeRolePlayed(Role role) {
//        HyperGraph graph = getGraph();
//        final HGHandle roleHandle = getHandle(graph, role);
        // roleHandle may be null e.g. if AssociationImpl.removeRole(RoleImpl role)
        // role.getPlayer().removeRolePlayed(role) and roleSupport.removeRole will both be called and try to remove the role. 
        // The second one will fail
//        if (roleHandle != null) {
//            graph.remove(roleHandle, false);
//        }
    }

    @Override
    public void addName(NameImpl name) {
        HyperGraph graph = getGraph();
        HGHandle nameHandle = add(graph, name);
        graph.add(new HGRel(HGTM.NameOf, new HGHandle[]{nameHandle, getHandle(graph, this)}),
                HGTM.hNameOf);
    }

    @Override
    public Set<NameImpl> getNames() {
        return HGTMUtil.getRelatedObjects(this, HGTM.hNameOf, false);
    }

    @Override
    public void removeName(NameImpl name) {
        HyperGraph graph = getGraph();
        graph.remove(getHandle(graph, name), false);
    }
}
