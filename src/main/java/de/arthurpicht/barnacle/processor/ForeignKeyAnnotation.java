package de.arthurpicht.barnacle.processor;

public class ForeignKeyAnnotation {
	
	private String referenceTableName; 
	private String referenceColumnName; 
	private String foreignKeyName;
	private boolean onDeleteCascade = false;
	private boolean onUpdateCascade = false;
	private boolean getEntityMethod = false;
	private boolean setEntityMethod = false;
	private String entityMethodName;
	private boolean getReferenceEntityMethod = false;
	private boolean setReferenceEntityMethod = false;
	private String referenceEntityMethodName;
	
	public ForeignKeyAnnotation() {
		this.referenceTableName = "";
		this.referenceColumnName = "";
		this.foreignKeyName = "";
		this.entityMethodName = "";
		this.referenceEntityMethodName = "";
	}

	public String getEntityMethodName() {
		return entityMethodName;
	}

	public void setEntityMethodName(String entityMethodName) {
		this.entityMethodName = entityMethodName;
	}

	public String getForeignKeyName() {
		return foreignKeyName;
	}

	public void setForeignKeyName(String foreignKeyName) {
		this.foreignKeyName = foreignKeyName;
	}

	public boolean isGetEntityMethod() {
		return getEntityMethod;
	}

	public void setGetEntityMethod(boolean getEntityMethod) {
		this.getEntityMethod = getEntityMethod;
	}

	public boolean isGetReferenceEntityMethod() {
		return getReferenceEntityMethod;
	}

	public void setGetReferenceEntityMethod(boolean getReferenceEntityMethod) {
		this.getReferenceEntityMethod = getReferenceEntityMethod;
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

	public String getReferenceColumnName() {
		return referenceColumnName;
	}

	public void setReferenceColumnName(String referenceColumnName) {
		this.referenceColumnName = referenceColumnName;
	}

	public String getReferenceEntityMethodName() {
		return referenceEntityMethodName;
	}

	public void setReferenceEntityMethodName(String referenceEntityMethodName) {
		this.referenceEntityMethodName = referenceEntityMethodName;
	}

	public String getReferenceTableName() {
		return referenceTableName;
	}

	public void setReferenceTableName(String referenceTableName) {
		this.referenceTableName = referenceTableName;
	}

	public boolean isSetEntityMethod() {
		return setEntityMethod;
	}

	public void setSetEntityMethod(boolean setEntityMethod) {
		this.setEntityMethod = setEntityMethod;
	}

	public boolean isSetReferenceEntityMethod() {
		return setReferenceEntityMethod;
	}

	public void setSetReferenceEntityMethod(boolean setReferenceEntityMethod) {
		this.setReferenceEntityMethod = setReferenceEntityMethod;
	}

}
