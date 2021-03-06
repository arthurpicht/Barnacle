package de.arthurpicht.barnacle.generator.java;

import de.arthurpicht.barnacle.mapping.Attribute;
import de.arthurpicht.barnacle.mapping.Entity;

import java.util.List;


/**
 * Base class for all value object classes, that means 'value object'
 * classes and 'primary key' classes. Adds specific functionality
 * needed to assemble those types of classes.
 * 
 * @author Arthur Picht, Arthur Picht GmbH, (c) 2007 Arthur Picht GmbH
 *
 */
public class VoBaseGenerator extends ClassGenerator {
	
	protected Entity entity;
	
	public VoBaseGenerator(String canonicalClassName, Entity entity) {
		super(canonicalClassName);
		this.entity = entity;
		this.setBaseClass(entity.getVofClass());
	}

	/**
	 * Creates and adds constants by attributes as used in value 
	 * objects: String constants with field names as constant names
	 * and corresponding column names as constant values. 
	 * 
	 * @param attributeList
	 */
	public void addConstants(List<Attribute> attributeList) {
		ConstantGenerator constantGenerator = this.getConstantGenerator();
		for (Attribute attribute : attributeList) {
			String name = attribute.getFieldName().toUpperCase();
			String value = attribute.getColumnName();
			constantGenerator.addConstant(name, value);
		}
	}
	
	
	/**
	 * Adds a getter method, that returns a primary key value object
	 * corresponding to current value object.
	 */
	protected void addPkGetter() {
		
		String pkCanonicalClassName = this.entity.getPkCanonicalClassName();
		String pkSimpleClassName = this.entity.getPkSimpleClassName();
		String pkVarName = generateVarNameFromSimpleClassName(pkSimpleClassName);
		
		MethodGenerator pkGetterGenerator = this.getNewMethodGenerator();
		pkGetterGenerator.setReturnTypeByCanonicalClassName(pkCanonicalClassName);
		pkGetterGenerator.setMethodName("getPK");
		
		List<Attribute> attributes = this.entity.getPkAttributes();
		
		String lineOfCode = pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(";
		
		boolean sequence = false;
		for (Attribute attribute : attributes) {
			if (sequence) {
				lineOfCode += ", ";
			}
			lineOfCode += "this." + attribute.getFieldName();
			sequence = true;
		}
		
		lineOfCode += ");";
		pkGetterGenerator.addCodeLn(lineOfCode);
		
		pkGetterGenerator.addCodeLn("return " + pkVarName + ";");
	}

	protected void addGetterSetter(List<Attribute> attributes) {
		
		for (Attribute attribute : attributes) {
			addGetter(attribute);			
			addSetter(attribute);			
		}
	}

	protected void addGetter(Attribute attribute) {
		
		MethodGenerator getterGenerator = this.getNewMethodGenerator();
		getterGenerator.setMethodName(attribute.generateGetterMethodName());
		getterGenerator.setReturnTypeBySimpleClassName(attribute.getFieldTypeSimpleName());

		getterGenerator.addCodeLn("return this." + attribute.getFieldName() + ";");		
	}

	protected void addSetter(Attribute attribute) {
		
		MethodGenerator setterGenerator = this.getNewMethodGenerator();
		setterGenerator.setMethodName(attribute.generateSetterMethodName());
		String fieldName = attribute.getFieldName();
		setterGenerator.addParameter(attribute.getFieldTypeSimpleName(), fieldName);
		
		setterGenerator.addCodeLn("this." + fieldName + "=" + fieldName + ";");		
	}

}
