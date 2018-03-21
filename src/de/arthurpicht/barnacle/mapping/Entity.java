package de.arthurpicht.barnacle.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.arthurpicht.barnacle.BarnacleInitializer;
import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;


@SuppressWarnings("rawtypes")
public class Entity {

	private GeneratorConfiguration generatorConfiguration;
	
//	private String vofClassName;
	private Class vofClass;
	private String tableName;
	private List<Attribute> attributes;
	private Map<String, List<Attribute>> uniqueConstraints;
	private Map<String, ForeignKeyWrapper> foreignKeyConstraints;
	private boolean vobFactoryMethod = false;
	
	private boolean isAssociationTable = false;
	private ForeignKeyWrapper associationForeignKeyA = null;
	private ForeignKeyWrapper associationForeignKeyB = null;
	
//	public Entity(String vofClassName) {
//		this.vofClassName = vofClassName;
//		this.attributes = new ArrayList<Attribute>();		
//	}

	public Entity(Class vofClass) {
	    this.generatorConfiguration = GeneratorContext.getInstance().getGeneratorConfiguration();

		this.vofClass = vofClass;
		this.attributes = new ArrayList<Attribute>();
		this.uniqueConstraints = new HashMap<String, List<Attribute>>();
		this.foreignKeyConstraints = new HashMap<String, ForeignKeyWrapper>();
	}
	
//	public String getVofClassName() {
//		return this.vofClassName;
//	}
	
