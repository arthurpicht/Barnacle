package de.arthurpicht.barnacle.codeGenerator.java;

import de.arthurpicht.utils.core.strings.Strings;

import java.util.ArrayList;
import java.util.List;

public class ImportGenerator {
	
	private final ClassGenerator classGenerator;
	private final List<String> canonicalClassNameList;
	
	public ImportGenerator(ClassGenerator classGenerator) {
		this.classGenerator = classGenerator;
		this.canonicalClassNameList = new ArrayList<>();
	}
	
	public void addImport(String canonicalClassName) {
		if (isImportedByDefault(canonicalClassName)) return;
		if (isPrimitiveType(canonicalClassName)) return;
		if (isWrapperClassSimpleName(canonicalClassName)) return;
		if (isStringSimpleName(canonicalClassName)) return;
		if (!this.canonicalClassNameList.contains(canonicalClassName)) {
			String packageNameOfGeneratedClass = this.classGenerator.getPackageName();
			String packageNameOfImportClass = JavaGeneratorHelper.getPackageNameFromCanonicalClassName(canonicalClassName);
			if (packageNameOfGeneratedClass.equals(packageNameOfImportClass)) return;
			this.canonicalClassNameList.add(canonicalClassName);
		}
	}
	
	public void addImport(Class<?> clazz) {
		if (isImportedByDefault(classGenerator.canonicalClassName)) return;

		String canonicalName = clazz.getCanonicalName();
		addImport(canonicalName);
	}
	
	public void generate(SourceCache sourceCache) {
		for (String canonicalClassName : this.canonicalClassNameList) {
			sourceCache.addLine("import " + canonicalClassName + ";");
		}
		sourceCache.addLine("");
	}

	private boolean isImportedByDefault(String canonicalClassName) {
		return canonicalClassName.startsWith("java.lang.");
	}

	private boolean isPrimitiveType(String name) {
		return Strings.isOneOf(name, "boolean", "char", "byte", "short", "int", "long", "float", "double");
	}

	private boolean isWrapperClassSimpleName(String name) {
		return Strings.isOneOf(name, Boolean.class.getSimpleName(), Character.class.getSimpleName(),
				Byte.class.getSimpleName(), Short.class.getSimpleName(), Integer.class.getSimpleName(),
				Long.class.getSimpleName(), Float.class.getSimpleName(), Double.class.getSimpleName());
	}

	private boolean isStringSimpleName(String name) {
		return name.equals(String.class.getSimpleName());
	}

}
