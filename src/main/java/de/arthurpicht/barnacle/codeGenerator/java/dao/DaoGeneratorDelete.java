package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DaoGeneratorDelete {

    public static void addPreparedStatementDeleteAsLocalConst(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();
        String statement = "DELETE FROM " + entity.getTableName() + " WHERE ";
        statement += DaoGeneratorCommons.getPreparedStatementSearchConditionForPk(entity);
        daoGenerator.getLocalStringConstGenerator().add("DELETE_STATEMENT", statement);
    }

    public static void addDeleteMethod(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();

        String pkSimpleClassName = entity.getPkSimpleClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("delete");

        if (entity.isComposedPk()) {
            methodGenerator.addAndImportParameter(entity.getPkCanonicalClassName(), pkVarName);
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            methodGenerator.addParameter(pkAttribute.getJavaTypeSimpleName(), pkAttribute.getFieldName());
        }

        methodGenerator.addThrowsException(daoGenerator.getConnectionExceptionCanonicalClassName());
        methodGenerator.addCodeLn(daoGenerator.createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");

        if (entity.isComposedPk()) {
            methodGenerator.addCodeLn("delete(" + pkVarName + ", connection);");
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            methodGenerator.addCodeLn("delete(" + pkAttribute.getFieldName() + ", connection);");
        }

        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + daoGenerator.getConnectionExceptionSimpleClassName() + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn(daoGenerator.generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    public static void addDeleteMethodByConnection(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        createSignature(methodGenerator, entity);
        DaoGeneratorCommons.buildPreparedStatementByPkAttributes(
                "DELETE_STATEMENT", daoGenerator, methodGenerator, entity);

        methodGenerator.addCodeLn("preparedStatement.execute();");
        methodGenerator.addCodeLn("try { preparedStatement.close(); } catch (SQLException ignored) {}");
    }

    private static void createSignature(MethodGenerator methodGenerator, Entity entity) {
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("delete");

        if (entity.isComposedPk()) {
            String pkVarName = JavaGeneratorHelper.getPkVarName(entity);
            methodGenerator.addAndImportParameter(entity.getPkCanonicalClassName(), pkVarName);
        } else {
            Attribute pkAttribute = entity.getSinglePkAttribute();
            methodGenerator.addParameter(pkAttribute.getJavaTypeSimpleName(), pkAttribute.getFieldName());
        }
        methodGenerator.addAndImportParameter(Connection.class, "connection");
        methodGenerator.addThrowsException(SQLException.class);
    }

}
