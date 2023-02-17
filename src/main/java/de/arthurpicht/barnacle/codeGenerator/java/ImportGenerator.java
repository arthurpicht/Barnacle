package de.arthurpicht.barnacle.codeGenerator.java;

import de.arthurpicht.barnacle.helper.Helper;

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
		if (!this.canonicalClassNameList.contains(canonicalClassName)) {
			String packageNameOfGeneratedClass = this.classGenerator.getPackageName();
			String packageNameOfImportClass = Helper.getPackageNameFromCanonicalClassName(canonicalClassName);
			if (!packageNameOfGeneratedClass.equals(packageNameOfImportClass)) {
				this.canonicalClassNameList.add(canonicalClassName);				
			}
		}
	}
	
	public void addImport(Class<?> clazz) {
		String canonicalName = clazz.getCanonicalName();
		addImport(canonicalName);
	}
	
	public void generate(SourceCache sourceCache) {
		for (String canonicalClassName : this.canonicalClassNameList) {
			sourceCache.addLine("import " + canonicalClassName + ";");
		}
		sourceCache.addLine("");
	}

}
