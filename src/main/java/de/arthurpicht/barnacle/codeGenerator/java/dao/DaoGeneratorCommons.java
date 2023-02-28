package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Attributes;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.utils.core.strings.Strings;

import java.util.ArrayList;
import java.util.List;

public class DaoGeneratorCommons {

    public static String getPreparedStatementSearchConditionForPk(Entity entity) {
        if (entity.isComposedPk()) {
            List<Attribute> pkAttributes = entity.getPkAttributes();
            List<String> columnNames = Attributes.getColumnNames(pkAttributes);
            return Strings.listing(
                    columnNames, " AND ", "", "", "", " = ?");
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            return pkAttribute.getColumnName() + " = ?";
        }
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

}
