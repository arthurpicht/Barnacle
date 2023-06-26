package de.arthurpicht.barnacle.model;

import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;

import java.util.*;

public class Entity {

	private final GeneratorConfiguration generatorConfiguration;
	private final Class<?> vofClass;
	private final List<Attribute> attributes;
	private final Map<String, List<Attribute>> uniqueConstraints;
	private final Map<String, ForeignKeyWrapper> foreignKeyConstraints;
	private String tableName;
	private boolean vobFactoryMethod = false;
	private boolean cloneable = false;
	private boolean isAssociationTable = false;
	private ForeignKeyWrapper associationForeignKeyA = null;
	private ForeignKeyWrapper associationForeignKeyB = null;

	public Entity(Class<?> vofClass, GeneratorConfiguration generatorConfiguration) {
		this.generatorConfiguration = generatorConfiguration;
		this.vofClass = vofClass;
		this.attributes = new ArrayList<>();
		this.uniqueConstraints = new HashMap<>();
		this.foreignKeyConstraints = new HashMap<>();
	}
	
	public Class<?> getVofClass() {
		return this.vofClass;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public void addAttribute(Attribute attribute) {
		this.attributes.add(attribute);
	}

	public List<Attribute> getAttributes() {
		return this.attributes;
	}
	
	public List<Attribute> getNonAutoIncrementAttributes() {
		List<Attribute> nonAutoIncrementAttributes = new ArrayList<>();
		for (Attribute attribute : this.attributes) {
			if (!attribute.isAutoIncrement()) {
				nonAutoIncrementAttributes.add(attribute);
			}
		}
		return nonAutoIncrementAttributes;
	}

	public boolean hasNonAutoIncAttributesAsObjectTypes() {
		return getNonAutoIncrementAttributes().stream()
				.anyMatch(a -> !a.isJavaTypeSimple());
	}

	public boolean hasAutoIncrementAttribute() {
		return this.attributes.stream().anyMatch(Attribute::isAutoIncrement);
	}

	public Attribute getAutoIncrementAttribute() {
		for (Attribute attribute : this.attributes) {
			if (attribute.isAutoIncrement()) {
				return attribute;
			}
		}
		throw new IllegalStateException("No auto increment attribute. Check before calling getter method.");
	}
	
	public int getNrPkAttributes() {
		int nrPkAttributes = 0;
		for (Attribute attribute : this.attributes) {
			if (attribute.isPrimaryKey()) {
				nrPkAttributes ++;
			}
		}		
		return nrPkAttributes;
	}
	
	public boolean isComposedPk() {
		return this.getNrPkAttributes() > 1;
	}
	
	public List<Attribute> getPkAttributes() {
		List<Attribute> pkAttributes = new ArrayList<>();
		for (Attribute attribute : this.attributes) {
			if (attribute.isPrimaryKey()) {
				pkAttributes.add(attribute);
			}
		}
		return pkAttributes;
	}

	public Attribute getSinglePkAttribute() {
		List<Attribute> pkAttributes = getPkAttributes();
		if (isComposedPk()) throw new IllegalStateException("No single PK attribute. Check before calling.");
		return pkAttributes.get(0);
	}
	
	public List<Attribute> getNonPkAttributes() {
		List<Attribute> nonPkAttributes = new ArrayList<>();
		for (Attribute attribute : this.attributes) {
			if (!attribute.isPrimaryKey()) {
				nonPkAttributes.add(attribute);
			}
		}
		return nonPkAttributes;
	}
	
	public Attribute getAttributeByFieldName(String fieldName) {
		for (Attribute attribute : this.attributes) {
			if (attribute.getFieldName().equals(fieldName)) {
				return attribute;
			}
		}
		return null;
	}
	
	public Attribute getAttributeByColumnName(String columnName) {
		for (Attribute attribute : this.attributes) {
			if (attribute.getColumnName().equals(columnName)) {
				return attribute;
			}
		}
		return null;
	}
	
	public void addUniqueField(String indexName, Attribute attribute) {
		
		List<Attribute> uniqueAttributeList = this.uniqueConstraints.get(indexName);
		if (uniqueAttributeList == null) {
			uniqueAttributeList = new ArrayList<>();
			uniqueAttributeList.add(attribute);
			this.uniqueConstraints.put(indexName, uniqueAttributeList);
		} else {
			uniqueAttributeList.add(attribute);
		}
	}
	
	public Set<String> getAllUniqueIndicesNames() {
		return this.uniqueConstraints.keySet();
	}
	
	public ForeignKeyWrapper getForeignKeyByName(String foreignKeyName) {
		return this.foreignKeyConstraints.get(foreignKeyName);
	}

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
	
	private String getNextForeignKeyName() {
		int i = 0;
		String nextForeignKeyName;
		ForeignKeyWrapper foreignKey;
		do {
			i++;
			nextForeignKeyName = "fk_" + this.getTableName() + "_" + i;
			foreignKey = this.foreignKeyConstraints.get(nextForeignKeyName);
		} while (foreignKey != null);
		return nextForeignKeyName;
	}
	
	public Set<String> getAllForeignKeyNames() {
		return this.foreignKeyConstraints.keySet();
	}
	
	public Set<ForeignKeyWrapper> getAllForeignKeys() {
		Set<ForeignKeyWrapper> foreignKeySet = new LinkedHashSet<>();
		for (String foreignKeyName : this.foreignKeyConstraints.keySet()) {
			ForeignKeyWrapper foreignKeyWrapper = this.foreignKeyConstraints.get(foreignKeyName);
			foreignKeySet.add(foreignKeyWrapper);
		}
		return foreignKeySet;
	}
	
	public List<Attribute> getAttributesByUniqueIndexName(String indexName) {
		return this.uniqueConstraints.get(indexName);
	}
	
	public boolean isVobFactoryMethod() {
		return this.vobFactoryMethod;
	}
	
	public void setVobFactoryMethod(boolean vobFactoryMethod) {
		this.vobFactoryMethod = vobFactoryMethod;
	}

	public boolean isCloneable() {
		return this.cloneable;
	}

	public void setAsCloneable() {
		this.cloneable = true;
	}

	public String getVoSimpleClassName() {
		String vofSimpleClassName = this.vofClass.getSimpleName();
		return vofSimpleClassName.substring(0, vofSimpleClassName.length() - 3) + "VO";
	}
	
	public String getVoCanonicalClassName() {
		return this.generatorConfiguration.getVoPackageName() + "." + this.getVoSimpleClassName();
	}
	
	public String getVobSimpleClassName() {
		String vofSimpleClassName = this.vofClass.getSimpleName();
		return vofSimpleClassName.substring(0, vofSimpleClassName.length() - 3) + "VOB";
	}
	
	public String getVobCanonicalClassName() {
		return this.generatorConfiguration.getVobPackageName() + "." + this.getVobSimpleClassName();
	}
	
	public String getDaoSimpleClassName() {
		String vofSimpleClassName = this.vofClass.getSimpleName();
		return vofSimpleClassName.substring(0, vofSimpleClassName.length()-3) + "DAO";
	}
	
	public String getDaoCanonicalClassName() {
		return this.generatorConfiguration.getDaoPackageName() + "." + this.getDaoSimpleClassName();
	}
	
	public String getVofSimpleClassName() {
		return this.vofClass.getSimpleName();
	}
	
	public String getPkSimpleClassName() {
		String vofSimpleClassName = this.vofClass.getSimpleName();
		return vofSimpleClassName.substring(0, vofSimpleClassName.length()-3) + "PK";
	}
	
	public String getPkCanonicalClassName() {
		return this.generatorConfiguration.getVoPackageName() + "." + this.getPkSimpleClassName();
	}

	public boolean isAssociationTable() {
		return isAssociationTable;
	}
	
	public void setAssociationTable(boolean isAssociationTable) {
		this.isAssociationTable = isAssociationTable;
	}
	
	public ForeignKeyWrapper getAssociationForeignKeyA() {
		return this.associationForeignKeyA;
	}

	/**
	 * Sets foreignKey as first part of association.
	 */
	public void setAssociationForeignKeyA(ForeignKeyWrapper associationForeignKeyA) {
		this.associationForeignKeyA = associationForeignKeyA;
	}

	/**
	 * Returns foreignKey as second part of association.
	 */
	public ForeignKeyWrapper getAssociationForeignKeyB() {
		return associationForeignKeyB;
	}

	/**
	 * Sets foreignKey as second part of association.
	 */
	public void setAssociationForeignKeyB(ForeignKeyWrapper associationForeignKeyB) {
		this.associationForeignKeyB = associationForeignKeyB;
	}

	public String toString() {
		StringBuilder string = new StringBuilder("Entity vobClassName=" + this.vofClass.getSimpleName() + " tableName=" + tableName);
		string.append("\n\tAttributes:");
		List<Attribute> attributeList = this.getAttributes();
		for (Attribute attribute : attributeList) {
			string.append("\n\t\t").append(attribute.toString());
		}
		
		string.append("\n\tUnique Constraints:");
		Set<String> uniqueKeyNames = this.uniqueConstraints.keySet();
		for (String keyName : uniqueKeyNames) {
			List<Attribute> uniqueAttributes = this.uniqueConstraints.get(keyName);
			string.append("\n\t\tkey name: ").append(keyName);
			for (Attribute attribute : uniqueAttributes) {
				string.append("\n\t\t").append(attribute.toString());
			}
		}
		
		Set<String> foreignKeyNames = this.foreignKeyConstraints.keySet();
		for (String keyName : foreignKeyNames) {
			ForeignKeyWrapper foreignKeyWrapper = this.foreignKeyConstraints.get(keyName);
			string.append("\n\t").append(foreignKeyWrapper.toString());
		}
		
		string.append("\n\tIs Association Table?:").append(this.isAssociationTable);
		if (this.isAssociationTable) {
			string.append("\n\t\tForeignKeyA: ").append(this.associationForeignKeyA.toString());
			string.append("\n\t\tForeignKeyB: ").append(this.associationForeignKeyB.toString());
		}
		
		return string.toString();
	}	

}
