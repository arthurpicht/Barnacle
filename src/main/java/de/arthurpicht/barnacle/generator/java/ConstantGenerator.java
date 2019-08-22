package de.arthurpicht.barnacle.generator.java;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates constant definitions.
 * 
 * @author Picht
 *
 */
@SuppressWarnings("rawtypes")
public class ConstantGenerator {
	
	private class ConstantWrapper {
		public String type = new String("String");
		public String name = new String();
		public String value = new String();
	}
	
	private ClassGenerator parentClassGenerator;
	private List<ConstantWrapper> constantList;
	
	public ConstantGenerator(ClassGenerator parentClassGenerator) {
		this.parentClassGenerator = parentClassGenerator;
		this.constantList = new ArrayList<ConstantWrapper>();
	}
	
	/**
	 * Adds constant to constant wrapper.
	 * 
	 * @param type
	 * @param name
	 * @param value
	 */
	public void addConstant(Class type, String name, String value) {
		ConstantWrapper constantWrapper = new ConstantWrapper();
		this.parentClassGenerator.getImportGenerator().addImport(type);
		constantWrapper.type = type.getSimpleName();
		constantWrapper.name = name;
		constantWrapper.value = value;
		this.constantList.add(constantWrapper);
	}
	
	/**
	 * Adds constant to constant wrapper. Type keeps default String.
	 * 
	 * @param name
	 * @param value
	 */
	public void addConstant(String name, String value) {
		ConstantWrapper constantWrapper = new ConstantWrapper();
		constantWrapper.name = name;
		constantWrapper.value = value;
		this.constantList.add(constantWrapper);
	}
	
	/**
	 * Generates constant definitions to passed souce cache.
	 * 
	 * @param sourceCache
	 */
	public void generate(SourceCache sourceCache) {
		for (ConstantWrapper constantWrapper : this.constantList) {
			sourceCache.addLine("public static final " 
					+ constantWrapper.type + " " 
					+ constantWrapper.name 
					+ " = \"" 
					+ constantWrapper.value + "\";");
		}
		if (this.constantList.size() > 0) {
			sourceCache.addLine("");
		}
	}

}
