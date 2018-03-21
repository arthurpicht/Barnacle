package de.arthurpicht.barnacle.processor;

import java.util.ArrayList;
import java.util.List;

import de.arthurpicht.barnacle.exceptions.GeneratorException;
import de.arthurpicht.barnacle.mapping.Attribute;
import de.arthurpicht.barnacle.mapping.Entity;
import de.arthurpicht.barnacle.mapping.ForeignKeyWrapper;



/**
 * Checks relation validity: 
 * 
 * 1. Referencing type and referenced type must be equal.
 * 2. Referenced attributes must be part of a primary key. 
 * 2. Referencing attributes must correspond to the first
 * key attributes.
 * 4. Referencing attributes and referenced attributes must
 * be in the same order. If not, referencing attributes are
 * reordered. 
 * 
 * For additional infotmation see: MySQL reference guide,
 * section 'foreign key constraints': 
 * http://dev.mysql.com/doc/refman/5.0/en/innodb-foreign-key-constraints.html
 * 
 * @author Arthur Picht, Arthur Picht GmbH, (c) 2007
 *
 */
public class RelationValidator {
	
	/**
	 * Processes some entity validation tasks.
	 *  
	 * @param entity
	 * @throws GeneratorException
	 */
	public static void validate(Entity entity) throws GeneratorException {
		
		for (ForeignKeyWrapper foreignKeyWrapper : entity.getAllForeignKeys()) {
			
			checkForTypeEquality(foreignKeyWrapper);
			
			checkForReferencingPrimaryKey(foreignKeyWrapper);
			
			checkForFurtherPrimaryKeyConditions(foreignKeyWrapper);
		}
		
		checkForAssociationTableConstraint(entity);
	}
	
	/**
	 * Checks referencing and referenced attribute for equality 
	 * in SQL-types.
	 * 
	 * @param foreignKeyWrapper
	 * @throws GeneratorException
	 */
	private static void checkForTypeEquality(ForeignKeyWrapper foreignKeyWrapper) throws GeneratorException {
		List<Attribute> keyFieldAttributeList = foreignKeyWrapper.getKeyFieldAttributes();
		List<Attribute> referencedFieldAttributeList = foreignKeyWrapper.getTargetFieldAttributes();
		
		for (int i=0; i<keyFieldAttributeList.size(); i++) {
			
			Attribute keyField = keyFieldAttributeList.get(i);
			String sqlTypeKeyField = keyField.getSqlDataType();
			
			Attribute referencedField = referencedFieldAttributeList.get(i);
			String sqlTypeReferncedField = referencedField.getSqlDataType();
			
			if (!sqlTypeKeyField.equals(sqlTypeReferncedField)) {
				throw new GeneratorException("Foreign key constraint definition error: types do not match! Entity table name: " 
				+ foreignKeyWrapper.getParentEntity().getTableName() + ", field: " + keyField.getFieldName()
				+ ", types: " + sqlTypeKeyField + " and " + sqlTypeReferncedField);
			}
			
		}
	}
	
	/**
	 * Checks wheather referenced attribute is defined as primary key field.
	 * 
	 * @param foreignKeyWrapper
	 * @throws GeneratorException
	 */
	private static void checkForReferencingPrimaryKey(ForeignKeyWrapper foreignKeyWrapper) throws GeneratorException {
		
		List<Attribute> keyFieldAttributeList = foreignKeyWrapper.getKeyFieldAttributes();
		List<Attribute> referencedFieldAttributeList = foreignKeyWrapper.getTargetFieldAttributes();
		
		for (int i=0; i<keyFieldAttributeList.size(); i++) {
			Attribute keyField = keyFieldAttributeList.get(i);
			Attribute referencedField = referencedFieldAttributeList.get(i);
			
			// check wheather referenced field is primary key field
			if (!referencedField.isPrimaryKey()) {
				throw new GeneratorException("Foreign key constraint definition error: Referenced field is no primary key field! Entity with table: "
						+ foreignKeyWrapper.getParentEntity().getTableName() + ", field: " + keyField.getFieldName()
						+ ", referenced Field: " + referencedField);
			}
		}
	}
	
