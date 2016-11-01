package org.gingolph.tm.json;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.gingolph.tm.AbstractConstruct;
import org.gingolph.tm.AssociationImpl;
import org.gingolph.tm.AssociationSupport;
import org.gingolph.tm.LocatorImpl;
import org.gingolph.tm.NameImpl;
import org.gingolph.tm.NameSupport;
import org.gingolph.tm.OccurrenceImpl;
import org.gingolph.tm.OccurrenceSupport;
import org.gingolph.tm.RoleImpl;
import org.gingolph.tm.RoleSupport;
import org.gingolph.tm.TopicImpl;
import org.gingolph.tm.TopicMapImpl;
import org.gingolph.tm.TopicMapSupport;
import org.gingolph.tm.TopicSupport;
import org.gingolph.tm.VariantImpl;
import org.gingolph.tm.VariantSupport;
import org.gingolph.tm.index.IdentifierIndex;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;
import org.tmapi.index.Index;

import mjson.Json;
import mjson.TopicMapJson;

public class TopicMapSupportJson extends TopicMapJson implements TopicMapSupport, TopicSupport, AssociationSupport, RoleSupport, NameSupport, OccurrenceSupport, VariantSupport {

  private static final String SCOPE_PP = "scope";
  private static final String TYPES_PP = "types";
  private static final String TYPE_PP = "type";
  private static final String ITEM_IDENTIFIERS_PP = "item.identifiers";
  private static final String SUBJECT_IDENTIFIERS_PP = "subject.identifiers";
  private static final String SUBJECT_LOCATORS_PP = "subject.locators";
  private static final String TOPICS_PP = "topics";
  private static final String ASSOCIATIONS_PP = "associations";  
  private static final String ROLES_PP = "roles";
  private static final String NAMES_PP = "names";
  private static final String OCCURRENCES_PP = "occurrences";
  private static final String REIFIER_PP = "reifier";
  private static final String REIFIED_PP = "reified";
  private static final String VARIANTS_PP = "variants";
  private static final String DATATYPE_PP = "datatype";
  private static final String VALUE_PP = "value";
  private static final String PLAYER_PP = "player";
  private static final String ROLES_PLAYED_PP = "roles.played";
  
  static AtomicLong counter = new AtomicLong();
  
  static {
    Json.setGlobalFactory(new Json.DefaultFactory() {
      @Override
      public Json object() {
        return new TopicMapSupportJson();
      }
    });    
  }
  
  private AbstractConstruct<?> owner;
  
  public TopicMapSupportJson() {
  }

  public TopicMapSupportJson(Json json) {
    super(json);
  }  
  
  private Json getJson() {
    return this;
  }

  private TopicMapImpl getTopicMap() {
    return owner.getTopicMap();
  }
  
  private <T extends AbstractConstruct<?>> T getOwner() {
    return (T)owner;
  }
  
  private TopicMapSupportJson getTopicMapSupportJson(Construct construct) {
    return ((AbstractConstruct<TopicMapSupportJson>)construct).getSupport();
  }
  
  private Json getOrCreateArray(String pp) {
    Json array = getJson().at(pp);
    if (array == null) {
      array = Json.array();
      getJson().set(pp, array);    
    }
    return array;
    
  }
  
  private Json nullSafeScope() {
    return getOrCreateArray(SCOPE_PP);
  }
  
  @Override
  public final Set<Topic> getScope() {
    Set<Topic> scope = new HashSet<>();
    for (Json topicJson: nullSafeScope().asJsonList()) {
      String topicId = topicJson.at("id").asString();
      Topic theme = (Topic) getTopicMap().getConstructById(topicId);
      scope.add(theme);
    }    
    return scope;
  }

  @Override
  public final void addTheme(Topic theme) {
    nullSafeScope().add(theme.getId());
  }

  @Override
  public final void removeTheme(Topic theme) {
    nullSafeScope().remove(theme.getId());  
  }
  
