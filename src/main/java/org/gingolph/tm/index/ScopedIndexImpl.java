package org.gingolph.tm.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.gingolph.tm.event.TopicMapEventListener;
import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;
import org.tmapi.index.ScopedIndex;


public final class ScopedIndexImpl extends AbstractIndex
    implements ScopedIndex, TopicMapEventListener {

  Map<Class<? extends Scoped>, Scope> scopes = new HashMap<>();

  public ScopedIndexImpl(Collection<Topic> topics, Collection<Association> associations) {
    associations.forEach(association -> addScoped(association));
    topics.forEach(topic -> {
      topic.getOccurrences().forEach(occurrence -> addScoped(occurrence));
      topic.getNames().forEach(name -> {
        addScoped(name);
        name.getVariants().forEach(variant -> addScoped(variant));
      });
    });
  }

  @Override
  protected void doClose() {}

  /**
   * @param <T>
   * @param scoped
   * @param declaredScopedClass the tm api (and not its concrete gingolp implementation). It has to
   *        match the class used when looking up a scope, which is the tm api interface.
   */
  protected <T extends Scoped> void addScoped(T scoped) {
    Scope scope = getOrCreateScope(scoped.getClass());
    if (scoped.getScope().isEmpty()) {
      scope.addUnscoped(scoped);
    } else {
      scoped.getScope().forEach(theme -> scope.addTheme(scoped, theme));
    }
  }

  protected <T extends Scoped> Scope<T> getOrCreateScope(Class<T> scopedClass) {
    if (!(scopedClass.getPackage().getName().equals("org.tmapi.core"))) {
      String scopedClassName = scopedClass.getSimpleName();
      try {
        scopedClass = (Class<T>) Class.forName("org.tmapi.core."
            + scopedClassName.substring(0, scopedClassName.length() - "Impl".length()));
      } catch (Exception exc) {
        throw new IllegalArgumentException("Unsupported class:" + scopedClass);
      }
    }
    Scope<T> scope = scopes.get(scopedClass);
    if (scope == null) {
      scope = new Scope();
      scopes.put(scopedClass, scope);
    }
    return scope;
  }

  @Override
  public Collection<Association> getAssociations(Topic theme) {
    return getOrCreateScope(Association.class).getScopeds(theme);
  }

  @Override
  public Collection<Association> getAssociations(Topic[] themes, boolean matchAll) {
    return getOrCreateScope(Association.class).getScopeds(themes, matchAll);
  }

  @Override
  public Collection<Topic> getAssociationThemes() {
    return getOrCreateScope(Association.class).getThemes();
  }

  @Override
  public Collection<Occurrence> getOccurrences(Topic theme) {
    return getOrCreateScope(Occurrence.class).getScopeds(theme);
  }

  @Override
  public Collection<Occurrence> getOccurrences(Topic[] themes, boolean matchAll) {
    return getOrCreateScope(Occurrence.class).getScopeds(themes, matchAll);
  }

  @Override
  public Collection<Topic> getOccurrenceThemes() {
    return getOrCreateScope(Occurrence.class).getThemes();
  }

  @Override
  public Collection<Name> getNames(Topic theme) {
    return getOrCreateScope(Name.class).getScopeds(theme);
  }

  @Override
  public Collection<Name> getNames(Topic[] themes, boolean matchAll) {
    return getOrCreateScope(Name.class).getScopeds(themes, matchAll);
  }

  @Override
  public Collection<Topic> getNameThemes() {
    return getOrCreateScope(Name.class).getThemes();
  }

  @Override
  public Collection<Variant> getVariants(Topic theme) {
    return getOrCreateScope(Variant.class).getScopeds(theme);
  }

  @Override
  public Collection<Variant> getVariants(Topic[] themes, boolean matchAll) {
    return getOrCreateScope(Variant.class).getScopeds(themes, matchAll);
  }

  @Override
  public Collection<Topic> getVariantThemes() {
    return getOrCreateScope(Variant.class).getThemes();
  }

  protected <T extends Scoped> Class<T> getClass(Scoped scoped) {
    return null;
  }

  @Override
  public void onThemeChanged(Scoped scoped, Topic themeToAdd, Topic themeToRemove) {
    final Scope scope = getOrCreateScope(scoped.getClass());
    if (themeToRemove != null) {
      scope.removeTheme(scoped, themeToRemove);
      if (scoped.getScope().isEmpty()) {
        scope.addUnscoped(scoped);
      }
    }
    if (themeToAdd != null) {
      scope.addTheme(scoped, themeToAdd);
    }
  }

  private static class Scope<T extends Scoped> {
    List<T> unscopeds;
    Map<Topic, Collection<T>> themesToScopeds = new LinkedHashMap<>();

    public void addUnscoped(T unscoped) {
      if (unscopeds == null) {
        unscopeds = new ArrayList<>();
      }
      unscopeds.add(unscoped);
    }

    public void addTheme(T scoped, Topic theme) {
      Collection<T> scopeds = themesToScopeds.get(theme);
      if (scopeds == null) {
        scopeds = new ArrayList<>();
        themesToScopeds.put(theme, scopeds);
      }
      scopeds.add(scoped);
    }

    protected void removeTheme(Scoped scoped, Topic theme) {
      Collection<T> scopeds = themesToScopeds.get(theme);
      if (scopeds != null) {
        scopeds.remove(scoped);
      }
    }

    public Collection<T> getScopeds(Topic theme) {
      Collection<T> scopeds = theme == null ? unscopeds : themesToScopeds.get(theme);
      return scopeds == null ? Collections.emptyList()
          : Collections.unmodifiableCollection(scopeds);
    }

    protected Collection<T> getScopeds(Topic[] themes, boolean matchAll)
        throws IllegalArgumentException {
      return AbstractIndex.getPropertiedObjects(themesToScopeds, Scoped::getScope, themes,
          matchAll);
    }

    protected Collection<Topic> getThemes() {
      return themesToScopeds.keySet();
    }
  }
}
