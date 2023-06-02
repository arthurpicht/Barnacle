package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.exceptions.UnknownTypeException;

public class TypeMapperH2 extends TypeMapper {

	protected TypeMapperH2() {
	}

	@Override
	public SqlType getMapping(String javaFieldTypeSimpleName) throws UnknownTypeException {
		switch (javaFieldTypeSimpleName) {
			case "String":
				return SqlType.VARCHAR;
			case "byte":
			case "Byte":
				return SqlType.TINYINT;
			case "short":
			case "Short":
				return SqlType.SMALLINT;
			case "int":
			case "Integer":
				return SqlType.INTEGER;
			case "long":
			case "Long":
				return SqlType.BIGINT;
			case "double":
			case "Double":
				return SqlType.DOUBLE;
			case "boolean":
			case "Boolean":
				return SqlType.TINYINT;
			case "float":
			case "Float":
				return SqlType.DOUBLE;
			case "BigDecimal":
				return SqlType.DECIMAL;
			case "Date":
				return SqlType.DATE;
		}
		throw new UnknownTypeException("Unknown Type: " + javaFieldTypeSimpleName);
	}

}
