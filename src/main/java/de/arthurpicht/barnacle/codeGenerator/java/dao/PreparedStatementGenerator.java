package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.model.Attribute;

public class PreparedStatementGenerator {

    public static String generateSetStatement(Attribute attribute, String getter, int index) {
        if (attribute.isJavaTypeSimple()) {
            String setMethod = TypeMapper.getPreparedStatementSetMethod(attribute.getJavaTypeSimpleName());
            return "preparedStatement." + setMethod + "(" + index + ", " + getter + ");";
        } else {
            String sqlTypeLiteral = attribute.getSqlDataTypeLiteral();
            return "preparedStatement.setObject(" + index + ", " + getter + ", " + sqlTypeLiteral + ");";
        }
    }

}
