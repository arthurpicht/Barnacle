package de.arthurpicht.barnacle.mapping;

import java.util.ArrayList;
import java.util.List;

import de.arthurpicht.barnacle.exceptions.GeneratorException;



public class ForeignKeyWrapper {
	
	private Entity parentEntity;
	private String foreignKeyName;
	private Entity referencedEntity = null;
	private List<Attribute> keyFieldAttributes;
	private List<Attribute> referencedFieldAttributes;
	private boolean onDeleteCascade;
	private boolean onUpdateCascade;
	private boolean getEntityMethod;
	private boolean setEntityMethod;
	private String entityMethodName;
	private boolean getReferencedEntityMethod;
	private boolean setReferencedEntityMethod;
	private String referencedEntityMethodName;
	
	public ForeignKeyWrapper(Entity parentEntity, String foreignKeyName) {
		this.parentEntity = parentEntity;
		this.foreignKeyName = foreignKeyName;
		this.keyFieldAttributes = new ArrayList<Attribute>();
		this.referencedFieldAttributes = new ArrayList<Attribute>();
	}
	
	public Entity getParentEntity() {
		return this.parentEntity;
	}
	
	public String getForeignKeyName() {
		return this.foreignKeyName;
	}
	
	public Entity getTargetEntity() {
		return referencedEntity;
	}

	/**
	 * Sets target entity. If a target entity reference is preexisting,
	 * a check occurs whether the preexisting and passed target entity
	 * reference are consistent. If not, false is given back. In all other
	 * cases true is given back. 
	 * 
	 * @param targetEntity
	 * @return
	 * @throws GeneratorException
	 */
	public boolean setTargetEntity(Entity targetEntity) {
		if (this.referencedEntity == null) {
			this.referencedEntity = targetEntity;
		} else if (!this.referencedEntity.getTableName().equals(targetEntity.getTableName())){
			return false;
		}
		return true;
	}

	public void addFields(Attribute attribute, Attribute targetAttribute) {
		this.keyFieldAttributes.add(attribute);
		this.referencedFieldAttributes.add(targetAttribute);
	}
	
	/**
	 * Returns field attributes defined as part of the represented
	 * foreign key. The order of attributes is equivalent to the order
	 * of the referenced key attributes.
	 * 
	 * @return
	 */
	public List<Attribute> getKeyFieldAttributes() {
		return this.keyFieldAttributes;
	}
	
	public List<Attribute> getTargetFieldAttributes() {
		return this.referencedFieldAttributes;
	}
	
	public void setKeyFieldAttributes(List<Attribute> keyFieldAttributes) {
		this.keyFieldAttributes = keyFieldAttributes;
	}
	
	public void setTargetFieldAttributes(List<Attribute> keyFieldAttributes) {
		this.referencedFieldAttributes = keyFieldAttributes;
	}
	
	public boolean isOnDeleteCascade() {
		return onDeleteCascade;
	}

	public void setOnDeleteCascade(boolean onDeleteCascade) {
		this.onDeleteCascade = onDeleteCascade;
	}

	public boolean isOnUpdateCascade() {
		return onUpdateCascade;
	}

	public void setOnUpdateCascade(boolean onUpdateCascade) {
		this.onUpdateCascade = onUpdateCascade;
	}
	
	public String getEntityMethodName() {
		return entityMethodName;
	}

	public void setEntityMethodName(String entityMethodName) {
		this.entityMethodName = entityMethodName;
	}

	public boolean isGetEntityMethod() {
		return getEntityMethod;
	}

	public void setGetEntityMethod(boolean getEntityMethod) {
		this.getEntityMethod = getEntityMethod;
	}

	public boolean isGetReferenceEntityMethod() {
		return getReferencedEntityMethod;
	}

	public void setGetReferenceEntityMethod(boolean getReferenceEntityMethod) {
		this.getReferencedEntityMethod = getReferenceEntityMethod;
	}

	public String getReferencedEntityMethodName() {
		return referencedEntityMethodName;
	}

	public void setReferencedEntityMethodName(String referenceEntityMethodName) {
		this.referencedEntityMethodName = referenceEntityMethodName;
	}

	public boolean isSetEntityMethod() {
		return setEntityMethod;
	}

	public void setSetEntityMethod(boolean setEntityMethod) {
		this.setEntityMethod = setEntityMethod;
	}

	public boolean isSetReferencedEntityMethod() {
		return setReferencedEntityMethod;
	}

	public void setSetReferencedEntityMethod(boolean setReferenceEntityMethod) {
		this.setReferencedEntityMethod = setReferenceEntityMethod;
	}
	
	public Attribute getKeyFieldAttributeByReferencedFieldAttribute(Attribute referencedFieldAttribute) {
		int i = this.referencedFieldAttributes.indexOf(referencedFieldAttribute);
		if (i<0) return null;
		return this.keyFieldAttributes.get(i);
	}
	
	public Attribute getReferencedFieldAttributeByReferencingFieldAttribute(Attribute keyFieldAttribute) {
		int i = this.keyFieldAttributes.indexOf(keyFieldAttribute);
		if (i<0) return null;
		return this.referencedFieldAttributes.get(i);
	}

	public String toString() {
		String out = "Foreign Key [" + this.foreignKeyName + "] target: " + this.referencedEntity.getVofClass().getSimpleName() 
		+ " onDeleteCascade: " + this.onDeleteCascade + " onUpdateCascade: " + this.onUpdateCascade;		
		for (int i=0; i<this.keyFieldAttributes.size(); i++) {
			out += "\nkey field: " + this.keyFieldAttributes.get(i) + " target field: " + this.referencedFieldAttributes.get(i);
		}
		out += "\ngetEntityMethod: " + this.isGetEntityMethod();
		if (this.isGetEntityMethod()) {
			out += " " + this.getEntityMethodName();
		}
		out += "\nsetEntityMethod: " + this.isSetEntityMethod();
		if (this.isSetEntityMethod()) {
			out += " " + this.getEntityMethodName();
		}
		return out;
	}

}
