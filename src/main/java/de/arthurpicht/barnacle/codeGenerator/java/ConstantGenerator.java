package de.arthurpicht.barnacle.codeGenerator.java;

import java.util.ArrayList;
import java.util.List;

public class ConstantGenerator {
	
	private static class ConstantWrapper {
		public String accessModifier = "public";
		public String type = "";
		public String name = "";
		public String value = "";
		public boolean extraLine = false;
	}

	private final List<ConstantWrapper> constantList;
	
	public ConstantGenerator() {
		this.constantList = new ArrayList<>();
	}

	public void addStringConstant(String name, String value) {
		ConstantWrapper constantWrapper = new ConstantWrapper();
		constantWrapper.type = "String";
		constantWrapper.name = name;
		constantWrapper.value = "\"" + value + "\"";
		this.constantList.add(constantWrapper);
	}

	public void addPrivateLongConstant(String name, long value) {
		ConstantWrapper constantWrapper = new ConstantWrapper();
		constantWrapper.accessModifier = "private";
		constantWrapper.name = name;
		constantWrapper.type = "long";
		constantWrapper.value = value + "L";
		constantWrapper.extraLine = true;
		this.constantList.add(constantWrapper);
	}
	
	public void generate(SourceCache sourceCache) {
		for (ConstantWrapper constantWrapper : this.constantList) {
			sourceCache.addLine(
					constantWrapper.accessModifier + " static final "
					+ constantWrapper.type + " " 
					+ constantWrapper.name 
					+ " = "
					+ constantWrapper.value
					+ ";");
			if (constantWrapper.extraLine)
				sourceCache.addLine();
		}
		if (this.constantList.size() > 0) {
			sourceCache.addLine("");
		}
	}

}
