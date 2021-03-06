package org.gingolph.gingmap.hg;

import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.handle.UUIDPersistentHandle;

/**
 * <p>
 * This class lists some static constants related to the HyperGraphDB Topic Maps API implementation.
 * </p>
 * 
 * @author Borislav Iordanov
 */
public class HGTM {
  public final static String APPLICATION_NAME = "TopicMaps";
  public final static String TOPIC_MAP_SYSTEM_LOCATION = "org.hypergraphdb.app.tm.hgdb";

  public static final HGPersistentHandle hSubjectIdentifier =
      UUIDPersistentHandle.makeHandle("93dd540d-354a-11dc-be9a-aaea7f210e4b");
  public static final String SubjectIdentifier = "SubjectIdentifier";

  public static final HGPersistentHandle hSubjectLocator =
      UUIDPersistentHandle.makeHandle("9d7e89ce-354a-11dc-be9a-aaea7f210e4b");
  public static final String SubjectLocator = "SubjectLocator";

  public static final HGPersistentHandle hItemIdentifier =
      UUIDPersistentHandle.makeHandle("2bf19637-3e0a-11dc-be53-bc672cb3ce6d");
  public static final String ItemIdentifier = "SourceLocator";

  public static final HGPersistentHandle hOccurrenceOf =
      UUIDPersistentHandle.makeHandle("b20ed20f-354a-11dc-be9a-aaea7f210e4b");
  public static final String OccurenceOf = "Occurrence";

  public static final HGPersistentHandle hTypeOf =
      UUIDPersistentHandle.makeHandle("bd078180-354a-11dc-be9a-aaea7f210e4b");
  public static final String TypeOf = "TypeOf";

  public static final HGPersistentHandle hScopeOf =
      UUIDPersistentHandle.makeHandle("d6ed2783-35a3-11dc-b44d-8884da7d2355");
  public static final String ScopeOf = "ScopeOf";

  public static final HGPersistentHandle hReifierOf =
      UUIDPersistentHandle.makeHandle("6ff12044-35ad-11dc-b44d-8884da7d2355");
  public static final String ReifierOf = "ReifierOf";

  public static final HGPersistentHandle hNameOf =
      UUIDPersistentHandle.makeHandle("fea1a486-37ca-11dc-b44d-8884da7d2355");
  public static final String NameOf = "NameOf";

  public static final HGPersistentHandle hVariantOf =
      UUIDPersistentHandle.makeHandle("03072187-37cb-11dc-b44d-8884da7d2355");
  public static final String VariantOf = "VariantOf";

  public static final HGPersistentHandle hMapMember =
      UUIDPersistentHandle.makeHandle("5440f8d8-3bf6-11dc-b44d-8884da7d2355");
  public static final String MapMember = "MapMember";


  //
  // Predefined types and subject identifiers.
  //

  // Type-Instance
  public static final String typeInstanceIdentifier =
      "http://psi.topicmaps.org/iso13250/model/type-instance";
  public static final String typeRoleIdentifier = "http://psi.topicmaps.org/iso13250/model/type";
  public static final String instanceRoleIdentifier =
      "http://psi.topicmaps.org/iso13250/model/instance";

  public static final HGPersistentHandle hTypeInstanceLocator =
      UUIDPersistentHandle.makeHandle("faaea15e-3f7e-11dc-be02-fe5652b30d4a");
  public static final HGPersistentHandle hTypeRoleLocator =
      UUIDPersistentHandle.makeHandle("ffc3bf4f-3f7e-11dc-be02-fe5652b30d4a");
  public static final HGPersistentHandle hInstanceRoleLocator =
      UUIDPersistentHandle.makeHandle("02ef5cc0-3f7f-11dc-be02-fe5652b30d4a");

  public static final HGPersistentHandle hTypeInstanceTopic =
      UUIDPersistentHandle.makeHandle("5d9032d4-3f7f-11dc-be02-fe5652b30d4a");
  public static final HGPersistentHandle hTypeRoleTopic =
      UUIDPersistentHandle.makeHandle("641e8345-3f7f-11dc-be02-fe5652b30d4a");
  public static final HGPersistentHandle hInstanceRoleTopic =
      UUIDPersistentHandle.makeHandle("68baeec6-3f7f-11dc-be02-fe5652b30d4a");

