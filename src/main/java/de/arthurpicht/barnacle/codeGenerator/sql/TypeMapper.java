package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;
import de.arthurpicht.barnacle.exceptions.UnknownTypeException;

public abstract class TypeMapper {
	
	public static TypeMapper getInstance(Const.Dialect dialect) {
		if (dialect == Const.Dialect.MYSQL) {
			return new TypeMapperMySQL();
		} else if (dialect == Const.Dialect.H2) {
			return new TypeMapperH2();
		}
		throw new BarnacleRuntimeException("Impossible type mapper requested!");
	}

	public String getSqlTypeByAutoMapping(Class<?> fieldClass) throws UnknownTypeException {
		String fieldJavaSimpleClassName = fieldClass.getSimpleName();
		SqlType sqlType = this.getMapping(fieldJavaSimpleClassName);
		return sqlType.getSqlTypeString();
	}

	public String getSqlTypeByCustomType(String type, Integer para1, Integer para2) {
		return getParameterizedCustomType(type, para1, para2); 
	}
	
	public String getSqlTypeLiteralByAutoMapping(Class<?> fieldClass) throws UnknownTypeException {
		SqlType sqlType = this.getMapping(fieldClass.getSimpleName());
		return sqlType.getTypesLiteral();
	}

	public String getSqlTypeLiteralByCustomType(String type) {
		return "Types." + type;
	}

	private String getParameterizedCustomType(String type, Integer para1, Integer para2) {
		String typeString = type;
		if (para1 != null) {
			typeString += "(" + para1;
			if (para2 != null) {
				typeString += ", " + para2;
			}
			typeString += ")";
		}
		return typeString;
	}

	protected abstract SqlType getMapping(String javaFieldTypeSimpleName) throws UnknownTypeException;

}
