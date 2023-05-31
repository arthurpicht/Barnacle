package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Attributes;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.utils.core.strings.Strings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DaoGeneratorCreate {

    public static void addPreparedStatementCreateAsLocalConst(DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();
        String statement = "INSERT INTO " + entity.getTableName();

        List<Attribute> attributes = entity.getNonAutoIncrementAttributes();
        List<String> columnNames = Attributes.getColumnNames(attributes);
        String columnNameListing = Strings.listing(columnNames, ", ", " (", ") ");
        statement += columnNameListing;

        statement += "VALUES";

        List<String> questionMarks = attributes.stream()
                .map(a -> "?")
                .collect(Collectors.toList());
        String questionMarkListing = Strings.listing(questionMarks, ", ", " (", ")");
        statement += questionMarkListing;

        daoGenerator.getLocalStringConstGenerator().add("CREATE_STATEMENT", statement);
    }

    public static void addCreateMethod(DaoGenerator parentDaoGenerator) {
        MethodGenerator methodGenerator = parentDaoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("create");

        Entity entity = parentDaoGenerator.getEntity();
        String voCanonicalClassName = entity.getVoCanonicalClassName();
        String voVarName = JavaGeneratorHelper.getVoVarName(entity);
        String connectionExceptionName = parentDaoGenerator.getConnectionExceptionCanonicalClassName();

        methodGenerator.addParameter(voCanonicalClassName, voVarName);
        methodGenerator.addThrowsException(connectionExceptionName);
        methodGenerator.addCodeLn(parentDaoGenerator.createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("create(" + voVarName + ", connection);");
        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " sqlEx) {");
        methodGenerator.addCodeLn("throw new "
                + JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(connectionExceptionName) + "(sqlEx);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn(parentDaoGenerator.generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    public static void addCreateMethodByConnection(DaoGenerator daoGenerator) {
        addPreparedStatementCreateAsLocalConst(daoGenerator);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("create");

        Entity entity = daoGenerator.getEntity();
        String voSimpleClassName = entity.getVoSimpleClassName();
        String voCanonicalClassName = entity.getVoCanonicalClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName);
        methodGenerator.addParameter(voCanonicalClassName, voVarName);
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

        int index = 1;
        List<String> getterList = new ArrayList<>();
        for (Attribute attribute : entity.getNonAutoIncrementAttributes()) {
            String getter = voVarName + "." + attribute.generateGetterMethodName() + "()";
            String line = PreparedStatementGenerator.generateSetStatement(attribute, getter, index);
            methodGenerator.addCodeLn(line);
            getterList.add(getter);
            index++;
        }

        String logStatement = Strings.listing(getterList, " + \"][\" + ", "CREATE_STATEMENT + \" [\" + ", " + \"]\"");
        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByExpression(logStatement));

        methodGenerator.addCodeLn("preparedStatement.executeUpdate();");

        if (entity.hasAutoIncrementAttribute()) {
            Attribute autoIncrementAttribute = entity.getAutoIncrementAttribute();
            methodGenerator.addCodeLn("ResultSet resultSet = preparedStatement.getGeneratedKeys();");
            methodGenerator.addCodeLn("if (resultSet.next()) {");
            methodGenerator.addCodeLn("int generatedKey = resultSet.getInt(1);");
            methodGenerator.addCodeLn(voVarName + "." + autoIncrementAttribute.generateSetterMethodName()
                    + "(generatedKey);");
            methodGenerator.addCodeLn("} else {");
            methodGenerator.addCodeLn("throw new " + daoGenerator.getConnectionExceptionSimpleClassName()
                    + "(\"Could not obtain generated key for auto increment field.\");");
            methodGenerator.addCodeLn("}");
            methodGenerator.addCodeLn("try { resultSet.close(); } catch (SQLException ignored) {}");
        }

        methodGenerator.addCodeLn("try { preparedStatement.close(); } catch (SQLException ignored) {}");
    }

}
