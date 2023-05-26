package de.arthurpicht.barnacle.codeGenerator.java;

import de.arthurpicht.barnacle.model.Entity;

public class JavaGeneratorHelper {

	public static String getPackageNameFromCanonicalClassName(String canonicalClassName) {
		int lastSeparatorIndex = canonicalClassName.lastIndexOf('.');
		if (lastSeparatorIndex < 0) return "";
		return canonicalClassName.substring(0, lastSeparatorIndex);
	}
	
	public static String getSimpleClassNameFromCanonicalClassName(String canonicalClassName) {
		int lastSeparatorIndex = canonicalClassName.lastIndexOf('.');
		if (lastSeparatorIndex < 0) return canonicalClassName;
		return canonicalClassName.substring(lastSeparatorIndex + 1);
	}

	public static String getVarNameFromSimpleClassName(String simpleClassName) {
		return simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1);
	}

	public static String getVoVarName(Entity entity) {
		String voSimpleClassName = entity.getVoSimpleClassName();
		return getVarNameFromSimpleClassName(voSimpleClassName);
	}

	public static String getVoVarListName(Entity entity) {
		return getVoVarName(entity) + "s";
	}

	public static String getPkVarName(Entity entity) {
		String pkSimpleClassName = entity.getPkSimpleClassName();
		return getVarNameFromSimpleClassName(pkSimpleClassName);
	}

}
