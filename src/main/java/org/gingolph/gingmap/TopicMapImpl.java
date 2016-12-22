package org.gingolph.gingmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.gingolph.gingmap.equality.Equality;
import org.gingolph.gingmap.equality.SAMEquality;
import org.gingolph.gingmap.equality.TMAPIEquality;
import org.gingolph.gingmap.event.TopicMapEventListener;
import org.gingolph.gingmap.index.IdentifierIndex;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;


public class TopicMapImpl extends AbstractConstruct<TopicMapSupport> implements TopicMap {

  private static final SAMEquality EQUALITY_FOR_MERGE = new SAMEquality();

  transient Collection<TopicMapEventListener> listeners = new ArrayList<>();

  TopicMapSystemImpl topicMapSystem;
  private final boolean autoMerge;
  private String id;
  private final ConstructSupportFactory supportFactory;
  private final transient Map<Class<?>, Index> indexes = new LinkedHashMap<>();  
  private final Equality equality;
  
  public TopicMapImpl(TopicMapSystemImpl topicMapSystem, boolean autoMerge,
      ConstructSupportFactory supportFactory) {
    super();
    this.topicMapSystem = topicMapSystem;
    this.autoMerge = autoMerge;
    this.supportFactory = supportFactory;
    String userSpecifiedEquality = topicMapSystem.getProperty(AbstractTopicMapSystemFactory.EQUALITY_PROPERTY, AbstractTopicMapSystemFactory.TMAPI_EQUALITY);    
    equality = AbstractTopicMapSystemFactory.SAM_EQUALITY.equals(userSpecifiedEquality)? new SAMEquality():new TMAPIEquality();
  }

  @Override
  public Construct getParent() {
    return null;
  }

  @Override
  public TopicMapImpl getTopicMap() {
    return this;
  }

  @Override
  public String getId() {
    if (id == null) {
      this.id = generateId(this);
    }
    return id;
  }

  @Override
  protected void notifyOwner() {
    support.setOwner(this);
  }
  
  @Override
  public Set<Topic> getTopics() {
    return new UnmodifiableCollectionSet<>(getNullSafeTopicImpls());
  }

  private List<TopicImpl> getNullSafeTopicImpls() {
    return support.getTopics();
  }

  @Override
  public LocatorImpl getLocator() {
    return support.getBaseLocator();
  }

  @Override
  public Locator createLocator(String value) throws MalformedIRIException {
    return topicMapSystem.createLocator(value);
  }

  IdentifierIndex getIdentifierIndex() {
    return getIndex(IdentifierIndex.class);
  }

  @Override
  public Construct getConstructByItemIdentifier(Locator itemIdentifier) {
    return getIdentifierIndex().getConstructByItemIdentifier(itemIdentifier);
  }

  public <T extends Construct> T getConstructByItemIdentifier(Locator itemIdentifier,
      Class<T> clazz) {
    Construct construct = getConstructByItemIdentifier(itemIdentifier);
    return clazz.isAssignableFrom(construct.getClass()) ? clazz.cast(construct) : null;
  }

  @Override
  public Construct getConstructById(String id) {
    return getIdentifierIndex().getConstructById(id);
  }

  @Override
  public Topic getTopicBySubjectIdentifier(Locator subjectIdentifier) {
    return getIdentifierIndex().getTopicBySubjectIdentifier(subjectIdentifier);
  }

  @Override
  public Topic createTopicBySubjectIdentifier(Locator subjectIdentifier)
      throws ModelConstraintException {
    Topic topic = getTopicBySubjectIdentifier(subjectIdentifier);
    if (topic != null) {
      return topic;
    }
    Construct construct = getConstructByItemIdentifier(subjectIdentifier);
    Topic newTopic = construct instanceof TopicImpl ? (TopicImpl) construct : doCreateTopic();
    newTopic.addSubjectIdentifier(subjectIdentifier);
    notifyListeners(listener -> listener.onSubjectIdentifierAdded(newTopic, subjectIdentifier));
    return newTopic;
  }

