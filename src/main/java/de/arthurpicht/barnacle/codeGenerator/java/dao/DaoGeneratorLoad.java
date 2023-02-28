package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.utils.core.strings.Strings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoGeneratorLoad {

    public static void addPreparedStatementLoadAsLocalConst(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();
        String statement = "SELECT * FROM " + entity.getTableName() + " WHERE ";
        statement += DaoGeneratorCommons.getPreparedStatementSearchConditionForPk(entity);
        daoGenerator.getLocalStringConstGenerator().add("LOAD_STATEMENT", statement);
    }

    public static void addLoadMethod(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();

        String pkSimpleClassName = entity.getPkSimpleClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnTypeByCanonicalClassName(entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("load");

        if (entity.isComposedPk()) {
            String pkCanonicalClassName = entity.getPkCanonicalClassName();
            methodGenerator.addAndImportParameter(pkCanonicalClassName, pkVarName);
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            methodGenerator.addParameter(pkAttribute.getJavaTypeSimpleName(), pkAttribute.getFieldName());
        }

        methodGenerator.addThrowsException(daoGenerator.getConnectionExceptionCanonicalClassName());
        methodGenerator.addThrowsException(daoGenerator.getEntityNotFoundExceptionCanonicalClassName());
        methodGenerator.addCodeLn(daoGenerator.createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");

        if (entity.isComposedPk()) {
            methodGenerator.addCodeLn("return load(" + pkVarName + ", connection);");
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            methodGenerator.addCodeLn("return load(" + pkAttribute.getFieldName() + ", connection);");
        }

        methodGenerator.addCodeLn("} catch(" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + daoGenerator.getConnectionExceptionSimpleClassName() + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn(daoGenerator.generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    public static void addLoadMethodByConnection(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();
        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        createSignature(daoGenerator, methodGenerator, entity);
        DaoGeneratorCommons.buildPreparedStatementByPkAttributes(
                "LOAD_STATEMENT", daoGenerator, methodGenerator, entity);
        processResultSet(daoGenerator, methodGenerator, entity);
    }

    private static void createSignature(DaoGenerator daoGenerator, MethodGenerator methodGenerator, Entity entity) {
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnTypeByCanonicalClassName(entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("load");
        if (entity.isComposedPk()) {
            String pkCanonicalClassName = entity.getPkCanonicalClassName();
            String pkVarName = JavaGeneratorHelper.getPkVarName(entity);
            methodGenerator.addAndImportParameter(pkCanonicalClassName, pkVarName);
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            methodGenerator.addParameter(pkAttribute.getJavaTypeSimpleName(), pkAttribute.getFieldName());
        }
        methodGenerator.addAndImportParameter(Connection.class, "connection");
        methodGenerator.addThrowsException(SQLException.class);
        methodGenerator.addThrowsException(daoGenerator.getEntityNotFoundExceptionCanonicalClassName());
    }

    private static void processResultSet(DaoGenerator daoGenerator, MethodGenerator methodGenerator, Entity entity) {
        methodGenerator.addCodeLn("ResultSet resultSet = preparedStatement.executeQuery();");
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("if (resultSet.next()) {");

        String voVarName = JavaGeneratorHelper.getVoVarName(entity);
        methodGenerator.addCode(
                entity.getVoSimpleClassName() + " "
                + voVarName
                + " = new " + entity.getVoSimpleClassName() + "(");
        if (entity.isComposedPk()) {
            methodGenerator.addCode(JavaGeneratorHelper.getPkVarName(entity));
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            methodGenerator.addCode(pkAttribute.getFieldName());
        }
        methodGenerator.addCodeLn(");");

        List<Attribute> nonPkAttributes = entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(daoGenerator.generateVoAssignmentFromResultSet(entity, attribute));
        }

        methodGenerator.addCodeLn("return " + voVarName + ";");
        methodGenerator.addCodeLn("} else {");

        methodGenerator.addCode("throw new " + daoGenerator.getEntityNotFoundExceptionSimpleClassName()
                + "(\"Entity '" + entity.getTableName() + "' with primary key ");
        if (entity.isComposedPk()) {
            String pkVarName = JavaGeneratorHelper.getPkVarName(entity);
            String pkSimpleClassName = entity.getPkSimpleClassName();
            methodGenerator.addCode(pkSimpleClassName + " = [\" + " + pkVarName);
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            methodGenerator.addCode("field '" + pkAttribute.getFieldName() + "' = [\" + " + pkAttribute.getFieldName());
        }
        methodGenerator.addCodeLn(" + \"] not found!\");");
        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException ignored) {}}");
        methodGenerator.addCodeLn("try { preparedStatement.close(); } catch (SQLException ignored) {}");
        methodGenerator.addCodeLn("}");
    }

}
