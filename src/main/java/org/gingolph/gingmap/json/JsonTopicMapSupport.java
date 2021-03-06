package org.gingolph.gingmap.json;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gingolph.gingmap.AbstractConstruct;
import org.gingolph.gingmap.AssociationImpl;
import org.gingolph.gingmap.AssociationSupport;
import org.gingolph.gingmap.LocatorImpl;
import org.gingolph.gingmap.NameImpl;
import org.gingolph.gingmap.NameSupport;
import org.gingolph.gingmap.OccurrenceImpl;
import org.gingolph.gingmap.OccurrenceSupport;
import org.gingolph.gingmap.RoleImpl;
import org.gingolph.gingmap.RoleSupport;
import org.gingolph.gingmap.TopicImpl;
import org.gingolph.gingmap.TopicMapImpl;
import org.gingolph.gingmap.TopicMapSupport;
import org.gingolph.gingmap.TopicMapSystemImpl;
import org.gingolph.gingmap.TopicSupport;
import org.gingolph.gingmap.VariantImpl;
import org.gingolph.gingmap.VariantSupport;
import org.gingolph.gingmap.equality.Equality;
import org.gingolph.gingmap.index.IdentifierIndex;
import org.gingolph.gingmap.index.LiteralIndexImpl;
import org.gingolph.gingmap.index.ScopedIndexImpl;
import org.gingolph.gingmap.index.TypeInstanceIndexImpl;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

import mjson.Json;
import mjson.TopicMapJson;

public class JsonTopicMapSupport extends TopicMapJson implements TopicMapSupport, TopicSupport, AssociationSupport, RoleSupport, NameSupport, OccurrenceSupport, VariantSupport {

  private static final long serialVersionUID = 1L;
  
  private static final String BASE_LOCATOR_PP = "locator";
  private static final String ID_PP = "id";
  private static final String SCOPE_PP = "scope";
  private static final String TYPES_PP = "types";
  private static final String TYPE_ID_PP = "typeId";
  private static final String ITEM_IDENTIFIERS_PP = "itemIdentifiers";
  private static final String SUBJECT_IDENTIFIERS_PP = "subjectIdentifiers";
  private static final String SUBJECT_LOCATORS_PP = "subjectLocators";
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
  private static final String PLAYER_ID_PP = "playerId";
  private static final String ROLES_PLAYED_PP = "rolesPlayed";
  
  static AtomicLong counter = new AtomicLong();
  
  static {
    Json.setGlobalFactory(new Json.DefaultFactory() {
      @Override
      public Json object() {
        return new JsonTopicMapSupport();
      }
    });    
  }
  
  private AbstractConstruct<?> owner;
  
  public JsonTopicMapSupport() {
  }

  public JsonTopicMapSupport(Json json) {
    super(json);
  }  
  
  public static TopicMapImpl read(String jsonAsString, TopicMapSystemImpl topicMapSystem) {
      JsonTopicMapSupport topicMapSupport = (JsonTopicMapSupport) Json.read(jsonAsString);
      TopicMapImpl topicMap = topicMapSystem.createTopicMap(topicMapSupport);      
      // if this json was created from a jsonstring, the owners have not been set, so we need to browse the json object and populate those now.
      // we start off with the root object which we assume is the topicMap itself.
      topicMap.setSupport(topicMapSupport);
      // calling TopicImpl.setSupport will in turn call Support.notifyOwner(topic) which is what we expect.
      topicMapSupport.nullSafeTopicSupports().forEach(topicSupport -> populateTopic(topicSupport, topicMap));
      topicMapSupport.nullSafeAssociationSupports().forEach(associationSupport -> populateAssociation(associationSupport, topicMap));
      return topicMap;
  }

  private static void populateAssociation(JsonTopicMapSupport associationSupport, TopicMapImpl topicMap) {
    AssociationImpl associationImpl = new AssociationImpl(topicMap);
    associationImpl.setSupport(associationSupport);
    associationSupport.nullSafeRoleSupports().forEach(roleSupport -> new RoleImpl(topicMap, associationImpl).setSupport(roleSupport));
  }

  private static void populateTopic(JsonTopicMapSupport topicSupport, TopicMapImpl topicMap) {
    new TopicImpl(topicMap).setSupport(topicSupport);
  }
  
  private Json getJson() {
    return this;
  }

  private TopicMapImpl getTopicMap() {
    return owner.getTopicMap();
  }
  
  @SuppressWarnings("unchecked")
  private <T extends AbstractConstruct<?>> T getOwner() {
    return (T)owner;
  }
  