  private Json nullSafeItemIdentifiers() {
    return getOrCreateArray(ITEM_IDENTIFIERS_PP);
  }
  
  @Override
  public final Set<Locator> getItemIdentifiers() {
    Set<Locator> locators = new HashSet<>();
    for (Json identifierJson: nullSafeItemIdentifiers().asJsonList()) {
      locators.add(getTopicMap().createLocator(identifierJson.asString()));
    }
    return locators;

  }

  @Override
  public final void addItemIdentifier(Locator identifier) {
    nullSafeItemIdentifiers().add(Json.make(identifier.getReference()));
  }

  @Override
  public final void removeItemIdentifier(Locator identifier) {
    nullSafeItemIdentifiers().remove(identifier.getReference());
  }

  private Json nullSafeSubjectIdentifiers() {
    return getOrCreateArray(SUBJECT_IDENTIFIERS_PP);
  }
  
  @Override
  public final Set<Locator> getSubjectIdentifiers() {
    Set<Locator> locators = new HashSet<>();
    for (Json identifierJson: nullSafeSubjectIdentifiers().asJsonList()) {
      locators.add(getTopicMap().createLocator(identifierJson.asString()));
    }
    return locators;

  }

  @Override
  public final void addSubjectIdentifier(Locator identifier) {
    nullSafeSubjectIdentifiers().add(Json.make(identifier.getReference()));
  }

  @Override
  public final void removeSubjectIdentifier(Locator identifier) {
    nullSafeSubjectIdentifiers().remove(identifier.getReference());
  }

  private Json nullSafeSubjectLocators() {
    return getOrCreateArray(SUBJECT_LOCATORS_PP);
  }
  
  @Override
  public final Set<Locator> getSubjectLocators() {
    Set<Locator> locators = new HashSet<>();
    for (Json identifierJson: nullSafeSubjectLocators().asJsonList()) {
      locators.add(getTopicMap().createLocator(identifierJson.asString()));
    }
    return locators;

  }

  @Override
  public final void addSubjectLocator(Locator locator) {
    nullSafeSubjectLocators().add(Json.make(locator.getReference()));
  }

  @Override
  public final void removeSubjectLocator(Locator locator) {
    nullSafeSubjectLocators().remove(locator.getReference());
  }

  private Json nullSafeRoles() {
    return getOrCreateArray(ROLES_PP);
  }

  @Override
  public Set<Role> getRoles() {
    Set<Role> roles = new HashSet<>();
    for (Json roleJson: nullSafeRoles().asJsonList()) {
      TopicMapSupportJson roleSupport = (TopicMapSupportJson) roleJson;
      RoleImpl role = new RoleImpl(getTopicMap(), this.<AssociationImpl>getOwner());
      role.setSupport(roleSupport);
      roleSupport.owner = role;
      roles.add(role);
    }
    return roles;
  }

  @Override
  public void addRole(Role role) {
    nullSafeRoles().add(getTopicMapSupportJson(role));
  }

  @Override
  public void removeRole(Role role) {
    nullSafeRoles().remove(getTopicMapSupportJson(role));
  }
  
  private Json nullSafeNames() {
    return getOrCreateArray(NAMES_PP);
  }
  
  @Override
  public Set<NameImpl> getNames() {
    Set<NameImpl> names = new HashSet<>();
    for (Json nameJson: nullSafeNames().asJsonList()) {
      TopicMapSupportJson nameSupport = (TopicMapSupportJson) nameJson;
      NameImpl name = new NameImpl(getTopicMap(), this.<TopicImpl>getOwner());
      name.setSupport(nameSupport);
      nameSupport.owner = name;
      names.add(name);
    }
    return names;
  }

  @Override
  public void addName(NameImpl name) {
    nullSafeNames().add(getTopicMapSupportJson(name));
  }

  @Override
  public void removeName(NameImpl name) {
    nullSafeNames().remove(getTopicMapSupportJson(name));
  }

  private Json nullSafeOccurrences() {
    return getOrCreateArray(OCCURRENCES_PP);
  }
  
