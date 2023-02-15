package de.arthurpicht.barnacle.generator.java;

import de.arthurpicht.barnacle.mapping.Attribute;

import java.util.ArrayList;
import java.util.List;


/**
 * Constructor Generator.
 * 
 * @author Arthur Picht, Arthur Picht GmbH, (c) 2007
 *
 */
public class VoConstructorGenerator extends ConstructorGenerator {
	
	private String simpleClassName = null;
	private List<String> paraTypeName;
	private List<String> paraName;
	private List<String> member;
	private List<String> assignment;
	
	/**
	 * 
	 * @param simpleClassName destination class
	 */
	public VoConstructorGenerator(String simpleClassName) {
		this.simpleClassName = simpleClassName;
		this.paraTypeName = new ArrayList<String>();
		this.paraName = new ArrayList<String>();
		this.member = new ArrayList<String>();
		this.assignment = new ArrayList<String>();
	}
	
	/**
	 * Defines constructor parameters and assignment by passed 
	 * attributes. Every attribute is percepted as a member of 
	 * the corresponding class.
	 * For every attribute a parameter and its assignment to
	 * class member is generated.
	 * 
	 * @param attributeList
	 */
	public void defineParametersAndAssignmentsByAttributes(List<Attribute> attributeList) {		
		for (Attribute attribute : attributeList) {
			this.paraTypeName.add(attribute.getJavaTypeSimpleName());
			this.paraName.add(attribute.getFieldName());
			this.member.add("this." + attribute.getFieldName());
			this.assignment.add(attribute.getFieldName());
		}
	}
	
	/**
	 * Defines a constructor parameter.
	 * 
	 * @param paraTypeName
	 * @param paraName
	 */
	public void defineParameter(String paraTypeName, String paraName) {
		this.paraTypeName.add(paraTypeName);
		this.paraName.add(paraName);
	}
	
	/**
	 * Defines an assignment operation.
	 * 
	 * @param member Part of assignment operation to the left of '='.
	 * @param assignment Part of assignment operation to the right of '='.
	 */
	public void defineAssignment(String member, String assignment) {
		this.member.add(member);
		this.assignment.add(assignment);
	}
	
	/**
	 * Generates consturctor.
	 * 
	 * @param printWriter
	 */
	public void generate(SourceCache sourceCache) {
		
		// Generate Signature		
		String signature = "";
		signature =  "public " + this.simpleClassName + "(";
		
		boolean sequence = false;
		for (int i=0; i<this.paraTypeName.size(); i++) {
			String paraTypeName = this.paraTypeName.get(i);
			String paraName = this.paraName.get(i);
			if (sequence) {
				signature += ", ";
			}
			signature += paraTypeName + " " + paraName;
			sequence = true;
		}
		
		signature += ") {";
		sourceCache.addLine(signature);
		
		// generate Assignment		
		for (int i=0; i<this.member.size(); i++) {
			String fieldName = this.member.get(i);
			String assignment = this.assignment.get(i);
			sourceCache.addLine(fieldName + "=" + assignment + ";");
		}
		
		sourceCache.addLine("}");
		sourceCache.addLine("");
	}

}