  @SuppressWarnings("unchecked")
  private JsonTopicMapSupport getTopicMapSupportJson(Construct construct) {
    return ((AbstractConstruct<JsonTopicMapSupport>)construct).getSupport();
  }
  
  private Json getOrCreateArray(String pp) {
    Json array = getJson().at(pp);
    if (array == null) {
      array = Json.array();
      getJson().set(pp, array);    
    }
    return array;
    
  }
  
  public String getId() {
    Json id = at(ID_PP);
    return id == null? null : id.asString();
  }
  
  public void setId(String id) {
    set(ID_PP, id);
  }
  
  private Json nullSafeScope() {
    return getOrCreateArray(SCOPE_PP);
  }
  
  @Override
  public final Set<TopicImpl> getScope() {
    Set<TopicImpl> scope = owner.getTopicMap().getEquality().newSet();
    for (Json themeJson: nullSafeScope().asJsonList()) {
      TopicImpl theme = (TopicImpl) getTopicMap().getConstructById(themeJson.asString());
      scope.add(theme);
    }    
    return scope;
  }

  @Override
  public final void addTheme(TopicImpl theme, Equality equality) {
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

  private Stream<JsonTopicMapSupport> nullSafeRoleSupports() {
    return nullSafeRoles().asJsonList().stream().map(JsonTopicMapSupport.class::cast);
  }
    
  @Override
  public List<RoleImpl> getRoles() {
    return nullSafeRoleSupports().map(JsonTopicMapSupport::getOwner).map(RoleImpl.class::cast).collect(Collectors.toList());    
  }

  @Override
  public void addRole(RoleImpl role) {
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
  public List<NameImpl> getNames() {
    List<NameImpl> names = new ArrayList<>();
    for (Json nameJson: nullSafeNames().asJsonList()) {
      JsonTopicMapSupport nameSupport = (JsonTopicMapSupport) nameJson;
      names.add(nameSupport.getOwner());
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
  public List<OccurrenceImpl> getOccurrences() {
    List<OccurrenceImpl> occurrences = new ArrayList<>();
    for (Json occurrenceJson: nullSafeOccurrences().asJsonList()) {
      JsonTopicMapSupport occurrenceSupport = (JsonTopicMapSupport) occurrenceJson;
      occurrences.add(occurrenceSupport.getOwner());
    }
    return occurrences;
  }

  @Override
  public void addOccurrence(OccurrenceImpl occurrence) {
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
  public final Set<TopicImpl> getTypes() {
    Set<TopicImpl> types = owner.getTopicMap().getEquality().newSet();
    for (Json typeJson: nullSafeTypes().asJsonList()) {
      TopicImpl type = (TopicImpl) getTopicMap().getConstructById(typeJson.asString());
      types.add(type);
    }    
    return types;
  }

  @Override
  public final void addType(TopicImpl type, Equality equality) {
    nullSafeTypes().add(type.getId());
  }

  @Override
  public final boolean removeType(Topic type) {
    nullSafeTypes().remove(type.getId());  
    return true;
  }  
  
  @Override
  public TopicImpl getType() {
    Json typeId = at(TYPE_ID_PP);
    return typeId == null?null:(TopicImpl) getTopicMap().getConstructById(typeId.asString());
  }
  
  @Override
  public void setType(TopicImpl type) {
    set(TYPE_ID_PP, type.getId());
  }
  
  private Json nullSafeTopics() {
    return getOrCreateArray(TOPICS_PP);
  }

  private Stream<JsonTopicMapSupport> nullSafeTopicSupports() {
    return nullSafeTopics().asJsonList().stream().map(JsonTopicMapSupport.class::cast);
  }
  
  @Override
  public List<TopicImpl> getTopics() {
    return nullSafeTopicSupports().map(JsonTopicMapSupport::getOwner).map(TopicImpl.class::cast).collect(Collectors.toList());
  }

  @Override
  public void addTopic(TopicImpl topic) {
    nullSafeTopics().add(getTopicMapSupportJson(topic));
  }

  @Override
  public void removeTopic(Topic topic) {
    nullSafeTopics().remove(getTopicMapSupportJson(topic));
  }

  private Json nullSafeAssociations() {
    return getOrCreateArray(ASSOCIATIONS_PP);
  }

  private Stream<JsonTopicMapSupport> nullSafeAssociationSupports() {
    return nullSafeAssociations().asJsonList().stream().map(JsonTopicMapSupport.class::cast);
  }
    
  @Override
  public List<AssociationImpl> getAssociations() {
    return nullSafeAssociationSupports().map(JsonTopicMapSupport::getOwner).map(AssociationImpl.class::cast).collect(Collectors.toList());    
  }

  @Override
  public void addAssociation(AssociationImpl association) {
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
  public List<VariantImpl> getVariants() {
    List<VariantImpl> variants = new ArrayList<>();
    for (Json variantJson: nullSafeVariants().asJsonList()) {
      JsonTopicMapSupport variantSupport = (JsonTopicMapSupport) variantJson;
      variants.add(variantSupport.getOwner());
    }
    return variants;
  }

  @Override
  public void addVariant(VariantImpl variant) {
    nullSafeVariants().add(getTopicMapSupportJson(variant));
  }

  @Override
  public void removeVariant(Variant variant) {
    nullSafeVariants().remove(getTopicMapSupportJson(variant));
  }

  @Override
  public <I extends Index> I getIndex(Class<I> type) {
    TopicMapImpl topicMap = getOwner();
    Index index;
      if (LiteralIndex.class.isAssignableFrom(type)) {
        index = topicMap.registerListener(new LiteralIndexImpl(topicMap.getEquality()));
      } else if (IdentifierIndex.class.isAssignableFrom(type)) {
        index = topicMap
            .registerListener(new IdentifierIndex(topicMap, getTopics(), getAssociations()));
      } else if (ScopedIndex.class.isAssignableFrom(type)) {
        index = topicMap.registerListener(new ScopedIndexImpl(topicMap.getEquality(), getTopics(), getAssociations()));
      } else if (TypeInstanceIndex.class.isAssignableFrom(type)) {
        index =
            topicMap.registerListener(new TypeInstanceIndexImpl(topicMap.getEquality(), getTopics(), getAssociations()));
      } else {
        throw new UnsupportedOperationException("Unknown index " + type);
      }
    return type.cast(index);
  }


  @Override
  public TopicImpl getReifier() {
    Json reifierId = at(REIFIER_PP);
    return reifierId == null ? null : (TopicImpl) getTopicMap().getConstructById(reifierId.asString());
  }
  
  @Override
  public void setReifier(TopicImpl reifier) {
    if (reifier == null) {
      if (has(REIFIER_PP)) {
        delAt(REIFIER_PP);
      }
    } else {
      set(REIFIER_PP, reifier.getId());
    }
  }
  
  @Override
  public Reifiable getReified() {
    Json reifiedId = at(REIFIED_PP);
    return reifiedId == null ? null : (Reifiable) getTopicMap().getConstructById(reifiedId.asString());
  }

  @Override
  public void setReified(Reifiable reified) {
    if (reified == null) {
      if (has(REIFIED_PP)) {
        delAt(REIFIED_PP);
      }
    } else {
      set(REIFIED_PP, reified.getId());
    }
  }
  
  @Override
  public String generateId(AbstractConstruct<?> construct) {
    return String.valueOf(counter.getAndIncrement());
  }

  private LocatorImpl createLocator(String value) {
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
    Json value = at(VALUE_PP);
    return value == null ? null : value.asString();
  }

  @Override
  public void setValue(String value) {
    set(VALUE_PP, value);
  }

  @Override
  public TopicImpl getPlayer() {
    Json playerId = at(PLAYER_ID_PP);
    return playerId == null ? null : (TopicImpl) getTopicMap().getConstructById(playerId.asString());
  }
  
  @Override
  public void setPlayer(TopicImpl player) {
    set(PLAYER_ID_PP, player.getId());
  }
  
  private Json nullSafeRolesPlayed() {
    return getOrCreateArray(ROLES_PLAYED_PP);
  }
  
  @Override
  public final List<RoleImpl> getRolesPlayed() {
    List<RoleImpl> rolesPlayed = new ArrayList<>();
    for (Json roleJson: nullSafeRolesPlayed().asJsonList()) {
      RoleImpl role = (RoleImpl) getTopicMap().getConstructById(roleJson.asString());
      rolesPlayed.add(role);
    }    
    return rolesPlayed;
  }

  @Override
  public final void addRolePlayed(RoleImpl role) {
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

  @Override
  public LocatorImpl getBaseLocator() {
    Json baseLocator = at(BASE_LOCATOR_PP);
    return baseLocator == null ? null : createLocator(baseLocator.asString());
  }

  @Override
  public void setBaseLocator(LocatorImpl locator) {
    set(BASE_LOCATOR_PP, locator.getReference());
  }
  
}
