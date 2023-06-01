package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;

public class ResultSetStatementGenerator {

    public static String generateGetStatement(Attribute attribute) {
        if (attribute.isJavaTypeSimple()) {
            String getMethod = TypeMapper.getResultSetGetMethod(attribute.getJavaTypeSimpleName());
            return "resultSet." + getMethod + "(\"" + attribute.getColumnName() + "\")";
        } else {
            String javaClassTypeLiteral = attribute.getTypeLiteral();
            return "resultSet.getObject(\"" + attribute.getColumnName() + "\", "  +  javaClassTypeLiteral + ")";
        }
    }

    public static String generateVoAssignmentFromResultSet(Entity entity, Attribute attribute) {
        // example:
        // personCompositeVO.setAge(resultSet.getInt("age"));
        // personCompositeVO.setAge(resultSet.getObject("age", Integer.class);
        String resultSetStatement = generateGetStatement(attribute);
        return JavaGeneratorHelper.getVoVarName(entity) + "."
                + attribute.generateSetterMethodName() + "("
                + resultSetStatement
                + ");";
    }

    /**
     * Generates assignment of a result set column to a local variable, named as
     * the original field name. If the field type is not a basic type, the value
     * is determined by using the getObject-method in conjunction with appropriate
     * casting.
     */
    public static String generateLocalVarFromResultSet(Attribute attribute) {
        String fieldType = attribute.getJavaTypeSimpleName();
        String out = fieldType + " " + attribute.getFieldName() + " = ";
        out += generateGetStatement(attribute) + ";";
        return out;
    }

}
