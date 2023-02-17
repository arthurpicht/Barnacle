package de.arthurpicht.barnacle.codeGenerator.java;

import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.codeGenerator.CodeGeneratorException;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;

import java.util.List;


/**
 * Extends VoBaseGenerator with special functionality to
 * define and generate primary key value classes 
 * ('pk' classes).
 * 
 * @author Arthur Picht, Arthur Picht GmbH, (c) 2007 Arthur Picht GmbH
 *
 */
public class PkGenerator extends VoBaseGenerator {
	
	public PkGenerator(Entity entity) throws CodeGeneratorException {
		
		super(getPKcanonicalClassNameFromEntity(entity), entity);

		this.addImportsForNonPrimitiveAttributes();

		// constants
		List<Attribute> attributes = entity.getPkAttributes();
		this.addConstants(attributes);
		
		// constructor
		this.addConstructor();
		
		// getter & setter methods
		List<Attribute> pkAttributes = entity.getPkAttributes();
		this.addGetterSetter(pkAttributes);
		
		// toString method
		this.addToStringMethod();
		
		// equals method
		this.addEqualsMethod();

	}

	private void addImportsForNonPrimitiveAttributes() {

		List<Attribute> attributeList = this.entity.getPkAttributes();
		for (Attribute attribute : attributeList) {
			if (!attribute.isPrimitiveType()) {
				this.getImportGenerator().addImport(attribute.getJavaTypeCanonicalName());
			}
		}
	}

	/**
	 * Adds constructor for primary key class: all pk attributes
	 * as parameters.
	 * 
	 * @throws CodeGeneratorException
	 */
	private void addConstructor() throws CodeGeneratorException {
		
		List<Attribute> attributes = this.entity.getPkAttributes();
		
		VoConstructorGenerator constructorGenerator = this.getVOConstructorGenerator();
		constructorGenerator.defineParametersAndAssignmentsByAttributes(attributes);		
	}

	/**
	 * Determines canonical class name for pk class from entity. 
	 * 
	 * @param entity
	 * @return
	 */
	private static String getPKcanonicalClassNameFromEntity(Entity entity) {		
		String vofClassName = entity.getVofClass().getSimpleName();
		String voSimpleClassName = vofClassName.substring(0, vofClassName.length()-3) + "PK";
		String voCanonicalClassName = GeneratorContext.getInstance().getGeneratorConfiguration().getVoPackageName() + "." + voSimpleClassName;
		return voCanonicalClassName;
	}
	
	private void addToStringMethod() {

		MethodGenerator methodGenerator = this.getNewMethodGenerator();
		methodGenerator.setMethodName("toString");
		methodGenerator.setReturnType(String.class);
		
		methodGenerator.addCodeLn("return \"" + this.entity.getPkCanonicalClassName() + "[\"");
		
		List<Attribute> attributeList = this.entity.getPkAttributes();
		boolean sequence = false;
		for (Attribute attribute : attributeList) {
			if (sequence) {
				methodGenerator.addCodeLn(" + \"; \"");
			}
			methodGenerator.addCode("+ \"" + attribute.getFieldName() + "=\" + this." + attribute.getFieldName());
			sequence = true;
		}
		methodGenerator.addCodeLn("");
		methodGenerator.addCodeLn("+ \"]\";");
		
	}
	
	private void addEqualsMethod() {
		
		MethodGenerator methodGenerator = this.getNewMethodGenerator();
		methodGenerator.setMethodName("equals");
		methodGenerator.setReturnTypeBySimpleClassName("boolean");
		methodGenerator.addParameter("Object", "o");
		
		methodGenerator.addCodeLn("if (o == null) return false;");
		methodGenerator.addCodeLn("if (o == this) return true;");
		methodGenerator.addCodeLn("if (o.getClass() != this.getClass()) return false;");
		
		String pkSimpleClassName = this.entity.getPkSimpleClassName();
		methodGenerator.addCodeLn(pkSimpleClassName + " other = (" + pkSimpleClassName + ") o;");
		
		List<Attribute> attributeList = this.entity.getPkAttributes();
		for (Attribute attribute : attributeList) {
			if (attribute.isPrimitiveType()) {
				methodGenerator.addCodeLn("if (this." + attribute.getFieldName() + " != other." + attribute.generateGetterMethodName() + "()) return false;");
			} else {
				methodGenerator.addCodeLn("if (this." + attribute.getFieldName() + " == null) {");
				methodGenerator.addCodeLn("if (other." + attribute.generateGetterMethodName() + "() != null) return false;");
				methodGenerator.addCodeLn("} else {");
				methodGenerator.addCodeLn("if (!this." + attribute.getFieldName() + ".equals(other." + attribute.generateGetterMethodName() + "())) return false;");
				methodGenerator.addCodeLn("}");
			}
		}
		methodGenerator.addCodeLn("return true;");
	}


}
