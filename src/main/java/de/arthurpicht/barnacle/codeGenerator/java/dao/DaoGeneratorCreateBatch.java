package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.utils.core.strings.Strings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoGeneratorCreateBatch {

    public static void addCreateMethod(DaoGenerator parentDaoGenerator) {
        MethodGenerator methodGenerator = parentDaoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("create");

        Entity entity = parentDaoGenerator.getEntity();
        String voCanonicalClassName = entity.getVoCanonicalClassName();
        String voVarListName = JavaGeneratorHelper.getVoVarListName(entity);
        String connectionExceptionName = parentDaoGenerator.getConnectionExceptionCanonicalClassName();

        methodGenerator.addParameter(List.class, voCanonicalClassName, voVarListName);
        methodGenerator.addThrowsException(connectionExceptionName);

        methodGenerator.addCodeLn(parentDaoGenerator.createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("create(" + voVarListName + ", connection);");
        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " sqlEx) {");
        methodGenerator.addCodeLn("throw new "
                + JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(connectionExceptionName) + "(sqlEx);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn(parentDaoGenerator.generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    public static void addCreateMethodByConnection(DaoGenerator daoGenerator) {

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("create");

        Entity entity = daoGenerator.getEntity();
        String voCanonicalClassName = entity.getVoCanonicalClassName();
        String voSimpleClassName = entity.getVoSimpleClassName();
        String voVarName = JavaGeneratorHelper.getVoVarName(entity);
        String voVarListName = JavaGeneratorHelper.getVoVarListName(entity);
        methodGenerator.addParameter(List.class, voCanonicalClassName, voVarListName);
        methodGenerator.addParameter(Connection.class, "connection");

        methodGenerator.addThrowsException(SQLException.class);
        if (entity.hasAutoIncrementAttribute())
            methodGenerator.addThrowsException(daoGenerator.getConnectionExceptionCanonicalClassName());

        if (entity.hasAutoIncrementAttribute()) {
            methodGenerator.addCodeLn(
                    "PreparedStatement preparedStatement = connection.prepareStatement(" +
                            "CREATE_STATEMENT, Statement.RETURN_GENERATED_KEYS);");
        } else {
            methodGenerator.addCodeLn(
                    "PreparedStatement preparedStatement = connection.prepareStatement(CREATE_STATEMENT);");
        }

        methodGenerator.addCodeLn("for (" + voSimpleClassName + " " + voVarName + " : " + voVarListName + ") {");

        int index = 1;
        List<String> getterList = new ArrayList<>();
        for (Attribute attribute : entity.getNonAutoIncrementAttributes()) {
            String getter = voVarName + "." + attribute.generateGetterMethodName() + "()";
            String line = PreparedStatementGenerator.getSetStatement(attribute, getter, index);
            methodGenerator.addCodeLn(line);
            getterList.add(getter);
            index++;
        }

        String logStatement = Strings.listing(getterList, " + \"][\" + ", "CREATE_STATEMENT + \" [\" + ", " + \"]\"");
        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByExpression(logStatement));
        methodGenerator.addCodeLn("preparedStatement.addBatch();");
        methodGenerator.addCodeLn("}");

        methodGenerator.addCodeLn("preparedStatement.executeBatch();");

        if (entity.hasAutoIncrementAttribute()) {
            methodGenerator.addCodeLn("ResultSet resultSet = preparedStatement.getGeneratedKeys();");
            methodGenerator.addCodeLn("for (" + voSimpleClassName + " " + voVarName + " : " + voVarListName + ") {");

            Attribute autoIncrementAttribute = entity.getAutoIncrementAttribute();
            methodGenerator.addCodeLn("if (resultSet.next()) {");
            methodGenerator.addCodeLn("int generatedKey = resultSet.getInt(1);");
            methodGenerator.addCodeLn(voVarName + "." + autoIncrementAttribute.generateSetterMethodName()
                    + "(generatedKey);");
            methodGenerator.addCodeLn("} else {");
            methodGenerator.addCodeLn("throw new " + daoGenerator.getConnectionExceptionSimpleClassName()
                    + "(\"Could not obtain generated key(s) for auto increment field on batch create.\");");
            methodGenerator.addCodeLn("}");
            methodGenerator.addCodeLn("}");
            methodGenerator.addCodeLn("try { resultSet.close(); } catch (SQLException ignored) {}");
        }

        methodGenerator.addCodeLn("try { preparedStatement.close(); } catch (SQLException ignored) {}");
    }

}
