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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DaoGeneratorUpdate {

    public static void addPreparedStatementUpdateAsLocalConst(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();
        String statement = "UPDATE " + entity.getTableName() + " SET ";

        List<Attribute> attributes = entity.getNonPkAttributes();
        List<String> columnNames = Attributes.getColumnNames(attributes);
        String columnNameListing = Strings.listing(columnNames, ", ", "", "", "", " = ?");
        statement += columnNameListing;
        statement += " WHERE ";
        statement += DaoGeneratorCommons.getPreparedStatementSearchConditionForPk(entity);

        daoGenerator.getLocalStringConstGenerator().add("UPDATE_STATEMENT", statement);
    }

    public static void addUpdateMethod(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();

        String voSimpleClassName = entity.getVoSimpleClassName();
        String voCanonicalClassName = entity.getVoCanonicalClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("update");
        methodGenerator.addAndImportParameter(voCanonicalClassName, voVarName);
        methodGenerator.addThrowsException(daoGenerator.getConnectionExceptionCanonicalClassName());

        methodGenerator.addCodeLn(daoGenerator.createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");

        methodGenerator.addCodeLn("update(" + voVarName + ", connection);");

        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + daoGenerator.getConnectionExceptionSimpleClassName() + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn(daoGenerator.generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    public static void addUpdateMethodByConnection(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();

        String voSimpleClassName = entity.getVoSimpleClassName();
        String voCanonicalClassName = entity.getVoCanonicalClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("update");
        methodGenerator.addAndImportParameter(voCanonicalClassName, voVarName);
        methodGenerator.addAndImportParameter(Connection.class, "connection");
        methodGenerator.addThrowsException(SQLException.class);

        methodGenerator.addCodeLn(
                DaoGeneratorCommons.initializePreparedStatement("UPDATE_STATEMENT")
        );

        List<String> valueList = new ArrayList<>();
        int index = 1;
        List<Attribute> nonPkAttributes = entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(
                    DaoGeneratorCommons.generatePreparedStatementSetterFromVO(index, entity, attribute, valueList)
            );
            index++;
        }
        List<Attribute> pkAttributes = entity.getPkAttributes();
        for (Attribute attribute : pkAttributes) {
            methodGenerator.addCodeLn(
                    DaoGeneratorCommons.generatePreparedStatementSetterFromVO(index, entity, attribute, valueList)
            );
            index++;
        }

        String loggingString
                = DaoGeneratorCommons.generateLogStringForPreparedStatement(
                "UPDATE_STATEMENT", valueList
        );
        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByExpression(loggingString));

        methodGenerator.addCodeLn("preparedStatement.executeUpdate();");
        methodGenerator.addCodeLn("try { preparedStatement.close(); } catch (SQLException ignored) {}");
    }

}
