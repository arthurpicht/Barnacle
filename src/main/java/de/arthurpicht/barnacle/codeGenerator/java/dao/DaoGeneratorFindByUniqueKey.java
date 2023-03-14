package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.helper.StringHelper;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Attributes;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.utils.core.strings.Strings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoGeneratorFindByUniqueKey {

    public static void addPreparedStatement(String uniqueKeyName, List<Attribute> uniqueKeyAttributeList, DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();

        String statement = "SELECT * FROM " + entity.getTableName() + " WHERE ";
        statement += DaoGeneratorCommons.getPreparedStatementSearchCondition(uniqueKeyAttributeList);

        String constName = uniqueKeyName.toUpperCase() + "_STATEMENT";
        daoGenerator.getLocalStringConstGenerator().add(constName, statement);
    }

    public static void addFindByUniqueKeyMethod(String uniqueKeyName, List<Attribute> uKAttributeList, DaoGenerator daoGenerator) {

        Entity entity = daoGenerator.getEntity();

        String voSimpleClassName = entity.getVoSimpleClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName);
        String pkSimpleClassName = entity.getPkSimpleClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnTypeByCanonicalClassName(entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("findBy" + StringHelper.shiftFirstLetterToUpperCase(uniqueKeyName));
        methodGenerator.addAsParameters(uKAttributeList);
        methodGenerator.addThrowsException(daoGenerator.getConnectionExceptionCanonicalClassName());
        methodGenerator.addThrowsException(daoGenerator.getEntityNotFoundExceptionCanonicalClassName());

        List<String> fkFieldNames = Attributes.getFieldNames(uKAttributeList);
        String ukParameterListing = Strings.listing(fkFieldNames, ", ");
        methodGenerator.addCodeLn(daoGenerator.createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("return " + getMethodName(uniqueKeyName) + "(" + ukParameterListing + ", connection);");
        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + daoGenerator.getConnectionExceptionSimpleClassName() + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn(daoGenerator.generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    public static void addFindByUkConnectionMethod(String uniqueKeyName, List<Attribute> uKAttributeList, DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();
        String voSimpleClassName = entity.getVoSimpleClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName);
        String pkSimpleClassName = entity.getPkSimpleClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnTypeByCanonicalClassName(entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("findBy" + StringHelper.shiftFirstLetterToUpperCase(uniqueKeyName));
        methodGenerator.addAsParameters(uKAttributeList);
        methodGenerator.addParameter(Connection.class, "connection");
        methodGenerator.addThrowsException(daoGenerator.getEntityNotFoundExceptionCanonicalClassName());
        methodGenerator.addThrowsException(SQLException.class);

        String preparedStatementConstName = uniqueKeyName.toUpperCase() + "_STATEMENT";
        methodGenerator.addCodeLn("PreparedStatement preparedStatement " +
                "= connection.prepareStatement(" + preparedStatementConstName + ");");
        int i = 1;
        for (Attribute ukAttribute : uKAttributeList) {
            String setMethod = TypeMapper.getPreparedStatementSetMethod(ukAttribute.getJavaTypeSimpleName());
            methodGenerator.addCodeLn("preparedStatement." + setMethod + "(" + i + ", " + ukAttribute.getFieldName() + ");");
            i++;
        }

        String logStatement = Strings.listing(Attributes.getFieldNames(uKAttributeList), " + \"][\" + ",
                preparedStatementConstName + " + \" [\" + ", " + \"]\"");
        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByExpression(logStatement));

        methodGenerator.addCodeLn("ResultSet resultSet = preparedStatement.executeQuery();");
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("if (resultSet.next()) {");

//        methodGenerator.addCodeLn(daoGenerator.createGetConnectionStatement());
//        methodGenerator.addCodeLn("Statement statement = null;");
//        methodGenerator.addCodeLn("ResultSet resultSet = null;");
//        methodGenerator.addCodeLn("try {");
//        methodGenerator.addCodeLn("statement = connection.createStatement();");
//        methodGenerator.addCodeLn("String sql = \"SELECT * FROM \" + " + voSimpleClassName + ".TABLENAME + \" WHERE \"");
//
//        boolean sequence = false;
//        for (Attribute attribute : uKAttributeList) {
//            if (sequence) {
//                methodGenerator.addCodeLn(" + \" AND \"");
//            }
//            methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getConstName() + " + \" = \" + getValueExpression(" + attribute.getFieldName() + ", \"" + attribute.getSqlDataType() + "\")");
//            sequence = true;
//        }
//        methodGenerator.addCodeLn(";");
//
//        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
//        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

//        methodGenerator.addCodeLn("resultSet = statement.executeQuery(sql);");
//        methodGenerator.addCodeLn("if (resultSet.next()) {");

        List<Attribute> attributes = entity.getAttributes();
        for (Attribute attribute : attributes) {
            if (!uKAttributeList.contains(attribute)) {
                methodGenerator.addCodeLn(DaoGeneratorCommons.generateLocalVarFromResultSet(entity, attribute));
            }
        }

        if (entity.isComposedPk()) {
            methodGenerator.addCode(pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(");
            List<Attribute> pkAttributes = entity.getPkAttributes();
            List<String> pkFieldNames = Attributes.getFieldNames(pkAttributes);
            methodGenerator.addCode(Strings.listing(pkFieldNames, ", "));
            methodGenerator.addCodeLn(");");
            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkVarName + ");");
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();

            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkAttribute.getFieldName() + ");");
        }

        List<Attribute> nonPkAttributes = entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName() + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn("return " + voVarName + ";");
        methodGenerator.addCodeLn("} else {");

        methodGenerator.addCode("throw new " + daoGenerator.getEntityNotFoundExceptionSimpleClassName()
                + "(\"" + entity.getTableName() + "-Entity with unique key '" + uniqueKeyName + "'[");
//        sequence = false;
//        for (Attribute attribute : uKAttributeList) {
//            if (sequence) {
//                methodGenerator.addCode(" + \", ");
//            }
//            methodGenerator.addCode(attribute.getFieldName() + " = \" + " + attribute.getFieldName());
//            sequence = true;
//        }
        List<String> attributeValueList = new ArrayList<>();
        for (Attribute attribute : uKAttributeList) {
            String attributeValue = attribute.getFieldName() + " = \" + " + attribute.getFieldName();
            attributeValueList.add(attributeValue);
        }
        String attributeValueListing = Strings.listing(attributeValueList, " + \", ");
        methodGenerator.addCode(attributeValueListing);
        methodGenerator.addCodeLn(" + \"] does not exist!\");");
        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException ignored) {}}");
        methodGenerator.addCodeLn("try { preparedStatement.close(); } catch (SQLException ignored) {}");
        methodGenerator.addCodeLn("}");
    }

    private static String getMethodName(String uniqueKeyName) {
        return "findBy" + StringHelper.shiftFirstLetterToUpperCase(uniqueKeyName);
    }

}
