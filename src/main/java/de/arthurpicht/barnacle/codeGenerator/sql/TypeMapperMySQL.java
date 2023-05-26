package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.exceptions.UnknownTypeException;

public class TypeMapperMySQL extends TypeMapper {
	
	protected TypeMapperMySQL() {
	}

	public String getSqlType(String fieldType) throws UnknownTypeException {
	    // TODO Parameter ändern: canonicalName
		switch (fieldType) {
			case "String":
				return "VARCHAR(255)";
			case "byte":
			case "Byte":
				return "TINYINT";
			case "short":
			case "Short":
				return "SMALLINT";
			case "int":
			case "Integer":
				return "INTEGER";
			case "long":
			case "Long":
				return "BIGINT";
			case "double":
			case "Double":
				return "DOUBLE";
			case "boolean":
			case "Boolean":
				return "TINYINT(1)";
			case "float":
			case "Float":
				return "DOUBLE";
			case "BigDecimal":
				return "DECIMAL(10,2)";
			case "Date":
				return "DATE";
		}
		throw new UnknownTypeException("Unknown Type: " + fieldType);
	}

}
