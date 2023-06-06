package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.utils.core.strings.Strings;

public class SimpleTypeHelper {
    
    private static final String[] simpleTypes = {"byte", "short", "int", "long", "double", "boolean", "float"};
    
    public static boolean isNoSimpleType(String type) {
        return !Strings.isOneOf(type, simpleTypes);
    }

}