  @Override
  public Topic getTopicBySubjectLocator(Locator subjectLocator) {
    return getIdentifierIndex().getTopicBySubjectLocator(subjectLocator);
  }

  @Override
  public Topic createTopicBySubjectLocator(Locator subjectLocator) throws ModelConstraintException {
    Topic topic = getTopicBySubjectLocator(subjectLocator);
    if (topic != null) {
      return topic;
    }
    Topic newTopic = doCreateTopic();
    newTopic.addSubjectLocator(subjectLocator);
    notifyListeners(listener -> listener.onSubjectLocatorAdded(newTopic, subjectLocator));
    return newTopic;
  }

  @Override
  public Topic createTopicByItemIdentifier(Locator itemIdentifier)
      throws IdentityConstraintException, ModelConstraintException {
    Construct construct = getConstructByItemIdentifier(itemIdentifier);
    if (construct instanceof Topic) {
      return (Topic) construct;
    }
    if (construct != null) {
      throw new IdentityConstraintException(this, construct, itemIdentifier,
          "A construct with the specified item identifier exists but is not a Topic");
    }
    Topic topic = getTopicBySubjectIdentifier(itemIdentifier);
    if (topic == null) {
      topic = doCreateTopic();
    }
    topic.addItemIdentifier(itemIdentifier);
    return topic;
  }

  @Override
  public Topic createTopic() {
    Topic topic = doCreateTopic();
    topic.addItemIdentifier(createLocator("internal-" + topic.getId()));
    return topic;
  }

  private TopicImpl doCreateTopic() {
    TopicImpl topic = new TopicImpl(this);
    topic.setSupport(createTopicSupport());
    support.addTopic(topic);
    notifyListeners(listener -> listener.onConstructCreated(topic));
    return topic;
  }

  void removeTopic(Topic topic) {
    support.removeTopic(topic);
  }

  @Override
  protected void customRemove() {
    Collection<Association> associations = new ArrayList<>(getNullSafeAssociationImpls());
    for (Association association : associations) {
      association.remove();
    }
    Collection<Topic> topics = new ArrayList<>(getTopics());
    for (Topic topic : topics) {
      ((TopicImpl)topic).doRemove();
    }
    topicMapSystem.removeTopicMap(this);
  }

  @Override
  public Set<Association> getAssociations() {
    return new UnmodifiableCollectionSet<>(getNullSafeAssociationImpls());
  }

  private List<AssociationImpl> getNullSafeAssociationImpls() {
    return support.getAssociations();
  }

  @Override
  public AssociationImpl createAssociation(Topic type, Topic... scope)
      throws ModelConstraintException {
    return createAssociation(type, scope == null ? null : Arrays.asList(scope));
  }

  @Override
  public AssociationImpl createAssociation(Topic type, Collection<Topic> scope)
      throws ModelConstraintException {
    if (type == null) {
      throw new ModelConstraintException(this, "Null type is not allowed");
    }
    if (scope == null) {
      throw new ModelConstraintException(this, "Null scope is not allowed");
    }
    return doCreateAssociation(type, scope);
  }

  private AssociationImpl doCreateAssociation(Topic type, Collection<Topic> scope) {
    AssociationImpl association = new AssociationImpl(this);
    association.setSupport(createAssociationSupport());
    support.addAssociation(association);
    association.setType(type);
    association.setScope(scope);
    notifyListeners(listener -> listener.onConstructCreated(association));
    return association;
  }

  void removeAssociation(AssociationImpl association) {
    support.removeAssociation(association);
  }

  @Override
  public void close() {
    topicMapSystem.closeTopicMap(this);
  }

  @Override
  public void mergeIn(TopicMap other) throws ModelConstraintException {
    if (other == null) {
      throw new ModelConstraintException(this, "Null topic map not allowed");
    }
    if (other == this) {
      return;
    }
    TopicMapImpl otherImpl = (TopicMapImpl)other;
    final Stream<AssociationImpl> otherAssociations = otherImpl.getNullSafeAssociationImpls().stream();
    otherAssociations.forEach(this::mergeAssociationIn);
    
    final Stream<TopicImpl> otherTopics = otherImpl.getNullSafeTopicImpls().stream();
    // We filter out topics with rolesPlayed because those have been handled above.
//    otherTopics.filter(topic -> topic.getNullSafeRolePlayedImpls().isEmpty()).forEach(this::mergeTopicIn);
    otherTopics.forEach(this::mergeTopicIn);
  }

