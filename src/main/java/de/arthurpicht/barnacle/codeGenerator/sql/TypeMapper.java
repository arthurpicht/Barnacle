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
		String sqlType;
		if (attribute.hasCustomType()) {
			sqlType = attribute.getCustomType();
		} else {
			sqlType = this.getSqlType(attribute.getJavaTypeSimpleName());
		}
		return sqlType;
	}
	
	protected abstract String getSqlType(String fieldType) throws UnknownTypeException;

	public static String getPreparedStatementSetMethod(String fieldType) {
		switch (fieldType) {
			case "String":
				return "setString";
			case "byte":
			case "Byte":
				return "setByte";
			case "short":
			case "Short":
				return "setShort";
			case "int":
			case "Integer":
				return "setInt";
			case "long":
			case "Long":
				return "setLong";
			case "double":
			case "Double":
				return "setDouble";
			case "boolean":
			case "Boolean":
				return "setBoolean";
			case "float":
			case "Float":
				return "setFloat";
			case "BigDecimal":
				// TODO test converted to SQL NUMERIC NOT (!) DECIMAL
				return "setBigDecimal";
			case "Date":
				return "setDate";
		}
		throw new BarnacleRuntimeException("Unknown Type: " + fieldType);
	}

	public static String getResultSetGetMethod(String fieldType) {
		switch (fieldType) {
			case "String":
				return "getString";
			case "byte":
			case "Byte":
				return "getByte";
			case "short":
			case "Short":
				return "getShort";
			case "int":
			case "Integer":
				return "getInt";
			case "long":
			case "Long":
				return "getLong";
			case "double":
			case "Double":
				return "getDouble";
			case "boolean":
			case "Boolean":
				return "getBoolean";
			case "float":
			case "Float":
				return "getFloat";
			case "BigDecimal":
				// TODO test converted to SQL NUMERIC NOT (!) DECIMAL
				return "getBigDecimal";
			case "Date":
				return "getDate";
		}
		throw new BarnacleRuntimeException("Unknown Type: " + fieldType);
	}

}
