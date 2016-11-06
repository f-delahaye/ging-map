package org.gingolph.tm.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import org.gingolph.tm.AssociationImpl;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.TypedConstruct;
import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.index.TypeInstanceIndex;


public final class TypeInstanceIndexImpl extends AbstractIndex implements TypeInstanceIndex {

  Map<Topic, Collection<Topic>> topicsByTypes = new IdentityHashMap<>();
  Map<Topic, Collection<Association>> associationsByTypes = new IdentityHashMap<>();
  Map<Topic, Collection<Role>> rolesByTypes = new IdentityHashMap<>();
  Map<Topic, Collection<Occurrence>> occurrencesByTypes = new IdentityHashMap<>();
  Map<Topic, Collection<Name>> namesByTypes = new IdentityHashMap<>();

  public TypeInstanceIndexImpl(Collection<TopicImpl> topics, Collection<AssociationImpl> associations) {
    topics.forEach(topic -> {
      topic.getTypes().forEach(type -> addType(topicsByTypes, type, topic));
      topic.getOccurrences()
          .forEach(occurrence -> addType(occurrencesByTypes, occurrence.getType(), occurrence));
      topic.getNames().forEach(name -> addType(namesByTypes, name.getType(), name));
    });
    associations.forEach(association -> {
      addType(associationsByTypes, association.getType(), association);
      association.getRoles().forEach(role -> addType(rolesByTypes, role.getType(), role));
    });
  }

  @Override
  protected void doClose() {}

  @Override
  public Collection<Topic> getTopics(Topic type) {
    return unmodifiableCollection(topicsByTypes.get(type));
  }

  @Override
  public Collection<Topic> getTopics(Topic[] types, boolean matchAll) {
    return getPropertiedObjects(topicsByTypes, Topic::getTypes, types, matchAll);
  }

  @Override
  public Collection<Topic> getTopicTypes() {
    return unmodifiableCollection(topicsByTypes.keySet());
  }

  @Override
  public Collection<Association> getAssociations(Topic type) {
    return unmodifiableCollection(associationsByTypes.get(type));
  }

  @Override
  public Collection<Topic> getAssociationTypes() {
    return unmodifiableCollection(associationsByTypes.keySet());
  }

  @Override
  public Collection<Role> getRoles(Topic type) {
    return unmodifiableCollection(rolesByTypes.get(type));
  }

  @Override
  public Collection<Topic> getRoleTypes() {
    return unmodifiableCollection(rolesByTypes.keySet());
  }

  @Override
  public Collection<Occurrence> getOccurrences(Topic type) {
    return unmodifiableCollection(occurrencesByTypes.get(type));
  }

  @Override
  public Collection<Topic> getOccurrenceTypes() {
    return unmodifiableCollection(occurrencesByTypes.keySet());
  }

  @Override
  public Collection<Name> getNames(Topic type) {
    return unmodifiableCollection(namesByTypes.get(type));
  }

  @Override
  public Collection<Topic> getNameTypes() {
    return unmodifiableCollection(namesByTypes.keySet());
  }

  @Override
  public void onTypeChanged(TypedConstruct typed, Topic typeToAdd, Topic typeToRemove) {
    final Map<Topic, Collection<TypedConstruct>> types = getOrCreateType(typed.getClass());
    if (typeToRemove != null) {
      Collection<TypedConstruct> typeds = types.get(typeToRemove);
      if (typeds != null) {
        typeds.remove(typed);
      }
    }
    if (typeToAdd != null) {
      addType(types, typeToAdd, typed);
    }
  }

  protected <T> void addType(final Map<Topic, Collection<T>> types, Topic typeToAdd, T typed) {
    Collection<T> typeds = types.get(typeToAdd);
    if (typeds == null) {
      typeds = new ArrayList<T>();
      types.put(typeToAdd, typeds);
    }
    typeds.add(typed);
  }

  private <T> Map<Topic, Collection<T>> getOrCreateType(Class<? extends Object> typedClass) {
    Map types;
    if (Topic.class.isAssignableFrom(typedClass)) {
      types = topicsByTypes;
    } else if (Association.class.isAssignableFrom(typedClass)) {
      types = associationsByTypes;
    } else if (Role.class.isAssignableFrom(typedClass)) {
      types = rolesByTypes;
    } else if (Occurrence.class.isAssignableFrom(typedClass)) {
      types = occurrencesByTypes;
    } else if (Name.class.isAssignableFrom(typedClass)) {
      types = namesByTypes;
    } else {
      throw new IllegalArgumentException("Unsupported type " + typedClass);
    }
    return types;
  }

}
