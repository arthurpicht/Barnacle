package de.arthurpicht.barnacle.generator.java;

import de.arthurpicht.barnacle.helper.Helper;

import java.util.ArrayList;
import java.util.List;


/**
 * Wrapps and generates import statements.
 * 
 * @author Picht
 *
 */
@SuppressWarnings("rawtypes")
public class ImportGenerator {
	
	private ClassGenerator parentClassGenerator;
	private List<String> canonicalClassNameList;
	
	public ImportGenerator(ClassGenerator parentClassGenerator) {
		this.parentClassGenerator = parentClassGenerator;
		this.canonicalClassNameList = new ArrayList<String>();
	}
	
	/**
	 * Adds import by canonical class name.
	 * 
	 * @param canonicalClassName
	 */
	public void addImport(String canonicalClassName) {
		// add it just one time
		if (!this.canonicalClassNameList.contains(canonicalClassName)) {
			String packageNameParentClass = this.parentClassGenerator.getPackageName();
			String packageNameImportClass = Helper.getPackageNameFromCanonicalClassName(canonicalClassName);
			// add it just in case class belongs to OTHER package
			if (!packageNameParentClass.equals(packageNameImportClass)) {
				this.canonicalClassNameList.add(canonicalClassName);				
			}
		}
	}
	
	/**
	 * Adds import by class.
	 * 
	 * @param clazz
	 */
	public void addImport(Class clazz) {
		String canonicalName = clazz.getCanonicalName();
		addImport(canonicalName);
	}
	
	/**
	 * Generates import statements. Code will be written to passed
	 * source cache.
	 * 
	 * @param sourceCache
	 */
	public void generate(SourceCache sourceCache) {
		for (String canonicalClassName : this.canonicalClassNameList) {
			sourceCache.addLine("import " + canonicalClassName + ";");
		}
		sourceCache.addLine("");
	}

}
