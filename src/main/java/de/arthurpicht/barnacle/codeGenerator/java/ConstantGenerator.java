package de.arthurpicht.barnacle.codeGenerator.java;

import java.util.ArrayList;
import java.util.List;

public class ConstantGenerator {
	
	private static class ConstantWrapper {
		public String type = "String";
		public String name = "";
		public String value = "";
	}
	
	private final ClassGenerator parentClassGenerator;
	private final List<ConstantWrapper> constantList;
	
	public ConstantGenerator(ClassGenerator parentClassGenerator) {
		this.parentClassGenerator = parentClassGenerator;
		this.constantList = new ArrayList<>();
	}
	
	public void addStringConstant(Class<?> type, String name, String value) {
		ConstantWrapper constantWrapper = new ConstantWrapper();
		this.parentClassGenerator.getImportGenerator().addImport(type);
		constantWrapper.type = type.getSimpleName();
		constantWrapper.name = name;
		constantWrapper.value = value;
		this.constantList.add(constantWrapper);
	}
	
	public void addStringConstant(String name, String value) {
		ConstantWrapper constantWrapper = new ConstantWrapper();
		constantWrapper.name = name;
		constantWrapper.value = value;
		this.constantList.add(constantWrapper);
	}
	
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
