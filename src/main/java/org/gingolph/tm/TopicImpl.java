package org.gingolph.tm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
              "Duplicate item identifiers not allowed");
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
    Set<NameImpl> names = support.getNames();
    return names == null ? Collections.emptySet() : Collections.unmodifiableSet(names);
  }

  @Override
  public Set<Name> getNames(Topic type) {
    if (type == null) {
      throw new IllegalArgumentException("Null not allowed");
    }
    final Set<Name> names = getNames();
    return names.stream().filter(name -> name.getType().equals(type)).collect(Collectors.toSet());
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
  public Name createName(Topic type, String value, Collection<Topic> scope)
      throws ModelConstraintException {
    if (type == null) {
      throw new ModelConstraintException(this, "Null type not allowed");
    }
    if (scope == null) {
      throw new ModelConstraintException(this, "Null scope not allowed");
    }
    NameImpl name = getTopicMap().createName(this, value);
    support.addName(name);
    name.setScope(scope);
    name.setType(type);
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
    Set<Occurrence> occurrences = support.getOccurrences();
    return occurrences == null ? Collections.emptySet() : Collections.unmodifiableSet(occurrences);
  }

  @Override
  public Set<Occurrence> getOccurrences(Topic type) {
    if (type == null) {
      throw new IllegalArgumentException("Null type not allowed");
    }
    if (getOccurrences() == null) {
      return Collections.emptySet();
    }
    return getOccurrences().stream().filter(occurrence -> occurrence.getType() == type)
        .collect(Collectors.toSet());
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
  public Occurrence createOccurrence(Topic type, String value, Collection<Topic> scope)
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
  public Occurrence createOccurrence(Topic type, String value, Locator dataType,
      Collection<Topic> scope) throws ModelConstraintException {
    return createOccurrence(type, dataType, value, scope);
  }

  @Override
  public Occurrence createOccurrence(Topic type, Locator value, Collection<Topic> scope)
      throws ModelConstraintException {
    return createOccurrence(type, LocatorImpl.XSD_ANY_URI, value, scope);
  }

  private Occurrence createOccurrence(Topic type, Locator datatype, Object value,
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
    final Set<Role> rolesPlayed = support.getRolesPlayed();
    return rolesPlayed == null ? Collections.emptySet() : Collections.unmodifiableSet(rolesPlayed);
  }

  @Override
  public Set<Role> getRolesPlayed(Topic type) {
    if (type == null) {
      throw new IllegalArgumentException("Null type not allowed");
    }
    return getRolesPlayed().stream().filter(role -> role.getType() == type)
        .collect(Collectors.toSet());
  }

  @Override
  public Set<Role> getRolesPlayed(Topic roleType, Topic associationType) {
    if (roleType == null) {
      throw new IllegalArgumentException("Null type not allowed");
    }
    if (associationType == null) {
      throw new IllegalArgumentException("Null association type not allowed");
    }
    return getRolesPlayed().stream()
        .filter(role -> role.getType() == roleType && role.getParent().getType() == associationType)
        .collect(Collectors.toSet());
  }

  void addRolePlayed(Role role) {
    support.addRolePlayed(role);
  }

  @Override
  public Set<Topic> getTypes() {
    Set<Topic> types = support.getTypes();
    return types == null ? Collections.emptySet() : Collections.unmodifiableSet(types);
  }

  @Override
  public void addType(Topic type) throws ModelConstraintException {
    TypedInstanceHelper.setType(this, type, this::doAddType);
  }

  protected void doAddType(Topic type) {
    support.addType(type);
    getTopicMap().notifyListeners(listener -> listener.onTypeChanged(this, type, null));
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

    otherTopic.getSubjectIdentifiers().forEach(identifier -> importSubjectIdentifier(identifier));
    otherTopic.getSubjectLocators().forEach(locator -> importSubjectLocator(locator));
    otherTopic.getItemIdentifiers().forEach(identifier -> importItemIdentifier(identifier));
    final Collection<Topic> otherTypes = new ArrayList<>(otherTopic.getTypes());

    otherTypes.removeAll(getTypes());
    otherTypes.forEach(otherType -> addType(otherType));

    final Collection<Name> otherNames = new ArrayList<>(otherTopic.getNames());
    final Collection<Occurrence> otherOccurrences = new ArrayList<>(otherTopic.getOccurrences());
    final Collection<Role> otherRolesPlayed = otherTopicData.getRolesPlayed();

    // and work off otherTopic's actual data to do the actual merging.
    otherNames.stream().forEach(otherName -> {
      Optional<Name> equivalentName = 
          getNames().stream().filter(name -> name.equals(otherName)).findAny();
      Name mergee = equivalentName.orElseGet(() -> createName(otherName.getType(), otherName.getValue(), otherName.getScope()));
      ((NameImpl)mergee).importIn(otherName, merge);
    });
    otherOccurrences.stream().forEach(otherOccurrence -> {
      Optional<Occurrence> equivalentOccurrence = getOccurrences().stream().filter(occurrence -> occurrence.equals(otherOccurrence)).findAny();
      Occurrence mergee = equivalentOccurrence.orElseGet(() -> createOccurrence(otherOccurrence.getType(), otherOccurrence.getValue(),otherOccurrence.getScope()));
      ((OccurrenceImpl) mergee).importIn(otherOccurrence, merge);
    });


    if (merge) {
// Change otherTopic's data so from now on it will be the same as topic ...
// (e.g. 2 names whose types are resp. this and other will be deemed identical which is what we want)
      otherTopic.doRemove();
      otherTopic.support = this.support;
    }

    if (otherRolesPlayed != null) {
      otherRolesPlayed.stream().map(role -> role.getParent()).map(association -> (AssociationImpl)association).forEach(otherAssociation -> {
        Optional<AssociationImpl> equivalentAssociation = findEquivalentAssociation(getRolesPlayed(), otherAssociation);
        AssociationImpl mergee = equivalentAssociation.orElseGet(() -> getParent().createAssociation(otherAssociation.getType(), otherAssociation.getScope()));
        mergee.importIn(otherAssociation, merge);
      });
    }
  }

  private Optional<AssociationImpl> findEquivalentAssociation(Set<Role> rolesPlayed, AssociationImpl association) {
//    getRolesPlayed().stream().map(role -> role.getParent()).map(association -> (AssociationImpl)association)
//    .filter(association -> association.equals(otherAssociation)).findAny();
    for (Role role: rolesPlayed) {
      AssociationImpl candidate = (AssociationImpl) role.getParent();
      if (candidate.equals(association)) {
        return Optional.of(candidate);
      }
    }
    return Optional.empty();
  }

  @Override
  public String toString() {
    return "id "+getId();
  }

  @Override
  public int hashCode() {
    throw new UnsupportedOperationException();
  }

  public boolean deepEquals(Topic otherTopic) {
    return getItemIdentifiers().equals(otherTopic.getItemIdentifiers()) && getSubjectIdentifiers().equals(otherTopic.getSubjectIdentifiers()) && getSubjectLocators().equals(otherTopic.getSubjectLocators()) && getNames().equals(otherTopic.getNames()) && getOccurrences().equals(otherTopic.getOccurrences()) && getTypes().equals(otherTopic.getTypes());
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof TopicImpl && equals((TopicImpl)other);
  }
    
  protected boolean equals(Topic otherItem) {
    if (!(otherItem instanceof TopicImpl)) {
      return false;
    }
    TopicImpl otherTopic = (TopicImpl) otherItem;
    if (otherTopic.getId().equals(this.getId())) {
      return true;
    }
    // Optimization:
    // If automerge, 2 topics which are equals would have been merged and the above test would have
    // returned true
    // commented out: what if equals is called when trying to merge?? 2 Topics may be equals but may
    // not have been merged yet ...
    // if (isAutoMerge(getTopicMap())) {
    // return false;
    // }
    final Collection<Locator> subjectIdentifiers = support.getSubjectIdentifiers();
    final Collection<Locator> subjectLocators = support.getSubjectLocators();
    final Collection<Locator> itemIdentifiers = support.getItemIdentifiers();

    return subjectIdentifiers != null
        && subjectIdentifiers.stream()
            .anyMatch(identifier -> otherTopic.getSubjectIdentifiers().contains(identifier)
                || otherTopic.getItemIdentifiers().contains(identifier))
        || subjectLocators != null && subjectLocators.stream()
            .anyMatch(identifier -> otherTopic.getSubjectLocators().contains(identifier))
        || itemIdentifiers != null && (itemIdentifiers.stream()
            .anyMatch(identifier -> otherTopic.getItemIdentifiers().contains(identifier))
            || otherTopic.getSubjectIdentifiers().stream()
                .anyMatch(identifier -> itemIdentifiers.contains(identifier)));
  }

  public boolean matches(TopicImpl otherTopic) {
    return getItemIdentifiers().equals(otherTopic.getItemIdentifiers()) && getSubjectIdentifiers().equals(otherTopic.getSubjectIdentifiers()) && getSubjectLocators().equals(otherTopic.getSubjectLocators())
        && getNames().equals(otherTopic.getNames()) && getOccurrences().equals(otherTopic.getOccurrences());
  }

  
  @Override
  public Reifiable getReified() {
    return support.getReified();
  }

  @Override
  public void setReified(Reifiable reifiable) {
    support.setReified(reifiable);
  }

}
