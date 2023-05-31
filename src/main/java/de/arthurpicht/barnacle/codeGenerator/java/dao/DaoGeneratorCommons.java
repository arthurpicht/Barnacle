package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Attributes;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;
import de.arthurpicht.utils.core.strings.Strings;

import java.util.ArrayList;
import java.util.List;

public class DaoGeneratorCommons {

    public static String initializePreparedStatement(String statementConstName) {
        return "PreparedStatement preparedStatement = connection.prepareStatement(" + statementConstName + ");";
    }

    public static String getPreparedStatementSearchConditionForPk(Entity entity) {
        if (entity.isComposedPk()) {
            List<Attribute> pkAttributes = entity.getPkAttributes();
            return getPreparedStatementSearchCondition(pkAttributes);
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            return pkAttribute.getColumnName() + " = ?";
        }
    }

    public static String getPreparedStatementSearchConditionForFk(ForeignKeyWrapper foreignKeyWrapper) {
        List<Attribute> fkAttributes = foreignKeyWrapper.getKeyFieldAttributes();
        return getPreparedStatementSearchCondition(fkAttributes);
    }

    public static String getPreparedStatementSearchCondition(List<Attribute> attributeList) {
        List<String> columnNames = Attributes.getColumnNames(attributeList);
        return getSearchCondition(columnNames);
    }

    private static String getSearchCondition(List<String> columnNames) {
        return Strings.listing(columnNames, " AND ", "", "", "", " = ?");
    }

    public static String generatePreparedStatementSetterFromVO(int index, Entity entity, Attribute attribute, List<String> valueList) {
        String voVarName = JavaGeneratorHelper.getVoVarName(entity);
        String getterMethodName = attribute.generateGetterMethodName();
        String getter = voVarName + "." + getterMethodName + "()";
        String line = PreparedStatementGenerator.generateSetStatement(attribute, getter, index);
        valueList.add(getter);
        return line;

//        String preparedStatementSetMethod = TypeMapper.getPreparedStatementSetMethod(attribute.getJavaTypeSimpleName());
//        String voVarName = JavaGeneratorHelper.getVoVarName(entity);
//        String getterMethodName = attribute.generateGetterMethodName();
//        String value = voVarName + "." + getterMethodName + "()";
//        valueList.add(value);
//        return "preparedStatement." + preparedStatementSetMethod + "(" + index + ", " + value + ");";
    }

    public static String generateLogStringForPreparedStatement(String preparedStatementConstName, List<String> values) {
        return Strings.listing(
                values, " + \"][\" + ",preparedStatementConstName + " + \" [\" + ", " + \"]\""
        );
    }

    public static void buildPreparedStatementByPkAttributes(
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
                String setMethod = TypeMapper.getPreparedStatementSetMethod(pkAttribute.getJavaTypeSimpleName());
                String getter = pkVarName + "." + pkAttribute.generateGetterMethodName() + "()";
                getterList.add(getter);
                methodGenerator.addCodeLn("preparedStatement." + setMethod + "(" + i + ", " + getter + ");");
                i++;
            }
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            String setMethod = TypeMapper.getPreparedStatementSetMethod(pkAttribute.getJavaTypeSimpleName());
            String field = pkAttribute.getFieldName();
            getterList.add(field);
            methodGenerator.addCodeLn("preparedStatement." + setMethod + "(1, " + field + ");");
        }

        String logStatement = Strings.listing(getterList, " + \"][\" + ",
                preparedStatementVarName + " + \" [\" + ", " + \"]\"");
        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByExpression(logStatement));
    }

    /**
     * Generates assignment of a result set column to a local variable, named as
     * the original field name. If the field type is not a basic type, the value
     * is determined by using the getObject-method in conjunction with appropriate
     * casting.
     *
     * @param entity
     * @param attribute
     * @return
     */
    public static String generateLocalVarFromResultSet(Entity entity, Attribute attribute) {
        String fieldType = attribute.getJavaTypeSimpleName();
        String out = fieldType + " " + attribute.getFieldName() + " = ";
        String resultSetGetter = TypeMapper.getResultSetGetMethod(fieldType);
            out += "resultSet." + resultSetGetter;
        out += "(\"" + attribute.getColumnName() + "\");";
        return out;
    }
}
