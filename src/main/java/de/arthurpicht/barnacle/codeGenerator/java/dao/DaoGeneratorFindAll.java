package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.utils.core.strings.Strings;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

public class DaoGeneratorFindAll {

    public static void addPreparedStatementFindAllAsLocalConst(DaoGenerator daoGenerator) {
        String statement = "SELECT * FROM " + daoGenerator.getEntity().getTableName();
        daoGenerator.getLocalStringConstGenerator().add("FIND_ALL_STATEMENT", statement);
    }

    public static void addFindAllMethod(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();

        String voSimpleClassName = entity.getVoSimpleClassName();
        String voCanonicalClassName = entity.getVoCanonicalClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("findAll");
        methodGenerator.addThrowsException(daoGenerator.getConnectionExceptionCanonicalClassName());

        methodGenerator.addCodeLn(daoGenerator.createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("return findAll(connection);");
        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + daoGenerator.getConnectionExceptionSimpleClassName() + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn(daoGenerator.generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    public static void addFindAllMethodByConnection(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();
        String voSimpleClassName = entity.getVoSimpleClassName();
        String voCanonicalClassName = entity.getVoCanonicalClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName);
        String voListVarName = voVarName + "s";

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("findAll");
        methodGenerator.addAndImportParameter(Connection.class, "connection");
        methodGenerator.addThrowsException(SQLException.class);

        methodGenerator.addCodeLn("Statement statement = connection.createStatement();");

        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByExpression("FIND_ALL_STATEMENT"));

        methodGenerator.addCodeLn("ResultSet resultSet = statement.executeQuery(FIND_ALL_STATEMENT);");
        methodGenerator.addCodeLn(
                "List<" + voSimpleClassName + "> " + voListVarName + " = new ArrayList<>();"
        );
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("while (resultSet.next()) {");

        for (Attribute attribute : entity.getAttributes()) {
            methodGenerator.addCodeLn(DaoGeneratorCommons.generateLocalVarFromResultSet(entity, attribute));
        }

        if (entity.isComposedPk()) {
            String pkSimpleClassName = entity.getPkSimpleClassName();
            String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);
            methodGenerator.addCode(pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(");
            List<Attribute> pkAttributes = entity.getPkAttributes();
            List<String> attributeFieldNames = pkAttributes.stream()
                    .map(Attribute::getFieldName)
                    .collect(Collectors.toList());
            String fieldNameListing = Strings.listing(attributeFieldNames, ", ");
            methodGenerator.addCode(fieldNameListing);
            methodGenerator.addCodeLn(");");
            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName
                    + "(" + pkVarName + ");");

        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName
                    + "(" + pkAttribute.getFieldName() + ");");
        }

        List<Attribute> nonPkAttributes = entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName()
                    + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn(voListVarName + ".add(" + voVarName + ");");
        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("return " + voListVarName + ";");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException ignored) {}}");
        methodGenerator.addCodeLn("try { statement.close(); } catch (SQLException ignored) {}");
        methodGenerator.addCodeLn("}");
    }

}
