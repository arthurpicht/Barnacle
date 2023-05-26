package de.arthurpicht.barnacle.codeGenerator.java;

import de.arthurpicht.barnacle.model.Attribute;

import java.util.ArrayList;
import java.util.List;

public class VoConstructorGenerator extends ConstructorGenerator {
	
	private final String simpleClassName;
	private final List<String> paraTypeName;
	private final List<String> paraName;
	private final List<String> member;
	private final List<String> assignment;
	
	public VoConstructorGenerator(String simpleClassName) {
		this.simpleClassName = simpleClassName;
		this.paraTypeName = new ArrayList<>();
		this.paraName = new ArrayList<>();
		this.member = new ArrayList<>();
		this.assignment = new ArrayList<>();
	}
	
	public void defineParametersAndAssignmentsByAttributes(List<Attribute> attributeList) {
		for (Attribute attribute : attributeList) {
			this.paraTypeName.add(attribute.getJavaTypeSimpleName());
			this.paraName.add(attribute.getFieldName());
			this.member.add("this." + attribute.getFieldName());
			this.assignment.add(attribute.getFieldName());
		}
	}
	
	public void defineParameter(String paraTypeName, String paraName) {
		this.paraTypeName.add(paraTypeName);
		this.paraName.add(paraName);
	}
	
	public void defineAssignment(String member, String assignment) {
		this.member.add(member);
		this.assignment.add(assignment);
	}
	
	public void generate(SourceCache sourceCache) {
		generateSignature(sourceCache);
		generateAssignment(sourceCache);
	}

	private void generateSignature(SourceCache sourceCache) {
		StringBuilder signature = new StringBuilder("public " + this.simpleClassName + "(");

		boolean sequence = false;
		for (int i=0; i<this.paraTypeName.size(); i++) {
			String paraTypeName = this.paraTypeName.get(i);
			String paraName = this.paraName.get(i);
			if (sequence) {
				signature.append(", ");
			}
			signature.append(paraTypeName).append(" ").append(paraName);
			sequence = true;
		}

		signature.append(") {");
		sourceCache.addLine(signature.toString());
	}

	private void generateAssignment(SourceCache sourceCache) {
		for (int i=0; i<this.member.size(); i++) {
			String fieldName = this.member.get(i);
			String assignment = this.assignment.get(i);
			sourceCache.addLine(fieldName + "=" + assignment + ";");
		}

		sourceCache.addLine("}");
		sourceCache.addLine("");
	}

}
