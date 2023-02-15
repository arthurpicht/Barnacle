package de.arthurpicht.barnacle.generator.sql;

import de.arthurpicht.barnacle.exceptions.UnknownTypeException;

public class TypeMapperH2 extends TypeMapper {

	/**
	 * Do not call constructor manually! Use getInstance method of
	 * superclass instead!
	 *
	 */
	public TypeMapperH2() {
	}

	public String getSqlType(String fieldType) throws UnknownTypeException {

	    // TODO Parameter ändern: canonicalName

		if (fieldType.equals("String")) {
			return "VARCHAR(255)";
		} else if (fieldType.equals("byte") || (fieldType.equals("Byte"))) {
			return "TINYINT";
		} else if (fieldType.equals("short") || (fieldType.equals("Short"))) {
			return "SMALLINT";
		} else if (fieldType.equals("int") || (fieldType.equals("Integer"))) {
			// return "INT(11)";
			// return "INT";
			return "INTEGER";
		} else if (fieldType.equals("long") || (fieldType.equals("Long"))) {
			return "BIGINT";
		} else if (fieldType.equals("double") || (fieldType.equals("Double"))) {
			return "DOUBLE";
		} else if (fieldType.equals("boolean") || (fieldType.equals("Boolean"))) {
			return "TINYINT";
		} else if (fieldType.equals("float") || (fieldType.equals("Float"))) {
			return "DOUBLE";
		} else if (fieldType.equals("BigDecimal")) {
			return "DECIMAL(10,2)";
		} else if (fieldType.equals("Date")) {
			return "DATE";
		}
		
		throw new UnknownTypeException("Unknown Type: " + fieldType);
		
	}

}
