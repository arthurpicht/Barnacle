package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;
import de.arthurpicht.barnacle.exceptions.UnknownTypeException;
import de.arthurpicht.barnacle.model.Attribute;

public abstract class TypeMapper {
	
	public static TypeMapper getInstance(Const.Dialect dialect) {
		if (dialect == Const.Dialect.MYSQL) {
			return new TypeMapperMySQL();
		} else if (dialect == Const.Dialect.H2) {
			return new TypeMapperH2();
		}
		throw new BarnacleRuntimeException("Impossible type mapper requested!");
	}
	
	public String getSQLType(Attribute attribute) throws UnknownTypeException {
		String sqlTypeString;
		if (attribute.hasCustomType()) {
			sqlTypeString = attribute.getCustomType();
		} else {
			SqlType sqlType = this.getSqlType(attribute.getJavaTypeSimpleName());
			sqlTypeString = sqlType.getSqlTypeString();
		}
		return sqlTypeString;
	}

	public String getSQLTypeLiteral(Attribute attribute) throws UnknownTypeException {
		String sqlTypeString;
		if (attribute.hasCustomType()) {
			// TODO derive type literal from custom type annotation
			sqlTypeString = attribute.getCustomType();
		} else {
			SqlType sqlType = this.getSqlType(attribute.getJavaTypeSimpleName());
			sqlTypeString = sqlType.getTypesLiteral();
		}
		return sqlTypeString;
	}
	
	protected abstract SqlType getSqlType(String fieldType) throws UnknownTypeException;

//	public static String getPreparedStatementSetMethod(String fieldType) {
//
//		switch (fieldType) {
//			case "String":
//				return "setString";
//			case "byte":
//			case "Byte":
//				return "setByte";
//			case "short":
//			case "Short":
//				return "setShort";
//			case "int":
//			case "Integer":
//				return "setInt";
//			case "long":
//			case "Long":
//				return "setLong";
//			case "double":
//			case "Double":
//				return "setDouble";
//			case "boolean":
//			case "Boolean":
//				return "setBoolean";
//			case "float":
//			case "Float":
//				return "setFloat";
//			case "BigDecimal":
//				// TODO test converted to SQL NUMERIC NOT (!) DECIMAL
//				return "setBigDecimal";
//			case "Date":
//				return "setDate";
//		}
//		throw new BarnacleRuntimeException("Unknown Type: " + fieldType);
//	}

	public static String getPreparedStatementSetMethod(String fieldType) {

		if (Character.isUpperCase(fieldType.charAt(0)))
			throw new IllegalArgumentException("PreparedStatement set-method is only available for basic types.");

		switch (fieldType) {
			case "byte":
				return "setByte";
			case "short":
				return "setShort";
			case "int":
				return "setInt";
			case "long":
				return "setLong";
			case "double":
				return "setDouble";
			case "boolean":
				return "setBoolean";
			case "float":
				return "setFloat";
		}
		throw new BarnacleRuntimeException("Unknown Type: " + fieldType);
	}


	public static String getResultSetGetMethod(String fieldType) {
		
		if (Character.isUpperCase(fieldType.charAt(0)))
			throw new IllegalArgumentException("ResultSet get-method is only available for basic types.");
		
		switch (fieldType) {
			case "byte":
				return "getByte";
			case "short":
				return "getShort";
			case "int":
				return "getInt";
			case "long":
				return "getLong";
			case "double":
				return "getDouble";
			case "boolean":
				return "getBoolean";
			case "float":
				return "getFloat";
		}
		throw new BarnacleRuntimeException("Unknown Type: " + fieldType);
	}


}
