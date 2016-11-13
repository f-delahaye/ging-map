package org.gingolph.tm.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import org.gingolph.tm.equality.Equality;
import org.gingolph.tm.event.TopicMapEventListener;
import org.tmapi.core.Construct;
import org.tmapi.core.Topic;

public abstract class ClassifiedItemsIndexImpl<T> extends AbstractIndex
    implements TopicMapEventListener {
  
  protected ClassifiedItemsIndexImpl(Equality equality) {
    super(equality);
  }

  // Inner class that mentions scoped and unscoped items for a given subclass of Scoped
  static class ClassifiedAndUnclassifiedItems<T> {
    // Using identityHashMap doesn't honour the selected EQuality but i see no other solutions ... its possible to implement an ArraySet but an ArrayMap seems impossible.
    Map<Topic, Collection<T>> scopedItems = new IdentityHashMap<>();
    Collection<T> unclassifiedItems = new ArrayList<>();
    
    void unclassify(T scoped, Topic theme) {
      scopedItems.computeIfPresent(theme, (k,v) -> removeAndNullifyIfEmpty(v, scoped));
    }
    
    void classify(T scoped, Topic theme) {
      scopedItems.computeIfAbsent(theme, key -> new ArrayList<>()).add(scoped);
    }
  }
  
  protected abstract T cast(Construct construct);
  
  protected abstract Collection<Topic> getNullSafeClassifierList(T classified);
  
  @Override
  public void onConstructCreated(Construct construct) {
    T classified = cast(construct);
    if (classified != null) {
      Collection<Topic> classifiers = getNullSafeClassifierList(classified);
      ClassifiedAndUnclassifiedItems<T> classifiedAndUnclassifiedItems = getItems(classified.getClass());
      if (classifiers.isEmpty()) {
        classifiedAndUnclassifiedItems.unclassifiedItems.add(classified);
      } else {
        onClassifiedWithClassifiersCreated(classified, classifiers);
      }
    }
  }

  protected void onClassifiedWithClassifiersCreated(T classified, Collection<Topic> classifiers) {
    // In many cases, If classified does have classifiers, they will be registered when construct.addClassifier
    // is invoked so we only handle unclassified items.
    // However every rule has its exceptions, and variants are one.
  }

  @Override
  public void onConstructRemoved(Construct construct) {
    T classified = cast(construct);
    if (classified != null) {
      ClassifiedAndUnclassifiedItems<T> itemsByThemes = getItems(classified.getClass());
      Collection<Topic> classifiers = getNullSafeClassifierList(classified);
      if (classifiers.isEmpty()) {
        itemsByThemes.unclassifiedItems.remove(classified);
      } else {
        classifiers.forEach(classifier -> itemsByThemes.unclassify(classified, classifier));
      }
    }
  }



  @Override
  protected void doClose() {}

  /**
   * @param <T>
   * @param item
   * @param declaredScopedClass the tm api (and not its concrete gingolp implementation). It has to
   *        match the class used when looking up a scope, which is the tm api interface.
   */
  protected <I extends T> void registerItem(I item, ClassifiedAndUnclassifiedItems<I> classifiedAndUnclassifiedItems) {
    Collection<Topic> classifiers = getNullSafeClassifierList(item);
    if (classifiers.isEmpty()) {
      classifiedAndUnclassifiedItems.unclassifiedItems.add(item);
    } else {
      classifiers.forEach(classifier -> classifiedAndUnclassifiedItems.classify(item, classifier));
    }
  }

  @SuppressWarnings("rawtypes")
  protected abstract ClassifiedAndUnclassifiedItems<T> getItems(Class classifiedClass);

  protected <TM, G extends TM> Collection<TM> getItemsByClassifier(ClassifiedAndUnclassifiedItems<G> items, Topic theme) {
    Collection<G> itemsByTheme = theme == null ? items.unclassifiedItems : items.scopedItems.get(theme);
    return itemsByTheme == null ? Collections.emptyList():Collections.unmodifiableCollection(itemsByTheme);
  }
  
  protected <TM, G extends TM> Collection<TM> getItemsByClassifiers(ClassifiedAndUnclassifiedItems<G> scopedAndUnscopedItems, Topic[] themes, boolean matchAll, Function<G, Collection<Topic>> propertiesSource)
      throws IllegalArgumentException {
    if (themes == null) {
      throw new IllegalArgumentException("Null themes not supported");
    }
    return AbstractIndex.getPropertiedObjects(scopedAndUnscopedItems.scopedItems, propertiesSource, themes, matchAll);
  }  
  
  protected  Collection<Topic> getClassifiers(ClassifiedAndUnclassifiedItems<? extends T> itemsByThemes) {
    return Collections.unmodifiableCollection(itemsByThemes.scopedItems.keySet());
  }
}