  @Override
  public Set<Occurrence> getOccurrences() {
    Set<Occurrence> occurrences = new HashSet<>();
    for (Json occurrenceJson: nullSafeOccurrences().asJsonList()) {
      TopicMapSupportJson occurrenceSupport = (TopicMapSupportJson) occurrenceJson;
      OccurrenceImpl occurrence = new OccurrenceImpl(getTopicMap(), this.<TopicImpl>getOwner());
      occurrence.setSupport(occurrenceSupport);
      occurrenceSupport.owner = occurrence;
      occurrences.add(occurrence);
    }
    return occurrences;
  }

  @Override
  public void addOccurrence(Occurrence occurrence) {
    nullSafeOccurrences().add(getTopicMapSupportJson(occurrence));
  }

  @Override
  public void removeOccurrence(Occurrence occurrence) {
    nullSafeOccurrences().remove(getTopicMapSupportJson(occurrence));
  }
  
//  @Override
//  public Set<Role> getRolesPlayed() {
//    return null;
//  }
//
//  @Override
//  public void addRolePlayed(Role role) {
//  }
//
//  @Override
//  public void removeRolePlayed(Role role) {
//  }

  private Json nullSafeTypes() {
    return getOrCreateArray(TYPES_PP);
  }
  
  @Override
  public final Set<Topic> getTypes() {
    Set<Topic> types = new HashSet<>();
    for (Json topicJson: nullSafeTypes().asJsonList()) {
      String topicId = topicJson.at("id").asString();
      Topic type = (Topic) getTopicMap().getConstructById(topicId);
      types.add(type);
    }    
    return types;
  }

  @Override
  public final void addType(Topic type) {
    nullSafeTypes().add(type.getId());
  }

  @Override
  public final boolean removeType(Topic type) {
    nullSafeTypes().remove(type.getId());  
    return true;
  }  
  
  @Override
  public Topic getType() {
    String topicId = at(TYPE_PP).asString();
    return (Topic) getTopicMap().getConstructById(topicId);
  }
  
  @Override
  public void setType(Topic type) {
    set(TYPE_PP, type.getId());
  }
  
  private Json nullSafeTopics() {
    return getOrCreateArray(TOPICS_PP);
  }

  @Override
  public Set<Topic> getTopics() {
    Set<Topic> topics = new HashSet<>();
    for (Json topicJson: nullSafeTopics().asJsonList()) {
      TopicMapSupportJson topicSupport = (TopicMapSupportJson) topicJson;
      TopicImpl topic = new TopicImpl(getTopicMap());
      topic.setSupport(topicSupport);
      topicSupport.owner = topic;
      topics.add(topic);
    }
    return topics;
  }

  @Override
  public void addTopic(Topic topic) {
    nullSafeTopics().add(getTopicMapSupportJson(topic));
  }

  @Override
  public void removeTopic(Topic topic) {
    nullSafeTopics().remove(getTopicMapSupportJson(topic));
  }

  private Json nullSafeAssociations() {
    return getOrCreateArray(ASSOCIATIONS_PP);
  }

  @Override
  public Set<Association> getAssociations() {
    Set<Association> associations = new HashSet<>();
    for (Json topicJson: nullSafeAssociations().asJsonList()) {
      TopicMapSupportJson associationSupport = (TopicMapSupportJson) topicJson;
      AssociationImpl association = new AssociationImpl(getTopicMap());
      association.setSupport(associationSupport);
      associationSupport.owner = association;
      associations.add(association);
    }
    return associations;
  }

  @Override
  public void addAssociation(Association association) {
    nullSafeAssociations().add(getTopicMapSupportJson(association));
  }

  @Override
  public void removeAssociation(Association association) {
    nullSafeAssociations().remove(getTopicMapSupportJson(association));
  }

  private Json nullSafeVariants() {
    return getOrCreateArray(VARIANTS_PP);
  }