  private void mergeTopicIn(TopicImpl otherTopic) {
    Optional<TopicImpl> sameTopic = getNullSafeTopicImpls().stream().filter(topic -> getEqualityForMerge().equals( topic, otherTopic)).findAny();
    TopicImpl mergee = sameTopic.isPresent() ? sameTopic.get() : doCreateTopic();
    mergee.importIn(otherTopic, false);
  }

  private void mergeAssociationIn(AssociationImpl otherAssociation) {
    Optional<AssociationImpl> sameAssociation = getNullSafeAssociationImpls().stream().filter(association -> getEqualityForMerge().equals( association, otherAssociation)).findAny();
    if (!sameAssociation.isPresent()) {
      AssociationImpl mergee = doCreateAssociation(otherAssociation.getType(), otherAssociation.getScope());
      otherAssociation.getNullSafeRoleImpls().stream().forEach(otherRole -> mergee.createRole(otherRole.getType(), otherRole.getPlayer()));
    } // By definition of getEqualityForMerge(), sameAssociation.get() has the same type, scope and roles than otherAssociation 
  }
  
  @Override
  public <I extends Index> I getIndex(Class<I> type) {
    I index = type.cast(indexes.get(type));
    if (index == null) {
      index = support.getIndex(type);
      indexes.put(type, index);
    }
    return index;
  }

  @Override
  public Topic getReifier() {
    return support.getReifier();
  }

  @Override
  public void setReifier(Topic reifier) throws ModelConstraintException {
    ReifierHelper.setReifier(this, reifier, this::doSetReifier);
  }

  protected void doSetReifier(TopicImpl reifier) {
    support.setReifier(reifier);
  }

  protected final String generateId(AbstractConstruct<?> construct) {
    return support.generateId(construct);
  }

  boolean isAutoMerge() {
    return autoMerge;
  }

  public void notifyListeners(Consumer<? super TopicMapEventListener> action) {
    listeners.forEach(action);
  }

  public <T extends TopicMapEventListener> T registerListener(T listener) {
    listeners.add(listener);
    return listener;
  }

  public NameImpl createName(TopicImpl topic) {
    NameImpl name = new NameImpl(this, topic);
    name.setSupport(createNameSupport());
    return name;
  }

  public OccurrenceImpl createOccurrence(TopicImpl topic) {
    OccurrenceImpl occurrence = new OccurrenceImpl(this, topic);
    occurrence.setSupport(createOccurrenceSupport());
    return occurrence;
  }

  public VariantImpl createVariant(NameImpl name, Locator datatype, Object value) {
    VariantImpl variant = new VariantImpl(this, name);
    variant.setSupport(createVariantSupport());
    variant.setValue(datatype, value);
    return variant;
  }

  AssociationSupport createAssociationSupport() {
    return supportFactory.createAssociationSupport();
  }

  RoleSupport createRoleSupport() {
    return supportFactory.createRoleSupport();
  }

  NameSupport createNameSupport() {
    return supportFactory.createNameSupport();
  }

  OccurrenceSupport createOccurrenceSupport() {
    return supportFactory.createOccurrenceSupport();
  }

  TopicSupport createTopicSupport() {
    return supportFactory.createTopicSupport();
  }

  VariantSupport createVariantSupport() {
    return supportFactory.createVariantSupport();
  }
  
  public Equality getEquality() {
    return equality;
  }
  
  SAMEquality getEqualityForMerge() {
    return EQUALITY_FOR_MERGE;
  }
  
  @Override
  protected boolean equalsFromEquality(Object otherObjectOfSameClass) {
    return this == otherObjectOfSameClass;
  }
  
  @Override
  protected int hashCodeFromEquality() {
    return System.identityHashCode(this);
  }  
  
}