  // Supertype-subtype
  public static final String subtypingIdentifier =
      "http://psi.topicmaps.org/iso13250/model/supertype-subtype";
  public static final String superTypeRoleIdentifier =
      "http://psi.topicmaps.org/iso13250/model/supertype";
  public static final String subTypeRoleIdentifier =
      "http://psi.topicmaps.org/iso13250/model/subtype";

  public static final HGPersistentHandle hSubtypingLocator =
      UUIDPersistentHandle.makeHandle("06386d41-3f7f-11dc-be02-fe5652b30d4a");
  public static final HGPersistentHandle hSuperTypeLocator =
      UUIDPersistentHandle.makeHandle("09beadd2-3f7f-11dc-be02-fe5652b30d4a");
  public static final HGPersistentHandle hSubTypeLocator =
      UUIDPersistentHandle.makeHandle("0daed3c3-3f7f-11dc-be02-fe5652b30d4a");

  public static final HGPersistentHandle hSubtypingTopic =
      UUIDPersistentHandle.makeHandle("76358e27-3f7f-11dc-be02-fe5652b30d4a");
  public static final HGPersistentHandle hSuperTypeTopic =
      UUIDPersistentHandle.makeHandle("7a7ed098-3f7f-11dc-be02-fe5652b30d4a");
  public static final HGPersistentHandle hSubTypeTopic =
      UUIDPersistentHandle.makeHandle("7f0a4c29-3f7f-11dc-be02-fe5652b30d4a");

  // topic name
  public static final String topicNameIdentifier =
      "http://psi.topicmaps.org/iso13250/model/topic-name";
  public static final HGPersistentHandle hTopicNameLocator =
      UUIDPersistentHandle.makeHandle("c177c026-3fa0-11dc-bfc2-b65c7db952a2");
  public static final HGPersistentHandle hTopicNameTopic =
      UUIDPersistentHandle.makeHandle("d36ee6f7-3fa0-11dc-bfc2-b65c7db952a2");

  // Some XML Schema type constants
  public static final String schemaAnyURI = "http://www.w3.org/2001/XMLSchema#anyURI";
  public static final HGPersistentHandle hSchemaAnyURI =
      UUIDPersistentHandle.makeHandle("93666d28-3fa1-11dc-bfc2-b65c7db952a2");

  public static final String schemaAnyType = "http://www.w3.org/2001/XMLSchema#anyType";
  public static final HGPersistentHandle hSchemaAnyType =
      UUIDPersistentHandle.makeHandle("a3990099-3fa1-11dc-bfc2-b65c7db952a2");

  public static final String schemaStringType = "http://www.w3.org/2001/XMLSchema#string";
  public static final HGPersistentHandle hSchemaStringType =
      UUIDPersistentHandle.makeHandle("a80bc40a-3fa1-11dc-bfc2-b65c7db952a2");

  //
  // XTM 1.0 Mandatory published subject indicators
  //
  public static final String xtmCoreTopic = "http://www.topicmaps.org/xtm/1.0/core.xtm#topic";
  public static final String xtmCoreAssociation =
      "http://www.topicmaps.org/xtm/1.0/core.xtm#association";
  public static final String xtmCoreOccurrence =
      "http://www.topicmaps.org/xtm/1.0/core.xtm#occurrence";
  public static final String xtmCoreClassInstance =
      "http://www.topicmaps.org/xtm/1.0/core.xtm#class-instance";
  public static final String xtmCoreClass = "http://www.topicmaps.org/xtm/1.0/core.xtm#class";
  public static final String xtmCoreInstance = "http://www.topicmaps.org/xtm/1.0/core.xtm#instance";
  public static final String xtmCoreSuperclassSubclass =
      "http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass";
  public static final String xtmCoreSuperclass =
      "http://www.topicmaps.org/xtm/1.0/core.xtm#superclass";
  public static final String xtmCoreSubclass = "http://www.topicmaps.org/xtm/1.0/core.xtm#subclass";
  public static final String xtmCoreSort = "http://www.topicmaps.org/xtm/1.0/core.xtm#sort";
  public static final String xtmCoreDisplay = "http://www.topicmaps.org/xtm/1.0/core.xtm#display";

}