	/**
	 * Checks wheather referencing fields match first fields of referenced primary key fields.
	 * Adjusts order of referencing fields if necessary.
	 * 
	 * @param foreignKeyWrapper
	 * @throws GeneratorException
	 */
	private static void checkForFurtherPrimaryKeyConditions(ForeignKeyWrapper foreignKeyWrapper) throws GeneratorException {
		Entity referencedEntity = foreignKeyWrapper.getTargetEntity();
		List<Attribute> referencedEntityPKAttributeList = referencedEntity.getPkAttributes();

		List<Attribute> keyFieldAttributeList = foreignKeyWrapper.getKeyFieldAttributes();
		List<Attribute> referencedFieldAttributeList = foreignKeyWrapper.getTargetFieldAttributes();
		
		if (referencedEntityPKAttributeList.size() < keyFieldAttributeList.size()) {
			// Actually this can not happen when passed checkForPrimaryKey method.
			throw new GeneratorException("Foreign key constraint definition error: Referenced field is no primary key field! Entity with table: "
						+ foreignKeyWrapper.getParentEntity().getTableName() + " Some field(s) referencing  non primary key field");
		}
		
		List<Attribute> newKeyFieldAttributeList = new ArrayList<Attribute>();
		List<Attribute> newReferencedFieldAttributeList = new ArrayList<Attribute>();
		
		for (int i=0; i<keyFieldAttributeList.size(); i++) {
			Attribute keyField = keyFieldAttributeList.get(i);
			Attribute referencedField = referencedFieldAttributeList.get(i);
			Attribute actPKField = referencedEntityPKAttributeList.get(i);
			
			if (referencedField.equals(actPKField)) {
				newKeyFieldAttributeList.add(keyField);
				newReferencedFieldAttributeList.add(referencedField);
			} else {
				int ri = referencedFieldAttributeList.indexOf(actPKField);
				if (ri < 0) {
					throw new GeneratorException("Foreign key constraint definition error: Referencing fields do " +
							"not match first primary key fields of referenced entity!"
							+ " Entity table name: " + foreignKeyWrapper.getParentEntity().getTableName() 
							+ ", ForeignKeyName: " + foreignKeyWrapper.getForeignKeyName());
				} 
				newKeyFieldAttributeList.add(keyFieldAttributeList.get(ri));
				newReferencedFieldAttributeList.add(referencedFieldAttributeList.get(ri));				
			}
			
			foreignKeyWrapper.setKeyFieldAttributes(newKeyFieldAttributeList);
			foreignKeyWrapper.setTargetFieldAttributes(newReferencedFieldAttributeList);
		}		

	}
	
	private static void checkForAssociationTableConstraint(Entity entity) throws GeneratorException {
		
		if (entity.isAssociationTable()) {
			List<Attribute> pkAttributes = entity.getPkAttributes();
			List<Attribute> fkAttributesA = entity.getAssociationForeignKeyA().getKeyFieldAttributes();
			List<Attribute> fkAttributesB = entity.getAssociationForeignKeyB().getKeyFieldAttributes();
			
			for (Attribute fkAttributeA : fkAttributesA) {
				if (!pkAttributes.contains(fkAttributeA)) {
					throw new GeneratorException("Association-Table definition error: Foreign key attribute '" + fkAttributeA.getFieldName() + "'" +
							" is not primary key.  Violation against [ConstAT_1]");
				}
			}
			
			for (Attribute fkAttributeB : fkAttributesB) {
				if (!pkAttributes.contains(fkAttributeB)) {
					throw new GeneratorException("Association-Table definition error: Foreign key attribute '" + fkAttributeB.getFieldName() + "'" +
							" is not part of primary key.  Violation against [ConstAT_1]");
				}
			}
			
			for (Attribute pkAttribute : pkAttributes) {
				if (!fkAttributesA.contains(pkAttribute)) {
					if (!fkAttributesB.contains(pkAttribute)) {
						throw new GeneratorException("Association-Table definition error: Primary key attribute '" + pkAttribute.getFieldName() + "'" +
						" is not part of foreign key.  Violation against [ConstAT_1]");
					}
				}
			}

			if (pkAttributes.size() != fkAttributesA.size() + fkAttributesB.size()) {
				throw new GeneratorException("Association-Table definition error: Primary must be the sum of all foreign" +
				" key attributes defining association. Violation against [ConstAT_1]");
			}
		}
	}

	
}
