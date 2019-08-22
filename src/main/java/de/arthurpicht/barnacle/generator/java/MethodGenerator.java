package de.arthurpicht.barnacle.generator.java;

import de.arthurpicht.barnacle.helper.Helper;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class MethodGenerator {
	
	public enum Accessibility {PRIVATE, PUBLIC, PROTECTED};

	private ClassGenerator parentClassGenerator;
	private String returnTypeSimpleName;
	private List<String> returnTypeParameters;
	private String methodName;
	private List<String> parameterTypeSimpleNameList;
	private List<String> parameterNameList;
	private List<String> throwsExceptionSimpleNameList;
	private List<String> bodyCodeLines;
	private Accessibility accessibility;
	private boolean isStatic;
	
	private String lineBuffer;
	
	public MethodGenerator(ClassGenerator parentClassGenerator) {
		this.parentClassGenerator = parentClassGenerator;
		this.returnTypeSimpleName = "void";
		this.returnTypeParameters = new ArrayList<String>();
		this.methodName = new String();
		this.parameterTypeSimpleNameList = new ArrayList<String>();
		this.parameterNameList = new ArrayList<String>();
		this.throwsExceptionSimpleNameList = new ArrayList<String>();
		this.bodyCodeLines = new ArrayList<String>();
		this.accessibility = Accessibility.PUBLIC;
		this.isStatic = false;
		this.lineBuffer = new String();
	}
	
	/**
	 * Sets return type. Adds type to import.
	 * @param returnType
	 */
	public void setReturnType(Class returnType) {
		this.parentClassGenerator.getImportGenerator().addImport(returnType);
		this.returnTypeSimpleName = returnType.getSimpleName();
	}
	
	/**
	 * Sets return type by canonical class name. Adds class name to import.
	 * 
	 * @param returnTypeCanonicalClassName
	 */
	public void setReturnTypeByCanonicalClassName(String returnTypeCanonicalClassName) {
		this.parentClassGenerator.getImportGenerator().addImport(returnTypeCanonicalClassName);
		this.returnTypeSimpleName = Helper.getSimpleClassNameFromCanonicalClassName(returnTypeCanonicalClassName);
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
	
	/**
	 * Adds a class to the list of return type parameters. Passed
	 * class is also added to import statements.
	 * 
	 * @param returnTypeParameter
	 */
	public void setReturnTypeParameter(Class returnTypeParameter) {
		this.parentClassGenerator.getImportGenerator().addImport(returnTypeParameter);
		this.returnTypeParameters.add(returnTypeParameter.getSimpleName());
	}
	
	/**
	 * Adds a class to the list of return type parameters. Passed
	 * canonical class name is also added to import statements.
	 * 
	 * @param typeParameterCanonicalClassName
	 */
	public void setReturnTypeParameter(String typeParameterCanonicalClassName) {
		this.parentClassGenerator.getImportGenerator().addImport(typeParameterCanonicalClassName);
		String typeParameterSimpleClassName = Helper.getSimpleClassNameFromCanonicalClassName(typeParameterCanonicalClassName);
		this.returnTypeParameters.add(typeParameterSimpleClassName);
	}
	
	/**
	 * Sets name of method.
	 * 
	 * @param methodName
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	/**
	 * Adds pair of parameter and name to method signature definition.
	 * Parameter type is added to import statements.
	 * 
	 * @param parameterType
	 * @param parameterName
	 */
	public void addAndImportParameter(Class parameterType, String parameterName) {
		this.parentClassGenerator.getImportGenerator().addImport(parameterType);
		this.addParameter(parameterType.getSimpleName(), parameterName);
	}
	
	/**
	 * Adds pair of parameter and name to method signature definition.
	 * Parameter type is added to import statements.
	 * 
	 * @param parameterCanonicalClassName
	 * @param parameterName
	 */
	public void addAndImportParameter(String parameterCanonicalClassName, String parameterName) {
		this.parentClassGenerator.getImportGenerator().addImport(parameterCanonicalClassName);
		String parameterSimpleClassName = Helper.getSimpleClassNameFromCanonicalClassName(parameterCanonicalClassName);
		this.addParameter(parameterSimpleClassName, parameterName);
	}
	
	/**
	 * Adds pair of parameter and name to method signature definition.
	 * Parameter itself is class parameterized. Applicable especially when parameterized collection class
	 * is used.
	 * Parameter type is added to import statement.
	 * 
	 * @param parameterType
	 * @param classParameterCanonicalClassName
	 * @param parameterName
	 */
	public void addAndImportParameter(Class parameterType, String classParameterCanonicalClassName, String parameterName) {
		this.parentClassGenerator.getImportGenerator().addImport(parameterType);
		this.parentClassGenerator.getImportGenerator().addImport(classParameterCanonicalClassName);
		String classParameterSimpleClassName = Helper.getSimpleClassNameFromCanonicalClassName(classParameterCanonicalClassName);
		String parameterTypeString = parameterType.getSimpleName() + "<" + classParameterSimpleClassName + ">";
		this.addParameter(parameterTypeString, parameterName);
	}
	
	/**
	 * Adds pair of parameter and name to method signature definition.
	 * Parameter type is NOT added to import statements.
	 * 
	 * @param parameterType type of parameter, given as simple class name or basis data type
	 * @param parameterName name of parameter
	 */
	public void addParameter(String parameterType, String parameterName) {
		this.parameterTypeSimpleNameList.add(parameterType);
		this.parameterNameList.add(parameterName);
	}
	
	public void addThrowsException(Class exceptionClass) {
		this.parentClassGenerator.getImportGenerator().addImport(exceptionClass);
		this.throwsExceptionSimpleNameList.add(exceptionClass.getSimpleName());
	}

	public void addThrowsException(String exceptionCanonicalClassName) {
		this.parentClassGenerator.getImportGenerator().addImport(exceptionCanonicalClassName);
		this.throwsExceptionSimpleNameList.add(Helper.getSimpleClassNameFromCanonicalClassName(exceptionCanonicalClassName));
	}
	
	/**
	 * Adds given class to import statements.
	 * 
	 * @param canonicalClassName
	 */
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
		for (int i=0; i<this.parameterTypeSimpleNameList.size(); i++) {
			if (sequence) {
				sourceCache.add(", ");
			}
			String parameterTypeSimpleName = this.parameterTypeSimpleNameList.get(i);
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
