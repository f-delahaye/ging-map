package org.gingolph.gingmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gingolph.gingmap.equality.SAMEquality;
import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

public class TopicImpl extends TopicMapItem<TopicMapImpl, TopicSupport>
    implements Topic, TypedConstruct, Reifier {

  public TopicImpl(TopicMapImpl topicMap) {
    super(topicMap, topicMap);
  }

  @Override
  protected void notifyOwner() {
    support.setOwner(this);
  }
  
  @Override
  public void addItemIdentifier(Locator identifier) throws IdentityConstraintException {
    Topic existingTopic = null;
    try {
      super.checkForAddItemIdentifier(identifier);
    } catch (IdentityConstraintException ice) {
      // From a general point of view, duplicate item identifiers are not allowed for Constructs.
      // However for topics we can try to merge them.
      Construct existingItemIdentifier = ice.getExisting();
      if (existingItemIdentifier instanceof Topic) {
        existingTopic = (Topic) existingItemIdentifier;
      } else {
        throw ice;
      }
    }
    if (existingTopic == null) {
      Topic existingSubjectIdentifier = topicMap.getTopicBySubjectIdentifier(identifier);
      if (existingSubjectIdentifier != null && !existingSubjectIdentifier.equals(this)) {
        existingTopic = existingSubjectIdentifier;
      }
    }
    if (existingTopic == null) {
      importItemIdentifier(identifier);
    } else if (topicMap.isAutoMerge()) {
      mergeIn(existingTopic);
    } else {
      throw new IdentityConstraintException(this, existingTopic, identifier,
          "Identifier is already in use");
    }
  }

  @Override
  public void remove() {
    checkRemove();
    super.remove();
  }

  protected void checkRemove() {
    if (getReified() != null) {
      throw new TopicInUseException(this, "Topic used as reifier");
    }
    if (!getRolesPlayed().isEmpty()) {
      throw new TopicInUseException(this, "Topic used as player");
    }
    TypeInstanceIndex typeInstanceIndex = topicMap.getIndex(TypeInstanceIndex.class);
    if (!typeInstanceIndex.getTopics(this).isEmpty()) {
      throw new TopicInUseException(this, "Topic used as topic type");
    }
    if (!typeInstanceIndex.getAssociations(this).isEmpty()) {
      throw new TopicInUseException(this, "Topic used as association type");
    }
    if (!typeInstanceIndex.getRoles(this).isEmpty()) {
      throw new TopicInUseException(this, "Topic used as role type");
    }
    if (!typeInstanceIndex.getNames(this).isEmpty()) {
      throw new TopicInUseException(this, "Topic used as name type");
    }
    if (!typeInstanceIndex.getOccurrences(this).isEmpty()) {
      throw new TopicInUseException(this, "Topic used as occurrence type");
    }
    ScopedIndex scopedIndex = topicMap.getIndex(ScopedIndex.class);
    if (!scopedIndex.getAssociations(this).isEmpty()) {
      throw new TopicInUseException(this, "Topic used as association theme");
    }
    if (!scopedIndex.getNames(this).isEmpty()) {
      throw new TopicInUseException(this, "Topic used as name theme");
    }
    if (!scopedIndex.getOccurrences(this).isEmpty()) {
      throw new TopicInUseException(this, "Topic used as occurrence theme");
    }
    if (!scopedIndex.getVariants(this).isEmpty()) {
      throw new TopicInUseException(this, "Topic used as association theme");
    }
  }

  @Override
  protected void customRemove() {

    Set<Locator> subjectIdentifiers = support.getSubjectIdentifiers();
    if (subjectIdentifiers != null) {
      new ArrayList<>(subjectIdentifiers).forEach(identifier -> removeSubjectIdentifier(identifier));
    }
    Set<Locator> subjectLocators = support.getSubjectLocators();
    if (subjectLocators != null) {
      new ArrayList<>(subjectLocators).forEach(locator -> removeSubjectLocator(locator));
    }
    if (getReified() != null) {
      getReified().setReifier(null);
    }

    Collection<Name> names = new ArrayList<>(getNames());
    names.forEach(name -> name.remove());

    Collection<Occurrence> occurrences = new ArrayList<>(getOccurrences());
    occurrences.forEach(occurrence -> occurrence.remove());

    getParent().removeTopic(this);
  }

  @Override
  public Set<Locator> getSubjectIdentifiers() {
    Set<Locator> subjectIdentifiers = support.getSubjectIdentifiers();
    return subjectIdentifiers == null ? Collections.emptySet()
        : Collections.unmodifiableSet(subjectIdentifiers);
  }

  @Override
  public void addSubjectIdentifier(Locator identifier)
      throws IdentityConstraintException, ModelConstraintException {
    if (identifier == null) {
      throw new ModelConstraintException(this, "Null identifier not allowed");
    }
    TopicImpl existingTopic = (TopicImpl) topicMap.getTopicBySubjectIdentifier(identifier);

    if (existingTopic != null) {
      if (!existingTopic.equals(this)) {
        if (isAutoMerge(topicMap)) {
          this.mergeIn(existingTopic);
          return;
        } else {
          throw new IdentityConstraintException(this, existingTopic, identifier,
              "Duplicate subject identifiers not allowed:"+identifier);
        }
      }
    }

    TopicMapItem<?,?> existingItemIdentifier = (TopicMapItem<?, ?>) topicMap.getConstructByItemIdentifier(identifier);
    if (existingItemIdentifier == null || this.equals(existingItemIdentifier)) {
      importSubjectIdentifier(identifier);
    } else if (existingItemIdentifier instanceof TopicImpl) {
      if (isAutoMerge(topicMap)) {
        this.mergeIn((TopicImpl) existingItemIdentifier);
      } else {
        throw new IdentityConstraintException(this, existingItemIdentifier, identifier,
            "Identifier is already used as an item identifier");
      }
    }
  }

  protected boolean isAutoMerge(TopicMapImpl topicMap) {
    return topicMap.isAutoMerge();
  }

  protected void importSubjectIdentifier(Locator identifier) {
    support.addSubjectIdentifier(identifier);
    getTopicMap().notifyListeners(listener -> listener.onSubjectIdentifierAdded(this, identifier));
  }

  @Override
  public void removeSubjectIdentifier(Locator identifier) {
    support.removeSubjectIdentifier(identifier);
    getTopicMap().notifyListeners(listener -> listener.onSubjectIdentifierRemoved(identifier));
  }

  @Override
  public Set<Locator> getSubjectLocators() {
    Set<Locator> subjectLocators = support.getSubjectLocators();
    return subjectLocators == null ? Collections.emptySet()
        : Collections.unmodifiableSet(subjectLocators);
  }

  @Override
  public void addSubjectLocator(Locator locator)
      throws IdentityConstraintException, ModelConstraintException {
    if (locator == null) {
      throw new ModelConstraintException(this, "Null identifier not allowed");
    }
    Topic existingTopic = topicMap.getTopicBySubjectLocator(locator);
    if (existingTopic != null) {
      if (existingTopic != this) {
        if (isAutoMerge(topicMap)) {
          this.mergeIn(existingTopic);
        } else {
          throw new IdentityConstraintException(this, existingTopic, locator,
              "Identifier already in use");
        }
      }
    } else {
      importSubjectLocator(locator);
    }
  }

  protected void importSubjectLocator(Locator locator) {
    support.addSubjectLocator(locator);
    getTopicMap().notifyListeners(listener -> listener.onSubjectLocatorAdded(this, locator));
  }

  @Override
  public void removeSubjectLocator(Locator locator) {
    support.removeSubjectLocator(locator);
    getTopicMap().notifyListeners(listener -> listener.onSubjectLocatorRemoved(locator));
  }

  @Override
  public Set<Name> getNames() {
    List<NameImpl> names = support.getNames();    
    return names == null || names.size() == 0 ? Collections.emptySet() : new UnmodifiableCollectionSet<>(names);
  }

  public Stream<NameImpl> names() {
    List<NameImpl> names = support.getNames();
    return names == null ? Stream.empty():names.stream();
  }

  @Override
  public Set<Name> getNames(Topic type) {
    if (type == null) {
      throw new IllegalArgumentException("Null not allowed");
    }
    final List<NameImpl> names = names().filter(name -> name.getType().equals(type)).collect(Collectors.toList());
    return new UnmodifiableCollectionSet<>(names);
  }

  @Override
  public Name createName(Topic type, String value, Topic... scope) throws ModelConstraintException {
    if (scope == null) {
      throw new ModelConstraintException(this, "Null scope not allowed");
    }
    if (type.getTopicMap() != getTopicMap()) {
      throw new ModelConstraintException(this, "Different topic maps not allowed");
    }
    return createName(type, value, Arrays.asList(scope));
  }

  @Override
  public NameImpl createName(Topic type, String value, Collection<Topic> scope)
      throws ModelConstraintException {
    if (type == null) {
      throw new ModelConstraintException(this, "Null type not allowed");
    }
    if (scope == null) {
      throw new ModelConstraintException(this, "Null scope not allowed");
    }
    NameImpl name = getTopicMap().createName(this);
    support.addName(name);
    name.setScope(scope);
    name.setType(type);
    name.setValue(value);
    getTopicMap().notifyListeners(listener -> listener.onConstructCreated(name));
    return name;
  }

  @Override
  public Name createName(String value, Topic... scope) throws ModelConstraintException {
    if (scope == null) {
      throw new ModelConstraintException(this, "Null scope not allowed");
    }
    return createName(value, Arrays.asList(scope));
  }

  @Override
  public Name createName(String value, Collection<Topic> scope) throws ModelConstraintException {
    return createName(getTopicMap().createTopicBySubjectIdentifier(LocatorImpl.DEFAULT_NAME_TYPE),
        value, scope);
  }

  void removeName(NameImpl name) {
    support.removeName(name);
  }

  @Override
  public Set<Occurrence> getOccurrences() {
    List<OccurrenceImpl> occurrences = support.getOccurrences();
    return occurrences == null || occurrences.isEmpty() ? Collections.emptySet() : new UnmodifiableCollectionSet<>(occurrences);
  }

  public Stream<OccurrenceImpl> occurrences() {
    List<OccurrenceImpl> occurrences = support.getOccurrences();
    return occurrences == null?Stream.empty() : occurrences.stream();
  }

  @Override
  public Set<Occurrence> getOccurrences(Topic type) {
    if (type == null) {
      throw new IllegalArgumentException("Null type not allowed");
    }
    return new UnmodifiableCollectionSet<>(occurrences().filter(occurrence -> occurrence.getType().equals(type)).collect(Collectors.toList()));
  }

  @Override
  public Occurrence createOccurrence(Topic type, String value, Topic... scope)
      throws ModelConstraintException {
    if (scope == null) {
      throw new ModelConstraintException(this, "Null scope not allowed");
    }
    return createOccurrence(type, value, Arrays.asList(scope));
  }

  @Override
  public OccurrenceImpl createOccurrence(Topic type, String value, Collection<Topic> scope)
      throws ModelConstraintException {
    return createOccurrence(type, value, LocatorImpl.XSD_STRING, scope);
  }

  @Override
  public Occurrence createOccurrence(Topic type, Locator value, Topic... scope)
      throws ModelConstraintException {
    return createOccurrence(type, value, Arrays.asList(scope));
  }

  @Override
  public Occurrence createOccurrence(Topic type, String value, Locator dataType, Topic... scope)
      throws ModelConstraintException {
    return createOccurrence(type, value, dataType, Arrays.asList(scope));
  }

  @Override
  public OccurrenceImpl createOccurrence(Topic type, String value, Locator dataType,
      Collection<Topic> scope) throws ModelConstraintException {
    return createOccurrence(type, dataType, value, scope);
  }

  @Override
  public Occurrence createOccurrence(Topic type, Locator value, Collection<Topic> scope)
      throws ModelConstraintException {
    return createOccurrence(type, LocatorImpl.XSD_ANY_URI, value, scope);
  }

  private OccurrenceImpl createOccurrence(Topic type, Locator datatype, Object value,
      Collection<Topic> scope) throws ModelConstraintException {
    if (type == null) {
      throw new ModelConstraintException(this, "Null type not allowed");
    }
    if (scope == null) {
      throw new ModelConstraintException(this, "Null scope not allowed");
    }
    OccurrenceImpl occurrence = getTopicMap().createOccurrence(this);
    support.addOccurrence(occurrence);
    occurrence.setType(type);
    occurrence.setScope(scope);
    occurrence.setValue(datatype, value);
    getTopicMap().notifyListeners(listener -> listener.onConstructCreated(occurrence));
    return occurrence;
  }

  protected void removeOccurrence(Occurrence occurrence) {
    support.removeOccurrence(occurrence);
  }

  void removeRolePlayed(Role role) {
    support.removeRolePlayed(role);
  }

  @Override
  public Set<Role> getRolesPlayed() {
    final List<RoleImpl> rolesPlayed = getNullSafeRolePlayedImpls();
    return rolesPlayed.isEmpty() ? Collections.emptySet() : new UnmodifiableCollectionSet<>(rolesPlayed);
  }

  public List<RoleImpl> getNullSafeRolePlayedImpls() {
    final List<RoleImpl> rolesPlayed = support.getRolesPlayed();
    return rolesPlayed == null?Collections.emptyList():rolesPlayed;
  }

  @Override
  public Set<Role> getRolesPlayed(Topic roleType) {
    if (roleType == null) {
      throw new IllegalArgumentException("Null type not allowed");
    }
    List<RoleImpl> rolesPlayed = getNullSafeRolePlayedImpls().stream().filter(role -> role.getType().equals(roleType)).collect(Collectors.toList());
    return new UnmodifiableCollectionSet<>(rolesPlayed); 
  }

  @Override
  public Set<Role> getRolesPlayed(Topic roleType, Topic associationType) {
    if (roleType == null) {
      throw new IllegalArgumentException("Null type not allowed");
    }
    if (associationType == null) {
      throw new IllegalArgumentException("Null association type not allowed");
    }
    List<RoleImpl> rolesPlayed = getNullSafeRolePlayedImpls().stream().filter(role -> role.getType().equals(roleType) && role.getParent().getType().equals(associationType)).collect(Collectors.toList());
    return new UnmodifiableCollectionSet<>(rolesPlayed);    
  }

  void addRolePlayed(RoleImpl role) {
    support.addRolePlayed(role);
  }

  @Override
  public Set<Topic> getTypes() {
    Set<TopicImpl> types = support.getTypes();
    return types == null ? Collections.emptySet() : Collections.unmodifiableSet(types);
  }

  @Override
  public void addType(Topic type) throws ModelConstraintException {
    TypedInstanceHelper.setType(this, type, this::doAddType);
  }

  protected void doAddType(Topic type) {
    support.addType((TopicImpl)type, getTopicMap().getEquality());
  }

  @Override
  public void removeType(Topic type) {
    if (support.removeType(type)) {
      getTopicMap().notifyListeners(listener -> listener.onTypeChanged(this, null, type));
    }
  }

  @Override
  public void mergeIn(Topic other) throws ModelConstraintException {
    if (other == null) {
      throw new ModelConstraintException(this, "Null topic not allowed");
    }
    if (getTopicMap() != other.getTopicMap()) {
      throw new ModelConstraintException(this, "Different topic maps not allowed");
    }
    if (getReified() != null && other.getReified() != null && !getReified().equals(other.getReified())) {
        throw new ModelConstraintException(this, "Different reified not allowed");
    }
    if (!this.equals(other)) {
      importIn(other, true);
    }
  }

  protected void importIn(Topic other, boolean merge) {
    TopicImpl otherTopic = (TopicImpl) other;
    // Store properties of other BEFORE we change its delegate.
    TopicSupport otherTopicData = otherTopic.support;

    copyLocators(otherTopic);
    final Collection<Topic> otherTypes = new ArrayList<>(otherTopic.getTypes());

    otherTypes.removeAll(getTypes());
    otherTypes.forEach(otherType -> addType(otherType));

    final Collection<NameImpl> otherNames = otherTopic.names().collect(Collectors.toList());
    final Collection<OccurrenceImpl> otherOccurrences = otherTopic.occurrences().collect(Collectors.toList());
    final Collection<RoleImpl> otherRolesPlayed = otherTopicData.getRolesPlayed();

    SAMEquality equalityForMerge = getTopicMap().getEqualityForMerge();
    
    // and work off otherTopic's actual data to do the actual merging.
    otherNames.stream().map(otherName -> (NameImpl)otherName).forEach(otherName -> {
      Optional<NameImpl> equivalentName = names().filter(name -> equalityForMerge.equals(name, otherName)).findAny();
      NameImpl mergee = equivalentName.orElseGet(() -> createName(otherName.getType(), otherName.getValue(), otherName.getScope()));
      mergee.importIn(otherName, merge);
    });
    otherOccurrences.stream().forEach(otherOccurrence -> {
      Optional<OccurrenceImpl> equivalentOccurrence = 
          occurrences().filter(occurrence -> equalityForMerge.equals(occurrence, otherOccurrence)).findAny();
      OccurrenceImpl mergee = equivalentOccurrence.orElseGet(() -> createOccurrence(otherOccurrence.getType(), otherOccurrence.getValue(),otherOccurrence.getScope()));
      mergee.importIn(otherOccurrence, merge);
    });


    TopicSupport otherTopicSupport = otherTopic.support;
    if (merge) {
//      getParent().removeTopic(otherTopic);
      // Change otherTopic's data so from now on it will be the same as topic ...
      // (e.g. 2 names whose types are resp. this and other will be deemed identical which is what
      // we want)
      otherTopic.support = this.support;
    }

    if (otherRolesPlayed != null) {
      for (RoleImpl otherRole: otherRolesPlayed) {
        // otherRole.setPlayer(this); not needed because player is manged by TopicSupport so switching the support also changed the player
        AssociationImpl otherAssociation = otherRole.getParent();
        
        Optional<AssociationImpl> equivalentAssociation = getNullSafeRolePlayedImpls().stream().map(role -> role.getParent()).filter(candidateAssociation -> equalityForMerge.associationEquals(candidateAssociation, otherAssociation, false)).findAny();
        if (equivalentAssociation.isPresent()) {
          getTopicMap().removeAssociation(otherAssociation);
          // should we import otherAssociation's item identifiers and all the roles' item identifiers too?
        } else {
          otherRole.setPlayer(this);
        }
      };
    }
    
    if (merge) {
      // revert back to the original support so we can remove otherTopic stuff (if we keep otherTopic.support set to this.support we'll actually delete this!!)
      // Other alternatives would include changing all references to otherTopic by this (in themes, types, ...) which would avoid setting the support back and forth...
      otherTopic.support = otherTopicSupport;
      getParent().removeTopic(otherTopic);
    }
    
  }

  public void copyLocators(TopicImpl otherTopic) {
    otherTopic.getSubjectIdentifiers().forEach(this::importSubjectIdentifier);
    otherTopic.getSubjectLocators().forEach(this::importSubjectLocator);
    otherTopic.getItemIdentifiers().forEach(this::importItemIdentifier);
  }

  @Override
  protected boolean equalsFromEquality(Object otherObjectOfSameClass) {
    return getTopicMap().getEquality().equals(this, (TopicImpl)otherObjectOfSameClass);
  }

  @Override
  public String toString() {
    return "[identifiers="+getItemIdentifiers()+"]";
  }

  @Override
  protected int hashCodeFromEquality() {
    return getTopicMap().getEquality().hashCode(this);
  }  

//  public boolean matches(TopicImpl otherTopic) {
//    return getItemIdentifiers().equals(otherTopic.getItemIdentifiers()) && getSubjectIdentifiers().equals(otherTopic.getSubjectIdentifiers()) && getSubjectLocators().equals(otherTopic.getSubjectLocators())
//        && getNames().equals(otherTopic.getNames()) && getOccurrences().equals(otherTopic.getOccurrences());
//  }

  
  @Override
  public Reifiable getReified() {
    return support.getReified();
  }

  @Override
  public void setReified(Reifiable reifiable) {
    support.setReified(reifiable);
  }

}