  @Override
  public Set<Variant> getVariants() {
    Set<Variant> variants = new HashSet<>();
    for (Json variantJson: nullSafeVariants().asJsonList()) {
      TopicMapSupportJson variantSupport = (TopicMapSupportJson) variantJson;
      VariantImpl variant = new VariantImpl(getTopicMap(), this.<NameImpl>getOwner());
      variant.setSupport(variantSupport);
      variantSupport.owner = variant;
      variants.add(variant);
    }
    return variants;
  }

  @Override
  public void addVariant(Variant variant) {
    nullSafeVariants().add(getTopicMapSupportJson(variant));
  }

  @Override
  public void removeVariant(Variant variant) {
    nullSafeVariants().remove(getTopicMapSupportJson(variant));
  }

  
  @Override
  public <I extends Index> I getIndex(Class<I> type) {
    if (type == IdentifierIndex.class) {
      return (I) new IdentifierIndex(getTopicMap(), getTopics(), getAssociations());
    }
    return null;
  }

  @Override
  public TopicImpl getReifier() {
    String topicId = at(REIFIER_PP).asString();
    return (TopicImpl) getTopicMap().getConstructById(topicId);
  }
  
  @Override
  public void setReifier(TopicImpl reifier) {
    set(REIFIER_PP, reifier.getId());
  }
  
  @Override
  public Reifiable getReified() {
    String constructId = at(REIFIED_PP).asString();
    return (Reifiable) getTopicMap().getConstructById(constructId);
  }

  @Override
  public void setReified(Reifiable reified) {
    set(REIFIED_PP, reified.getId());    
  }
  
  @Override
  public String generateId(AbstractConstruct construct) {
    return String.valueOf(counter.getAndIncrement());
  }

  @Override
  public Locator createLocator(String value) {
    return new LocatorImpl(value);
  }

  @Override
  public Locator getDatatype() {
    return createLocator(at(DATATYPE_PP).asString());
  }

  @Override
  public void setDatatype(Locator locator) {
    set(DATATYPE_PP, locator.getReference());
  }

  @Override
  public String getValue() {
    return at(VALUE_PP).asString();
  }

  @Override
  public void setValue(String value) {
    set(VALUE_PP, value);
  }

  @Override
  public Topic getPlayer() {
    String topicId = at(PLAYER_PP).asString();
    return (Topic) getTopicMap().getConstructById(topicId);
  }
  
  @Override
  public void setPlayer(TopicImpl player) {
    set(PLAYER_PP, player.getId());
  }
  
  private Json nullSafeRolesPlayed() {
    return getOrCreateArray(ROLES_PLAYED_PP);
  }
  
  @Override
  public final Set<Role> getRolesPlayed() {
    Set<Role> rolesPlayed = new HashSet<>();
    for (Json roleJson: nullSafeRolesPlayed().asJsonList()) {
      String roleId = roleJson.at("id").asString();
      Role role = (Role) getTopicMap().getConstructById(roleId);
      rolesPlayed.add(role);
    }    
    return rolesPlayed;
  }

  @Override
  public final void addRolePlayed(Role role) {
    nullSafeRolesPlayed().add(role.getId());
  }

  @Override
  public final void removeRolePlayed(Role role) {
    nullSafeRolesPlayed().remove(role.getId());  
  }

  @Override
  public void setOwner(VariantImpl owner) {
    this.owner = owner;
  }

  @Override
  public void setOwner(OccurrenceImpl owner) {
    this.owner = owner;
  }

  @Override
  public void setOwner(NameImpl owner) {
    this.owner = owner;    
  }

  @Override
  public void setOwner(RoleImpl owner) {
    this.owner = owner;
  }

  @Override
  public void setOwner(AssociationImpl owner) {
    this.owner = owner;    
  }

  @Override
  public void setOwner(TopicImpl owner) {
    this.owner = owner;    
  }

  @Override
  public void setOwner(TopicMapImpl owner) {
    this.owner = owner;
  }
  
}
