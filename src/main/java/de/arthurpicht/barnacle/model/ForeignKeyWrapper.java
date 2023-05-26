package de.arthurpicht.barnacle.model;

import java.util.ArrayList;
import java.util.List;


public class ForeignKeyWrapper {
	
	private final Entity parentEntity;
	private final String foreignKeyName;
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
		this.keyFieldAttributes = new ArrayList<>();
		this.referencedFieldAttributes = new ArrayList<>();
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

	public boolean setTargetEntity(Entity targetEntity) {
		if (this.referencedEntity == null) {
			this.referencedEntity = targetEntity;
			return true;
		} else {
			return this.referencedEntity.getTableName().equals(targetEntity.getTableName());
		}
	}

	public void addFields(Attribute attribute, Attribute targetAttribute) {
		this.keyFieldAttributes.add(attribute);
		this.referencedFieldAttributes.add(targetAttribute);
	}
	
	/**
	 * Returns field attributes defined as part of the represented
	 * foreign key. The order of attributes is equivalent to the order
	 * of the referenced key attributes.
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
		StringBuilder out = new StringBuilder(
				"Foreign Key [" + this.foreignKeyName + "] target: "
						+ this.referencedEntity.getVofClass().getSimpleName()
						+ " onDeleteCascade: " + this.onDeleteCascade + " onUpdateCascade: " + this.onUpdateCascade);
		for (int i = 0; i < this.keyFieldAttributes.size(); i++) {
			out.append("\nkey field: ").append(this.keyFieldAttributes.get(i)).append(" target field: ")
					.append(this.referencedFieldAttributes.get(i));
		}
		out.append("\ngetEntityMethod: ").append(this.isGetEntityMethod());
		if (this.isGetEntityMethod()) {
			out.append(" ").append(this.getEntityMethodName());
		}
		out.append("\nsetEntityMethod: ").append(this.isSetEntityMethod());
		if (this.isSetEntityMethod()) {
			out.append(" ").append(this.getEntityMethodName());
		}
		return out.toString();
	}

}
