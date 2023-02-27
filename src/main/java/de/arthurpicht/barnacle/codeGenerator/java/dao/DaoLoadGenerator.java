package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Attributes;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.utils.core.strings.Strings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DaoLoadGenerator {

    public static void addLoadMethod(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();

        String pkSimpleClassName = entity.getPkSimpleClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnTypeByCanonicalClassName(entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("load");

        Attribute pkAttribute = entity.getAttributes().get(0);

        if (entity.isComposedPk()) {
            String pkCanonicalClassName = entity.getPkCanonicalClassName();
            methodGenerator.addAndImportParameter(pkCanonicalClassName, pkVarName);
        } else {
            methodGenerator.addParameter(pkAttribute.getJavaTypeSimpleName(), pkAttribute.getFieldName());
        }

        methodGenerator.addThrowsException(daoGenerator.getConnectionExceptionCanonicalClassName());
        methodGenerator.addThrowsException(daoGenerator.getEntityNotFoundExceptionCanonicalClassName());
        methodGenerator.addCodeLn(daoGenerator.createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");

        if (entity.isComposedPk()) {
            methodGenerator.addCodeLn("return load(" + pkVarName + ", connection);");
        } else {
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

        String voSimpleClassName = entity.getVoSimpleClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName);
        String pkSimpleClassName = entity.getPkSimpleClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnTypeByCanonicalClassName(entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("load");

        Attribute pkAttribute = entity.getAttributes().get(0);

        if (entity.isComposedPk()) {
            String pkCanonicalClassName = entity.getPkCanonicalClassName();
            methodGenerator.addAndImportParameter(pkCanonicalClassName, pkVarName);
        } else {
            methodGenerator.addParameter(pkAttribute.getJavaTypeSimpleName(), pkAttribute.getFieldName());
        }
        methodGenerator.addAndImportParameter(Connection.class, "connection");

        methodGenerator.addThrowsException(SQLException.class);
        methodGenerator.addThrowsException(daoGenerator.getEntityNotFoundExceptionCanonicalClassName());


        methodGenerator.addCodeLn("String sql = \"SELECT * FROM \" + " + voSimpleClassName + ".TABLENAME + \" WHERE \"");

        if (entity.isComposedPk()) {
            List<Attribute> pkAttributes = entity.getPkAttributes();
            boolean sequence = false;
            for (Attribute attribute : pkAttributes) {
                if (sequence) {
                    methodGenerator.addCodeLn(" + \" AND \"");
                }
                methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getConstName() + " + \" = \" + getValueExpression(" + pkVarName + "." + attribute.generateGetterMethodName() + "(), \"" + attribute.getSqlDataType() + "\")");
                sequence = true;
            }
            methodGenerator.addCodeLn(";");
        } else {
            methodGenerator.addCodeLn("+ " + voSimpleClassName + "." + pkAttribute.getConstName() + " + \" = \" + getValueExpression(" + pkAttribute.getFieldName() + ", \"" + pkAttribute.getSqlDataType() + "\");");
        }

        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

        methodGenerator.addCodeLn("Statement statement = connection.createStatement();");
        methodGenerator.addCodeLn("ResultSet resultSet = statement.executeQuery(sql);");
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("if (resultSet.next()) {");

        methodGenerator.addCode(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(");
        if (entity.isComposedPk()) {
            methodGenerator.addCode(pkVarName);
        } else {
            methodGenerator.addCode(pkAttribute.getFieldName());
        }
        methodGenerator.addCodeLn(");");

        List<Attribute> nonPkAttributes = entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(daoGenerator.generateLocalVarFromResultSet(entity, attribute));
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName() + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn("return " + voVarName + ";");
        methodGenerator.addCodeLn("} else {");

        methodGenerator.addCode("throw new " + daoGenerator.getEntityNotFoundExceptionCanonicalClassName()
                + "(" + voSimpleClassName + ".TABLENAME + \"-Entity with primary key ");
        if (entity.isComposedPk()) {
            methodGenerator.addCode(pkSimpleClassName + " = \" + " + pkVarName + ".toString()");
        } else {
            methodGenerator.addCode("field '" + pkAttribute.getFieldName() + "' = \" + " + pkAttribute.getFieldName());
        }
        methodGenerator.addCodeLn(" + \" does not exist!\");");
        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("}");
    }

    public static void addPreparedStatementLoadAsLocalConst(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();
        // TODO Check this: PK-Attribute always index 0?
        Attribute pkAttribute = entity.getAttributes().get(0);
        String statement = "SELECT * FROM " + entity.getTableName() + " WHERE ";

        if (entity.isComposedPk()) {
            List<Attribute> pkAttributes = entity.getPkAttributes();
            List<String> columnNames = Attributes.getColumnNames(pkAttributes);
            String listing = Strings.listing(
                    columnNames, " AND ", "", "", "", " = ?");
            statement += listing;
        } else {
            statement += pkAttribute.getColumnName() + " = ?";
        }
        daoGenerator.getLocalStringConstGenerator().add("LOAD_STATEMENT", statement);
    }

}
