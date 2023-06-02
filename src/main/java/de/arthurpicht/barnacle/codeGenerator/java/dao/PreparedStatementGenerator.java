package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.helper.StringHelper;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Attributes;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;
import de.arthurpicht.utils.core.strings.Strings;

import java.util.ArrayList;
import java.util.List;

public class PreparedStatementGenerator {

    public static String getInitializationStatement(String statementConstName) {
        return "PreparedStatement preparedStatement = connection.prepareStatement(" + statementConstName + ");";
    }

    public static String getSearchConditionForPk(Entity entity) {
        if (entity.isComposedPk()) {
            List<Attribute> pkAttributes = entity.getPkAttributes();
            return getSearchCondition(pkAttributes);
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            return pkAttribute.getColumnName() + " = ?";
        }
    }

    public static String getSearchConditionForFk(ForeignKeyWrapper foreignKeyWrapper) {
        List<Attribute> fkAttributes = foreignKeyWrapper.getKeyFieldAttributes();
        return getSearchCondition(fkAttributes);
    }

    public static String getSearchCondition(List<Attribute> attributeList) {
        List<String> columnNames = Attributes.getColumnNames(attributeList);
        return Strings.listing(columnNames, " AND ", "", "", "", " = ?");
    }

    public static String generateFromVO(int index, Entity entity, Attribute attribute, List<String> valueList) {
        String voVarName = JavaGeneratorHelper.getVoVarName(entity);
        String getterMethodName = attribute.generateGetterMethodName();
        String getter = voVarName + "." + getterMethodName + "()";
        String line = getSetStatement(attribute, getter, index);
        valueList.add(getter);
        return line;
    }

    public static String generateLogString(String preparedStatementConstName, List<String> values) {
        return Strings.listing(
                values, " + \"][\" + ",preparedStatementConstName + " + \" [\" + ", " + \"]\""
        );
    }

    public static void getByPkAttributes(
            String preparedStatementVarName,
            DaoGenerator daoGenerator, MethodGenerator methodGenerator, Entity entity) {

        methodGenerator.addCodeLn(
                "PreparedStatement preparedStatement = connection.prepareStatement("
                        + preparedStatementVarName + ");");
        List<String> getterList = new ArrayList<>();
        if (entity.isComposedPk()) {
            List<Attribute> pkAttributes = entity.getPkAttributes();
            String pkVarName = JavaGeneratorHelper.getPkVarName(entity);
            int i=1;
            for (Attribute pkAttribute : pkAttributes) {
                String getter = pkVarName + "." + pkAttribute.generateGetterMethodName() + "()";
                methodGenerator.addCodeLn(PreparedStatementGenerator.getSetStatement(pkAttribute, getter, i));
                getterList.add(getter);
                i++;
            }
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            String field = pkAttribute.getFieldName();
            String statement = PreparedStatementGenerator.getSetStatement(pkAttribute, field, 1);
            methodGenerator.addCodeLn(statement);
            getterList.add(field);
        }

        String logStatement = Strings.listing(getterList, " + \"][\" + ",
                preparedStatementVarName + " + \" [\" + ", " + \"]\"");
        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByExpression(logStatement));
    }

    public static String getSetStatement(Attribute attribute, String getter, int index) {
        if (attribute.isJavaTypeSimple()) {
            String setMethod = getPreparedStatementSetMethod(attribute.getJavaTypeSimpleName());
            return "preparedStatement." + setMethod + "(" + index + ", " + getter + ");";
        } else {
            String sqlTypeLiteral = attribute.getSqlDataTypeLiteral();
            return "preparedStatement.setObject(" + index + ", " + getter + ", " + sqlTypeLiteral + ");";
        }
    }

    public static String getPreparedStatementSetMethod(String simpleFieldType) {
        if (SimpleTypeHelper.isNoSimpleType(simpleFieldType))
            throw new IllegalArgumentException("PreparedStatement set-method is only available for basic types" +
                    " (except char).");

        return "set" + StringHelper.shiftFirstLetterToUpperCase(simpleFieldType);
    }
    
}
