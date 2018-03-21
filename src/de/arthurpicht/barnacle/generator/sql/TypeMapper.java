package de.arthurpicht.barnacle.generator.sql;

import de.arthurpicht.barnacle.exceptions.UnknownTypeException;
import de.arthurpicht.barnacle.mapping.Attribute;

public abstract class TypeMapper {
	
	public static TypeMapper getInstance(Databases database) {
		if (database == Databases.MYSQL) {
			return new TypeMapperMySQL();
		}
		throw new RuntimeException("Impossible type mapper requested!");
	}
	
	/**
	 * Returns SQL-Type for passed attribute.
	 * 
	 * @param attribute
	 * @return
	 * @throws UnknownTypeException
	 */
	public String getSQLType(Attribute attribute) throws UnknownTypeException {
		String sqlType = null;
		if (attribute.hasCustomType()) {
			sqlType = attribute.getCustomType();
		} else {
			sqlType = this.getSqlType(attribute.getFieldTypeSimpleName());
		}
		return sqlType;
	}
	
	protected abstract String getSqlType(String fieldType) throws UnknownTypeException;

}
