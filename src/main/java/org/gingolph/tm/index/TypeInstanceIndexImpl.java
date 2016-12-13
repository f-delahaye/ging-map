package org.gingolph.tm.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.gingolph.tm.AssociationImpl;
import org.gingolph.tm.NameImpl;
import org.gingolph.tm.OccurrenceImpl;
import org.gingolph.tm.RoleImpl;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.TypedConstruct;
import org.gingolph.tm.equality.Equality;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Typed;
import org.tmapi.index.TypeInstanceIndex;


public final class TypeInstanceIndexImpl extends ClassifiedItemsIndexImpl<TypedConstruct> implements TypeInstanceIndex {

  ClassifiedAndUnclassifiedItems<TopicImpl> topicsByTypes = new ClassifiedAndUnclassifiedItems<>();
  ClassifiedAndUnclassifiedItems<AssociationImpl> associationsByTypes = new ClassifiedAndUnclassifiedItems<>();
  ClassifiedAndUnclassifiedItems<RoleImpl> rolesByTypes = new ClassifiedAndUnclassifiedItems<>();
  ClassifiedAndUnclassifiedItems<OccurrenceImpl> occurrencesByTypes = new ClassifiedAndUnclassifiedItems<>();
  ClassifiedAndUnclassifiedItems<NameImpl> namesByTypes = new ClassifiedAndUnclassifiedItems<>();

  public TypeInstanceIndexImpl(Equality equality, Collection<TopicImpl> topics, Collection<AssociationImpl> associations) {
    super(equality);
    topics.forEach(topic -> {
      registerItem(topic, topicsByTypes);
      topic.getNullSafeOccurrenceImpls()
          .forEach(occurrence -> registerItem(occurrence, occurrencesByTypes));
      topic.getNullSafeNameImpls().forEach(name -> registerItem(name, namesByTypes));
    });
    associations.forEach(association -> {
      registerItem(association, associationsByTypes);
      association.getNullSafeRoleImpls().forEach(role -> registerItem(role, rolesByTypes));
    });
  }

  @Override
  protected void doClose() {}
  
  @Override
  public Collection<Topic> getTopics(Topic[] types, boolean matchAll) {
    return getItemsByClassifiers(topicsByTypes, types, matchAll, this::getNullSafeClassifierList);
  }

  @Override
  public Collection<Topic> getTopics(Topic type) {
    return getItemsByClassifier(topicsByTypes, type);
  }

  @Override
  public Collection<Topic> getTopicTypes() {
    return getClassifiers(topicsByTypes);
  }

  @Override
  public Collection<Association> getAssociations(Topic type) {
    return getItemsByClassifier(associationsByTypes, type);
  }

  @Override
  public Collection<Topic> getAssociationTypes() {
    return getClassifiers(associationsByTypes);
  }

  @Override
  public Collection<Role> getRoles(Topic type) {
    return getItemsByClassifier(rolesByTypes, type);
  }

  @Override
  public Collection<Topic> getRoleTypes() {
    return getClassifiers(rolesByTypes);
  }

  @Override
  public Collection<Occurrence> getOccurrences(Topic type) {
    return getItemsByClassifier(occurrencesByTypes, type);
  }

  @Override
  public Collection<Topic> getOccurrenceTypes() {
    return getClassifiers(occurrencesByTypes);
  }

  @Override
  public Collection<Name> getNames(Topic type) {
    return getItemsByClassifier(namesByTypes, type);
  }

  @Override
  public Collection<Topic> getNameTypes() {
    return getClassifiers(namesByTypes);
  }

  @Override
  public void onTypeChanged(TypedConstruct typed, Topic typeToAdd, Topic typeToRemove) {
    final ClassifiedAndUnclassifiedItems<TypedConstruct> typedAndUntyped = getItems(typed.getClass());
    if (typeToRemove != null) {
      typedAndUntyped.unclassify(typed, typeToRemove);
      if (getNullSafeClassifierList(typed).isEmpty()) {
        typedAndUntyped.unclassifiedItems.add(typed);
      }
    }
    if (typeToAdd != null) {
      typedAndUntyped.classify(typed,  typeToAdd);
      if (getNullSafeClassifierList(typed).size() == 1) {
        // was previously unscoped, remove it from there
        typedAndUntyped.unclassifiedItems.remove(typed);
      }
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

  @SuppressWarnings("unchecked")
@Override
  protected ClassifiedAndUnclassifiedItems<TypedConstruct> getItems(Class<?> typedClass) {
    ClassifiedAndUnclassifiedItems<?> types;
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
    return ClassifiedAndUnclassifiedItems.class.cast(types);
  }

  @Override
  protected TypedConstruct cast(Construct construct) {
    return construct instanceof TypedConstruct?(TypedConstruct)construct:null;
  }

  @Override
  protected Collection<Topic> getNullSafeClassifierList(TypedConstruct classified) {
    if (classified.getClass() == TopicImpl.class) {
      return ((TopicImpl)classified).getTypes();
    }
    Topic type = ((Typed)classified).getType();
    return type == null ? Collections.emptyList():Arrays.asList(type);
  }

}