	public Class getVofClass() {
		return this.vofClass;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void addAttribute(Attribute attribute) {
		this.attributes.add(attribute);
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	/**
	 * Returns all attributes except attributes annotated
	 * as auto increment.
	 * 
	 * @return
	 */
	public List<Attribute> getNonAutoIncrementAttributes() {
		List<Attribute> nonAutoIncrementAttributes = new ArrayList<Attribute>();
		for (Attribute attribute : this.attributes) {
			if (!attribute.isAutoIncrement()) {
				nonAutoIncrementAttributes.add(attribute);
			}
		}
		return nonAutoIncrementAttributes;
	}
	
	/**
	 * If existing returns the auto increment attribute.
	 * Returns null in other cases.
	 *  
	 * @return
	 */
	public Attribute getAutoIncrementAttribute() {
		for (Attribute attribute : this.attributes) {
			if (attribute.isAutoIncrement()) {
				return attribute;
			}
		}
		return null;
	}
	
	/**
	 * Determines the number of field defined as primary key
	 * in this entity.
	 * 
	 * @return
	 */
	public int getNrPkAttributes() {
		int nrPkAttributes = 0;
		for (Attribute attribute : this.attributes) {
			if (attribute.isPrimaryKey()) {
				nrPkAttributes ++;
			}
		}		
		return nrPkAttributes;
	}
	
	/**
	 * Determines whether primary key is a composed primary key.
	 * 
	 * @return
	 */
	public boolean isComposedPk() {
		return this.getNrPkAttributes() > 1;
	}
	
	/**
	 * Returns all attributes annotated as primary key.
	 * 
	 * @return
	 */
	public List<Attribute> getPkAttributes() {
		List<Attribute> pkAttributes = new ArrayList<Attribute>();
		for (Attribute attribute : this.attributes) {
			if (attribute.isPrimaryKey()) {
				pkAttributes.add(attribute);
			}
		}
		return pkAttributes;
	}
	
	/**
	 * Returns all attributes NOT annotated as primary key.
	 * 
	 * @return
	 */
	public List<Attribute> getNonPkAttributes() {
		List<Attribute> nonPkAttributes = new ArrayList<Attribute>();
		for (Attribute attribute : this.attributes) {
			if (!attribute.isPrimaryKey()) {
				nonPkAttributes.add(attribute);
			}
		}
		return nonPkAttributes;
	}
	
	/**
	 * Returns attribute object by its field name. Returns null if no
	 * attribute exists with this field name.
	 * 
	 * @param fieldName
	 * @return
	 */
	public Attribute getAttributeByFieldName(String fieldName) {
		for (Attribute attribute : this.attributes) {
			if (attribute.getFieldName().equals(fieldName)) {
				return attribute;
			}
		}
		return null;
	}
	
	/**
	 * Returns attribute object by its column name. Returns null if no
	 * attribute exists with this column name.
	 * 
	 * @param columnName
	 * @return
	 */
	public Attribute getAttributeByColumnName(String columnName) {
		for (Attribute attribute : this.attributes) {
			if (attribute.getColumnName().equals(columnName)) {
				return attribute;
			}
		}
		return null;
	}
	
	/**
	 * Adds an attribute to the referenced unique index. If no index with
	 * the given name is preexisting, a new index is created.
	 * 
	 * @param indexName
	 * @param attribute
	 */
	public void addUniqueField(String indexName, Attribute attribute) {
		
		List<Attribute> uniqueAttributeList = this.uniqueConstraints.get(indexName);
		if (uniqueAttributeList == null) {
			uniqueAttributeList = new ArrayList<Attribute>();
			uniqueAttributeList.add(attribute);
			this.uniqueConstraints.put(indexName, uniqueAttributeList);
		} else {
			uniqueAttributeList.add(attribute);
		}
	}
	
	/**
	 * Returns all names of the unique indices.
	 * 
	 * @return
	 */
	public Set<String> getAllUniqueIndicesNames() {
		return this.uniqueConstraints.keySet();
	}
	
	/**
	 * Returns a foreign key by passed name. If requested foreign
	 * key object does not exist, null is returned.
	 * 
	 * @param foreignKeyName
	 * @return
	 */
	public ForeignKeyWrapper getForeignKeyByName(String foreignKeyName) {
		ForeignKeyWrapper foreignKey = this.foreignKeyConstraints.get(foreignKeyName);
		return foreignKey;
	}
	
	
	/**
	 * Returns a foreign key by passed name. If requested foreign
	 * key object does not exist, a new one is created, stored and
	 * given back. If the passed parameter foreignKeyName is empty,
	 * a generated key name is used to create a new foreign key 
	 * object.
	 * 
	 * @param foreignKeyName
	 * @return
	 */
	public ForeignKeyWrapper getOrCreateForeignKeyByName(String foreignKeyName) {
		ForeignKeyWrapper foreignKey = this.foreignKeyConstraints.get(foreignKeyName);
		if (foreignKey == null) {
			if (foreignKeyName.equals("")) {				
				foreignKeyName = getNextForeignKeyName();				
			}
			foreignKey = new ForeignKeyWrapper(this, foreignKeyName);
			this.foreignKeyConstraints.put(foreignKeyName, foreignKey);
		} 
		return foreignKey;
	}
	
	/**
	 * Determines the next unused foreign key name following the
	 * pattern 'fk_{tablename}_{1, 2, ...}' 
	 * 
	 * @return
	 */
	private String getNextForeignKeyName() {
		int i = 0;
		String nextForeignKeyName = new String();
		ForeignKeyWrapper foreignKey;
		do {
			i++;
			nextForeignKeyName = "fk_" + this.getTableName() + "_" + i;
			foreignKey = this.foreignKeyConstraints.get(nextForeignKeyName);
		} while (foreignKey != null);
		return nextForeignKeyName;
	}
	
	/**
	 * Returns all names of defined foreign keys.
	 * 
	 * @return
	 */
	public Set<String> getAllForeignKeyNames() {
		return this.foreignKeyConstraints.keySet();
	}
	
	/**
	 * Returns all foreign keys.
	 * 
	 * @return
	 */
	public Set<ForeignKeyWrapper> getAllForeignKeys() {
		Set<ForeignKeyWrapper> foreignKeySet = new HashSet<ForeignKeyWrapper>();
		for (String foreignKeyName : this.foreignKeyConstraints.keySet()) {
			ForeignKeyWrapper foreignKeyWrapper = this.foreignKeyConstraints.get(foreignKeyName);
			foreignKeySet.add(foreignKeyWrapper);
		}
		return foreignKeySet;
	}
	
	/**
	 * Returns all foreign keys that reference this entity.
	 * 
	 * @return
	 */
	public Set<ForeignKeyWrapper> getAllReferencingForeignKeys() {
		Set<ForeignKeyWrapper> referencingForeignKeyWrapper = new HashSet<ForeignKeyWrapper>();
		for (Entity entity : EntityCollection.getEntities()) {
			for (ForeignKeyWrapper foreignKeyWrapper : entity.getAllForeignKeys()) {
				if (foreignKeyWrapper.getTargetEntity().getTableName().equals(this.getTableName())) {
					referencingForeignKeyWrapper.add(foreignKeyWrapper);
				}
			}
		}
		return referencingForeignKeyWrapper;
	}
	
	/**
	 * Returns all attributes assigned to given index.
	 * 
	 * @param indexName
	 * @return
	 */
	public List<Attribute> getAttributesByUniqueIndexName(String indexName) {
		return this.uniqueConstraints.get(indexName);
	}
	
	/**
	 * Checks whether value object contains a vob factory method. 
	 * 
	 * @return
	 */
	public boolean isVobFactoryMethod() {
		return this.vobFactoryMethod;
	}
	
	/**
	 * Defines wheather value object contains a vob factory method.
	 * 
	 * @param vobFactoryMethod
	 */
	public void setVobFactoryMethod(boolean vobFactoryMethod) {
		this.vobFactoryMethod = vobFactoryMethod;
	}
	
	/**
	 * Returns the simple name of the corresponding value object class.
	 * 
	 * @return
	 */
	public String getVoSimpleClassName() {
		String vofSimpleClassName = this.vofClass.getSimpleName();
		String voSimpleClassName = vofSimpleClassName.substring(0, vofSimpleClassName.length()-3) + "VO";
		return voSimpleClassName;
	}
	
	/**
	 * Returns the canonical name of the corresponding value object class.
	 * 
	 * @return
	 */
	public String getVoCanonicalClassName() {
		return this.generatorConfiguration.getVoPackageName() + "." + this.getVoSimpleClassName();
	}
	
	/**
	 * Returns the simple name of the corresponding value object business class.
	 * 
	 * @return
	 */
	public String getVobSimpleClassName() {
		String vofSimpleClassName = this.vofClass.getSimpleName();
		String vobSimpleClassName = vofSimpleClassName.substring(0, vofSimpleClassName.length()-3) + "VOB";
		return vobSimpleClassName;
	}
	
	/**
	 * Returns the canonical name of the corresponding value object business class.
	 * 
	 * @return
	 */
	public String getVobCanonicalClassName() {
		return this.generatorConfiguration.getVobPackageName() + "." + this.getVobSimpleClassName();
	}
	
	/**
	 * Returns the simple name of the corresponding data access object class.
	 * 
	 * @return
	 */
	public String getDaoSimpleClassName() {
		String vofSimpleClassName = this.vofClass.getSimpleName();
		String daoSimpleClassName = vofSimpleClassName.substring(0, vofSimpleClassName.length()-3) + "DAO";
		return daoSimpleClassName;
	}
	
	/**
	 * Returns the canonical name of the corresponding data access object class.
	 * 
	 * @return
	 */
	public String getDaoCanonicalClassName() {
		return this.generatorConfiguration.getDaoPackageName() + "." + this.getDaoSimpleClassName();
	}
	
	/**
	 * Returns the simple name of the corresponding value field object class.
	 * @return
	 */
	public String getVofSimpleClassName() {
		return this.vofClass.getSimpleName();
	}
	
	/**
	 * Returns the simple name of the corresponding value object class 
	 * containing the private key fields.
	 * 
	 * @return
	 */
	public String getPkSimpleClassName() {
		String vofSimpleClassName = this.vofClass.getSimpleName();
		String pkSimpleClassName = vofSimpleClassName.substring(0, vofSimpleClassName.length()-3) + "PK";
		return pkSimpleClassName;
	}
	
	/**
	 * Returns the canonical name of the corresponding value object class
	 * containing the private key fields.
	 * 
	 * @return
	 */
	public String getPkCanonicalClassName() {
		return this.generatorConfiguration.getVoPackageName() + "." + this.getPkSimpleClassName();
	}

	/**
	 * Checks whether this entity represents an association-table.
	 * 
	 * @return
	 */
	public boolean isAssociationTable() {
		return isAssociationTable;
	}
	
	/**
	 * Defines this entity as an representation for an association-table. 
	 * 
	 * @param isAssociationTable
	 */
	public void setAssociationTable(boolean isAssociationTable) {
		this.isAssociationTable = isAssociationTable;
	}
	
	/**
	 * Returns foreignKey as first part of association. 
	 * 
	 * @return
	 */
	public ForeignKeyWrapper getAssociationForeignKeyA() {
		return associationForeignKeyA;
	}

	/**
	 * Sets foreignKey as first part of association.
	 * 
	 * @param associationForeignKeyA
	 */
	public void setAssociationForeignKeyA(ForeignKeyWrapper associationForeignKeyA) {
		this.associationForeignKeyA = associationForeignKeyA;
	}

	/**
	 * Returns foreignKey as seconf part of association.
	 * 
	 * @return
	 */
	public ForeignKeyWrapper getAssociationForeignKeyB() {
		return associationForeignKeyB;
	}

	/**
	 * Sets foreignKey as second part of association.
	 * 
	 * @param associationForeignKeyB
	 */
	public void setAssociationForeignKeyB(ForeignKeyWrapper associationForeignKeyB) {
		this.associationForeignKeyB = associationForeignKeyB;
	}

	public String toString() {
		String string = "Entity vobClassName=" + this.vofClass.getSimpleName() + " tableName=" + tableName;
		string += "\n\tAttributes:";
		List<Attribute> attributeList = this.getAttributes();
		for (Attribute attribute : attributeList) {
			string += "\n\t\t" + attribute.toString();
		}
		
		string += "\n\tUnique Constraints:";
		Set<String> uniqueKeyNames = this.uniqueConstraints.keySet();
		for (String keyName : uniqueKeyNames) {
			List<Attribute> uniqueAttributes = this.uniqueConstraints.get(keyName);
			string += "\n\t\tkey name: " + keyName;
			for (Attribute attribute : uniqueAttributes) {
				string += "\n\t\t" + attribute.toString();
			}
		}
		
		Set<String> foreignKeyNames = this.foreignKeyConstraints.keySet();
		for (String keyName : foreignKeyNames) {
			ForeignKeyWrapper foreignKeyWrapper = this.foreignKeyConstraints.get(keyName);
			string += "\n\t" + foreignKeyWrapper.toString();
		}
		
		string += "\n\tIs Association Table?:" + this.isAssociationTable;
		if (this.isAssociationTable) {
			string += "\n\t\tForeignKeyA: " + this.associationForeignKeyA.toString();
			string += "\n\t\tForeignKeyB: " + this.associationForeignKeyB.toString();
		}
		
		return string;			
	}	

}
