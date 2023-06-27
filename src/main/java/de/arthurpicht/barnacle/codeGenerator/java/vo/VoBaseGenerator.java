package de.arthurpicht.barnacle.codeGenerator.java.vo;

import de.arthurpicht.barnacle.codeGenerator.java.ClassGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.ConstantGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;

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
	
	protected final Entity entity;
	
	public VoBaseGenerator(String canonicalClassName, Entity entity, GeneratorConfiguration generatorConfiguration) {
		super(canonicalClassName, generatorConfiguration);
		this.entity = entity;
		this.setBaseClass(entity.getVofClass());
	}

	/**
	 * Creates and adds constants by attributes as used in value 
	 * objects: String constants with field names as constant names
	 * and corresponding column names as constant values. 
	 */
	public void addConstants(List<Attribute> attributeList) {
		ConstantGenerator constantGenerator = this.getConstantGenerator();
		for (Attribute attribute : attributeList) {
			String name = attribute.getFieldName().toUpperCase();
			String value = attribute.getColumnName();
			constantGenerator.addStringConstant(name, value);
		}
	}
	
	
	/**
	 * Adds a getter method, that returns a primary key value object
	 * corresponding to current value object.
	 */
	protected void addPkGetter() {
		String pkCanonicalClassName = this.entity.getPkCanonicalClassName();
		String pkSimpleClassName = this.entity.getPkSimpleClassName();
		String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);
		
		MethodGenerator pkGetterGenerator = this.getNewMethodGenerator();
		pkGetterGenerator.setReturnTypeByCanonicalClassName(pkCanonicalClassName);
		pkGetterGenerator.setMethodName("getPK");
		
		List<Attribute> attributes = this.entity.getPkAttributes();
		
		StringBuilder lineOfCode = new StringBuilder(pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(");
		
		boolean sequence = false;
		for (Attribute attribute : attributes) {
			if (sequence) {
				lineOfCode.append(", ");
			}
			lineOfCode.append("this.").append(attribute.getFieldName());
			sequence = true;
		}
		
		lineOfCode.append(");");
		pkGetterGenerator.addCodeLn(lineOfCode.toString());
		
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
		getterGenerator.setReturnTypeBySimpleClassName(attribute.getJavaTypeSimpleName());
		getterGenerator.addCodeLn("return this." + attribute.getFieldName() + ";");
	}

	protected void addSetter(Attribute attribute) {
		MethodGenerator setterGenerator = this.getNewMethodGenerator();
		setterGenerator.setMethodName(attribute.generateSetterMethodName());
		String fieldName = attribute.getFieldName();
		setterGenerator.addParameter(attribute.getType(), fieldName);
		setterGenerator.addCodeLn("this." + fieldName + "=" + fieldName + ";");
	}

}
