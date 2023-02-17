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
		if (fieldType.equals("String")) {
			return "setString";
		} else if (fieldType.equals("byte") || (fieldType.equals("Byte"))) {
			return "setByte";
		} else if (fieldType.equals("short") || (fieldType.equals("Short"))) {
			return "setShort";
		} else if (fieldType.equals("int") || (fieldType.equals("Integer"))) {
			return "setInt";
		} else if (fieldType.equals("long") || (fieldType.equals("Long"))) {
			return "setLong";
		} else if (fieldType.equals("double") || (fieldType.equals("Double"))) {
			return "setDouble";
		} else if (fieldType.equals("boolean") || (fieldType.equals("Boolean"))) {
			return "setBoolean";
		} else if (fieldType.equals("float") || (fieldType.equals("Float"))) {
			return "setFloat";
		} else if (fieldType.equals("BigDecimal")) {
			// TODO test converted to SQL NUMERIC NOT (!) DECIMAL
			return "setBigDecimal";
		} else if (fieldType.equals("Date")) {
			return "setDate";
		}
		throw new BarnacleRuntimeException("Unknown Type: " + fieldType);
	}

	public static String getResultSetGetMethod(String fieldType) {
		if (fieldType.equals("String")) {
			return "getString";
		} else if (fieldType.equals("byte") || (fieldType.equals("Byte"))) {
			return "getByte";
		} else if (fieldType.equals("short") || (fieldType.equals("Short"))) {
			return "getShort";
		} else if (fieldType.equals("int") || (fieldType.equals("Integer"))) {
			return "getInt";
		} else if (fieldType.equals("long") || (fieldType.equals("Long"))) {
			return "getLong";
		} else if (fieldType.equals("double") || (fieldType.equals("Double"))) {
			return "getDouble";
		} else if (fieldType.equals("boolean") || (fieldType.equals("Boolean"))) {
			return "getBoolean";
		} else if (fieldType.equals("float") || (fieldType.equals("Float"))) {
			return "getFloat";
		} else if (fieldType.equals("BigDecimal")) {
			// TODO test converted to SQL NUMERIC NOT (!) DECIMAL
			return "getBigDecimal";
		} else if (fieldType.equals("Date")) {
			return "getDate";
		}
		throw new BarnacleRuntimeException("Unknown Type: " + fieldType);
	}


}
