package de.arthurpicht.barnacle.processor;

import de.arthurpicht.barnacle.annotations.Annotations.Barnacle;
import de.arthurpicht.barnacle.annotations.Annotations.ForeignKey;
import de.arthurpicht.barnacle.helper.Helper;
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
			
			List<ForeignKeyAnnotationWrapper> foreignKeyAnnotations = new ArrayList<>();
						
			int length = referenceTableName.length;
			for (int i=0; i<length; i++) {
				ForeignKeyAnnotationWrapper annotationWrapper = new ForeignKeyAnnotationWrapper();
				foreignKeyAnnotations.add(annotationWrapper);
				
				annotationWrapper.setReferenceTableName(referenceTableName[i]);
				
				try {
					annotationWrapper.setReferenceColumnName(referenceColumnName[i]);
				} catch (IndexOutOfBoundsException e) {
					throw new ERMBuilderException("referenceColumnName missing");
				}

				try {
					annotationWrapper.setForeignKeyName(foreignKeyName[i]);
				} catch (IndexOutOfBoundsException e) {
					annotationWrapper.setForeignKeyName("");
				}
				
				try {
					annotationWrapper.setOnDeleteCascade(onDeleteCascade[i]);
				} catch (IndexOutOfBoundsException e) {
				}

				try {
					annotationWrapper.setOnUpdateCascade(onUpdateCascade[i]);
				} catch (IndexOutOfBoundsException e) {					
				}
				
				try {
					annotationWrapper.setGetEntityMethod(getEntityMethod[i]);
				} catch (IndexOutOfBoundsException e) {					
				}
				
				try {
					annotationWrapper.setSetEntityMethod(setEntityMethod[i]);
				} catch (IndexOutOfBoundsException e) {					
				}
				
				try {
					annotationWrapper.setEntityMethodName(entityMethodName[i]);
				} catch (IndexOutOfBoundsException e) {
					
				}
				
				try {
					annotationWrapper.setGetReferenceEntityMethod(getReferenceEntityMethod[i]);
				} catch (IndexOutOfBoundsException e) {					
				}
				
				try {
					annotationWrapper.setSetReferenceEntityMethod(setReferenceEntityMethod[i]);
				} catch (IndexOutOfBoundsException e) {					
				}
				
				try {
					annotationWrapper.setReferenceEntityMethodName(referenceEntityMethodName[i]);
				} catch (IndexOutOfBoundsException e) {					
				}
			}
			
			for (ForeignKeyAnnotationWrapper foreignKeyAnnotationWrapper : foreignKeyAnnotations) {
				processForeignKey(entityRelationshipModel, foreignKeyAnnotationWrapper, entity, field);
			}
		}
		
	}
	
	private static void processForeignKey(
			EntityRelationshipModel entityRelationshipModel,
			ForeignKeyAnnotationWrapper annotationWrapper,
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
		// further flags: onDelteCascase, onUpdateCascade, get/set EntityMethod
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
				+ Helper.shiftCaseFirstLetter(foreignKeyName);
		} else {

			entityMethodName = "get" + Helper.shiftCaseFirstLetter(entityMethodName);
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
				+ Helper.shiftCaseFirstLetter(foreignKeyName);
		}  else {
			referenceEntityMethodName = "get" + Helper.shiftCaseFirstLetter(referenceEntityMethodName);
		}
		
		foreignKeyWrapper.setReferencedEntityMethodName(referenceEntityMethodName);
	}

}
