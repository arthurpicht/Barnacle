package de.arthurpicht.barnacle.codeGenerator.java;

import java.util.ArrayList;
import java.util.List;

public class MethodGenerator {
	
	public enum Accessibility {PRIVATE, PUBLIC, PROTECTED};

	private final ClassGenerator parentClassGenerator;
	private Accessibility accessibility;
	private boolean isStatic;
	private String returnTypeSimpleName;
	private final List<String> returnTypeParameters;
	private String methodName;
	private final List<String> parameterTypePlainStringList;
	private final List<String> parameterNameList;
	private final List<String> throwsExceptionSimpleNameList;
	private final List<String> bodyCodeLines;

	private String lineBuffer;
	
	public MethodGenerator(ClassGenerator parentClassGenerator) {
		this.parentClassGenerator = parentClassGenerator;
		this.returnTypeSimpleName = "void";
		this.returnTypeParameters = new ArrayList<>();
		this.methodName = "";
		this.parameterTypePlainStringList = new ArrayList<>();
		this.parameterNameList = new ArrayList<>();
		this.throwsExceptionSimpleNameList = new ArrayList<>();
		this.bodyCodeLines = new ArrayList<>();
		this.accessibility = Accessibility.PUBLIC;
		this.isStatic = false;
		this.lineBuffer = "";
	}
	
	public void setReturnType(Class<?> returnType) {
		this.parentClassGenerator.getImportGenerator().addImport(returnType);
		this.returnTypeSimpleName = returnType.getSimpleName();
	}
	
	public void setReturnTypeByCanonicalClassName(String returnTypeCanonicalClassName) {
		this.parentClassGenerator.getImportGenerator().addImport(returnTypeCanonicalClassName);
		this.returnTypeSimpleName = JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(returnTypeCanonicalClassName);
	}
	
	/**
	 * Sets return type by simple class name. Passed type reference is not
	 * added to import statements. Use this method only in cases of basic data
	 * types and cases that need no automatic adding to import.
	 * 
	 * @param simpleClassName
	 */
	public void setReturnTypeBySimpleClassName(String simpleClassName) {
		this.returnTypeSimpleName = simpleClassName;
	}
	
	public void setReturnTypeParameter(Class<?> returnTypeParameter) {
		this.parentClassGenerator.getImportGenerator().addImport(returnTypeParameter);
		this.returnTypeParameters.add(returnTypeParameter.getSimpleName());
	}
	
	public void setReturnTypeParameter(String typeParameterCanonicalClassName) {
		this.parentClassGenerator.getImportGenerator().addImport(typeParameterCanonicalClassName);
		String typeParameterSimpleClassName = JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(typeParameterCanonicalClassName);
		this.returnTypeParameters.add(typeParameterSimpleClassName);
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public void addParameter(Class<?> parameterType, String parameterName) {
		this.parentClassGenerator.getImportGenerator().addImport(parameterType);
		this.addToParameterLists(parameterType.getSimpleName(), parameterName);
	}
	
	public void addParameter(String parameterCanonicalClassName, String parameterName) {
		this.parentClassGenerator.getImportGenerator().addImport(parameterCanonicalClassName);
		String parameterSimpleClassName = JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(parameterCanonicalClassName);
		this.addToParameterLists(parameterSimpleClassName, parameterName);
	}
	
	public void addParameter(Class<?> parameterType, String parameterParameterCanonicalClassName, String parameterName) {
		this.parentClassGenerator.getImportGenerator().addImport(parameterType);
		this.parentClassGenerator.getImportGenerator().addImport(parameterParameterCanonicalClassName);
		String parameterParameterSimpleClassName
				= JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(parameterParameterCanonicalClassName);
		String parameterTypeString = parameterType.getSimpleName() + "<" + parameterParameterSimpleClassName + ">";
		this.addToParameterLists(parameterTypeString, parameterName);
	}
	
	private void addToParameterLists(String parameterTypeSimpleClassName, String parameterName) {
		this.parameterTypePlainStringList.add(parameterTypeSimpleClassName);
		this.parameterNameList.add(parameterName);
	}
	
	public void addThrowsException(Class<?> exceptionClass) {
		this.parentClassGenerator.getImportGenerator().addImport(exceptionClass);
		this.throwsExceptionSimpleNameList.add(exceptionClass.getSimpleName());
	}

	public void addThrowsException(String exceptionCanonicalClassName) {
		this.parentClassGenerator.getImportGenerator().addImport(exceptionCanonicalClassName);
		this.throwsExceptionSimpleNameList.add(JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(exceptionCanonicalClassName));
	}
	
	public void addImport(String canonicalClassName) {
		this.parentClassGenerator.getImportGenerator().addImport(canonicalClassName);
	}
	
	private boolean hasThrowsException() {
		return this.throwsExceptionSimpleNameList.size() > 0;
	}

	public void addCodeLn(String codeLine) {
		this.bodyCodeLines.add(this.lineBuffer + codeLine);
		this.lineBuffer = "";
	}
	
	public void addCode(String code) {
		this.lineBuffer += code;
	}
	
	public void addLn() {
		this.addCodeLn("");
	}
	
	public void setAccessibility(Accessibility accessibility){
		this.accessibility = accessibility;
	}
	
	public void setIsStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	public void generate(SourceCache sourceCache) {
		// 
		// signature
		//
//		String methodSignatur = new String();
		switch (this.accessibility) {
			case PROTECTED: sourceCache.add("protected");
				break;
			case PRIVATE: sourceCache.add("private");
				break;
			default: sourceCache.add("public");
				break;
		}
		if (this.isStatic) {
			sourceCache.add(" static");
		}
		sourceCache.add(" " + this.returnTypeSimpleName);
		
		if (this.returnTypeParameters.size() > 0) {
			sourceCache.add("<");	
			
			boolean sequence = false;
			for (String typeParameter : this.returnTypeParameters) {
				if (sequence) {
					sourceCache.add(", ");
				}
				sourceCache.add(typeParameter);
				sequence = true;
			}
			
			sourceCache.add(">");
		}
		
		sourceCache.add(" " + this.methodName + "(");
		
		boolean sequence = false;
		for (int i = 0; i<this.parameterTypePlainStringList.size(); i++) {
			if (sequence) {
				sourceCache.add(", ");
			}
			String parameterTypeSimpleName = this.parameterTypePlainStringList.get(i);
			String parameterName = this.parameterNameList.get(i);
			sourceCache.add(parameterTypeSimpleName + " " + parameterName);
			sequence = true;
		}
		
		sourceCache.add(")");
		
		// throws Exceptions
		if (this.hasThrowsException()) {
			sourceCache.add(" throws ");
			sequence = false;
			for (String throwsException : this.throwsExceptionSimpleNameList) {
				if (sequence) {
					sourceCache.add(", ");
				}
				sourceCache.add(throwsException);
				sequence = true;
			}
		}
			
		sourceCache.addLine(" {");
		
		//
		// body
		//
		for (String lineOfCode : this.bodyCodeLines) {
			sourceCache.addLine(lineOfCode);
		}
		
		//
		// closing brace
		//
		sourceCache.addLine("}");
	}
}
