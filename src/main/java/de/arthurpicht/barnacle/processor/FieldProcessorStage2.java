package de.arthurpicht.barnacle.processor;

import de.arthurpicht.barnacle.annotations.Annotations.Barnacle;
import de.arthurpicht.barnacle.annotations.Annotations.ForeignKey;
import de.arthurpicht.barnacle.helper.StringHelper;
import de.arthurpicht.barnacle.model.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class FieldProcessorStage2 {
	
	public static void process(Entity entity, EntityRelationshipModel entityRelationshipModel) {

		Class<?> vofClass = entity.getVofClass();
		Field[] fields = vofClass.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Barnacle.class)) {
				processField(field, entity, entityRelationshipModel);
			}
		}
	}
	
	private static void processField(Field field, Entity entity, EntityRelationshipModel entityRelationshipModel) {
		
		if (field.isAnnotationPresent(ForeignKey.class)) {
			
			ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
			
			String[] referenceTableName = foreignKey.referenceTableName();
			String[] referenceColumnName = foreignKey.referenceColumnName();
			String[] foreignKeyName = foreignKey.foreignKeyName();
			boolean[] onDeleteCascade = foreignKey.onDeleteCascade();
			boolean[] onUpdateCascade = foreignKey.onUpdateCascade();
			boolean[] getEntityMethod = foreignKey.getEntityMethod();
			boolean[] setEntityMethod = foreignKey.setEntityMethod();
			String[] entityMethodName = foreignKey.entityMethodName();
			boolean[] getReferenceEntityMethod = foreignKey.getReferenceEntityMethod();
			boolean[] setReferenceEntityMethod = foreignKey.setReferenceEntityMethod();
			String[] referenceEntityMethodName = foreignKey.referenceEntityMethodName();
			
			List<ForeignKeyAnnotation> foreignKeyAnnotationList = new ArrayList<>();
						
			int length = referenceTableName.length;
			for (int i=0; i<length; i++) {
				ForeignKeyAnnotation foreignKeyAnnotation = new ForeignKeyAnnotation();
				foreignKeyAnnotationList.add(foreignKeyAnnotation);
				
				foreignKeyAnnotation.setReferenceTableName(referenceTableName[i]);
				
				try {
					foreignKeyAnnotation.setReferenceColumnName(referenceColumnName[i]);
				} catch (IndexOutOfBoundsException e) {
					throw new ERMBuilderException("referenceColumnName missing");
				}

				try {
					foreignKeyAnnotation.setForeignKeyName(foreignKeyName[i]);
				} catch (IndexOutOfBoundsException e) {
					foreignKeyAnnotation.setForeignKeyName("");
				}
				
				try {
					foreignKeyAnnotation.setOnDeleteCascade(onDeleteCascade[i]);
				} catch (IndexOutOfBoundsException ignored) {
				}

				try {
					foreignKeyAnnotation.setOnUpdateCascade(onUpdateCascade[i]);
				} catch (IndexOutOfBoundsException ignored) {
				}
				
				try {
					foreignKeyAnnotation.setGetEntityMethod(getEntityMethod[i]);
				} catch (IndexOutOfBoundsException ignored) {
				}
				
				try {
					foreignKeyAnnotation.setSetEntityMethod(setEntityMethod[i]);
				} catch (IndexOutOfBoundsException ignored) {
				}
				
				try {
					foreignKeyAnnotation.setEntityMethodName(entityMethodName[i]);
				} catch (IndexOutOfBoundsException ignored) {
					
				}
				
				try {
					foreignKeyAnnotation.setGetReferenceEntityMethod(getReferenceEntityMethod[i]);
				} catch (IndexOutOfBoundsException ignored) {
				}
				
				try {
					foreignKeyAnnotation.setSetReferenceEntityMethod(setReferenceEntityMethod[i]);
				} catch (IndexOutOfBoundsException ignored) {
				}
				
				try {
					foreignKeyAnnotation.setReferenceEntityMethodName(referenceEntityMethodName[i]);
				} catch (IndexOutOfBoundsException ignored) {
				}
			}
			
			for (ForeignKeyAnnotation foreignKeyAnnotation : foreignKeyAnnotationList) {
				processForeignKey(entityRelationshipModel, foreignKeyAnnotation, entity, field);
			}
		}
		
	}
	
	private static void processForeignKey(
			EntityRelationshipModel entityRelationshipModel,
			ForeignKeyAnnotation annotationWrapper,
			Entity entity,
			Field field) {
		
		//
		// foreign key Name
		//
		String foreignKeyName = annotationWrapper.getForeignKeyName();
		ForeignKeyWrapper foreignKeyWrapper = entity.getOrCreateForeignKeyByName(foreignKeyName);
		// get keyName as defined by user or generated
		foreignKeyName = foreignKeyWrapper.getForeignKeyName();
		
		//
		// reference table name 
		//
		String referenceTableName = annotationWrapper.getReferenceTableName();
		Entity referenceEntity = entityRelationshipModel.getEntityByTableName(referenceTableName);
		if (referenceEntity == null) {
			throw new ERMBuilderException("Error in foreign key definition. "
					+ "class: " + entity.getVofClass().getSimpleName() + ", field: " + field.getName() + ", 'referenceTableName=" 
					+ referenceTableName + "'. Entity does not exist!");
					
		}
		
		boolean success = foreignKeyWrapper.setTargetEntity(referenceEntity);
		if (!success) {
			throw new ERMBuilderException("Inconsistent foreign key definition: same index name but different target " +
					"tables. VOF class: " + entity.getVofClass().getSimpleName() + "; field: " + field.getName());
		}
		
		String fieldName = field.getName();
		Attribute attribute = entity.getAttributeByFieldName(fieldName);
		
		//
		// reference column name
		//
		String referenceColumnName = annotationWrapper.getReferenceColumnName();
		Attribute referenceAttribute = referenceEntity.getAttributeByColumnName(referenceColumnName);
		
		if (referenceAttribute == null) {
			throw new ERMBuilderException("Error in foreign key definition. "
					+ "class: " + entity.getVofClass().getSimpleName() + ", field: " + field.getName() + ", 'referenceTableName="
					+ referenceTableName + "', 'referenceColumnName="	+ referenceColumnName + "'. Column does not exist.");
		}
		
		foreignKeyWrapper.addFields(attribute, referenceAttribute);
		
		// 
		// further flags: onDeleteCascade, onUpdateCascade, get/set EntityMethod
		//
		boolean onDeleteCascade = annotationWrapper.isOnDeleteCascade();
		foreignKeyWrapper.setOnDeleteCascade(onDeleteCascade);
		
		boolean onUpdateCascade = annotationWrapper.isOnUpdateCascade();
		foreignKeyWrapper.setOnUpdateCascade(onUpdateCascade);
		
		boolean getEntityMethod = annotationWrapper.isGetEntityMethod();
		foreignKeyWrapper.setGetEntityMethod(getEntityMethod);
		
		boolean setEntityMethod = annotationWrapper.isSetEntityMethod();
		foreignKeyWrapper.setSetEntityMethod(setEntityMethod);
		
		String entityMethodName = annotationWrapper.getEntityMethodName();
		if (entityMethodName.equals("")) {
			
			// If no method name given, generate one by this pattern:
			// getMyVoByFk3
			
			entityMethodName = "get" 
				+ foreignKeyWrapper.getTargetEntity().getVoSimpleClassName()
				+ "By"
				+ StringHelper.shiftFirstLetterToUpperCase(foreignKeyName);
		} else {

			entityMethodName = "get" + StringHelper.shiftFirstLetterToUpperCase(entityMethodName);
		}
		foreignKeyWrapper.setEntityMethodName(entityMethodName);
		
		boolean getReferenceEntityMethod = annotationWrapper.isGetReferenceEntityMethod();
		foreignKeyWrapper.setGetReferenceEntityMethod(getReferenceEntityMethod);
		
		boolean setReferenceEntityMethod = annotationWrapper.isSetReferenceEntityMethod();
		foreignKeyWrapper.setSetReferencedEntityMethod(setReferenceEntityMethod);
		
		String referenceEntityMethodName = annotationWrapper.getReferenceEntityMethodName();
		if (referenceEntityMethodName.equals("")) {
			
			referenceEntityMethodName = "get"
				+ entity.getVoSimpleClassName()
				+ "By"
				+ StringHelper.shiftFirstLetterToUpperCase(foreignKeyName);
		}  else {
			referenceEntityMethodName = "get" + StringHelper.shiftFirstLetterToUpperCase(referenceEntityMethodName);
		}
		
		foreignKeyWrapper.setReferencedEntityMethodName(referenceEntityMethodName);
	}

}
