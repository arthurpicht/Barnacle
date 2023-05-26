package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.exceptions.UnknownTypeException;

public class TypeMapperMySQL extends TypeMapper {
	
	protected TypeMapperMySQL() {
	}

	@Override
	public SqlType getSqlType(String fieldType) throws UnknownTypeException {
		switch (fieldType) {
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
				return SqlType.TINYINT1;
			case "float":
			case "Float":
				return SqlType.DOUBLE;
			case "BigDecimal":
				return SqlType.DECIMAL;
			case "Date":
				return SqlType.DATE;
		}
		throw new UnknownTypeException("Unknown Type: " + fieldType);
	}

//
//
//	@Override
//	public String getSqlType(String fieldType) throws UnknownTypeException {
//	    // TODO Parameter ändern: canonicalName
//		switch (fieldType) {
//			case "String":
//				return "VARCHAR(255)";
//			case "byte":
//			case "Byte":
//				return "TINYINT";
//			case "short":
//			case "Short":
//				return "SMALLINT";
//			case "int":
//			case "Integer":
//				return "INTEGER";
//			case "long":
//			case "Long":
//				return "BIGINT";
//			case "double":
//			case "Double":
//				return "DOUBLE";
//			case "boolean":
//			case "Boolean":
//				return "TINYINT(1)";
//			case "float":
//			case "Float":
//				return "DOUBLE";
//			case "BigDecimal":
//				return "DECIMAL(10,2)";
//			case "Date":
//				return "DATE";
//		}
//		throw new UnknownTypeException("Unknown Type: " + fieldType);
//	}

}
