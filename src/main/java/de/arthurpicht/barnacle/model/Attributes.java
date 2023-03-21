package de.arthurpicht.barnacle.model;

import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class Attributes {

    public static List<String> getColumnNames(List<Attribute> attributes) {
        return attributes.stream()
                .map(Attribute::getColumnName)
                .collect(Collectors.toList());
    }

    public static List<String> getFieldNames(List<Attribute> attributes) {
        return attributes.stream()
                .map(Attribute::getFieldName)
                .collect(Collectors.toList());
    }

}
