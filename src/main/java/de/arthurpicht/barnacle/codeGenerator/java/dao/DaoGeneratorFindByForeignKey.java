package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.helper.StringHelper;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Attributes;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;
import de.arthurpicht.utils.core.strings.Strings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DaoGeneratorFindByForeignKey {

    public static void addPreparedStatement(ForeignKeyWrapper foreignKeyWrapper, DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();

        String statement = "SELECT * FROM " + entity.getTableName() + " WHERE ";
        statement += DaoGeneratorCommons.getPreparedStatementSearchConditionForFk(foreignKeyWrapper);

        String foreignKeyName = foreignKeyWrapper.getForeignKeyName();
        String constName = foreignKeyName.toUpperCase() + "_STATEMENT";
        daoGenerator.getLocalStringConstGenerator().add(constName, statement);
    }

    public static void addFindByFkMethod(ForeignKeyWrapper foreignKeyWrapper, DaoGenerator daoGenerator) {
        Entity entity = daoGenerator.getEntity();

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(entity.getVoCanonicalClassName());

        String methodName = getMethodName(foreignKeyWrapper);
        List<Attribute> fkAttributes = foreignKeyWrapper.getKeyFieldAttributes();
        methodGenerator.setMethodName(methodName);
        methodGenerator.addAsParameters(fkAttributes);
        methodGenerator.addThrowsException(daoGenerator.getConnectionExceptionCanonicalClassName());

        List<String> fkFieldNames = Attributes.getFieldNames(fkAttributes);
        String fkParameter = Strings.listing(fkFieldNames, ", ");
        methodGenerator.addCodeLn(daoGenerator.createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("return " + methodName + "(" + fkParameter + ", connection);");
        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + daoGenerator.getConnectionExceptionSimpleClassName() + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn(daoGenerator.generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }


    public static void addFindByForeignKeyMethod(ForeignKeyWrapper foreignKeyWrapper, DaoGenerator daoGenerator) {

        Entity entity = daoGenerator.getEntity();
        List<Attribute> fkAttributes = foreignKeyWrapper.getKeyFieldAttributes();

        String foreignKeyName = foreignKeyWrapper.getForeignKeyName();
        String voSimpleClassName = entity.getVoSimpleClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName);
        String pkSimpleClassName = entity.getPkSimpleClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);
        String voListVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName) + "List";

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("findBy" + StringHelper.shiftFirstLetterToUpperCase(foreignKeyName));
        methodGenerator.addAsParameters(fkAttributes);
        methodGenerator.addParameter(Connection.class, "connection");
        methodGenerator.addThrowsException(SQLException.class);

        String preparedStatementConstName = foreignKeyName.toUpperCase() + "_STATEMENT";

        methodGenerator.addCodeLn("PreparedStatement preparedStatement " +
                "= connection.prepareStatement(" + preparedStatementConstName + ");");
        int i = 1;
        for (Attribute fkAttribute : fkAttributes) {
            String setMethod = TypeMapper.getPreparedStatementSetMethod(fkAttribute.getJavaTypeSimpleName());
            methodGenerator.addCodeLn("preparedStatement." + setMethod + "(" + i + ", " + fkAttribute.getFieldName() + ");");
            i++;
        }

        String logStatement = Strings.listing(Attributes.getFieldNames(fkAttributes), " + \"][\" + ",
                preparedStatementConstName + " + \" [\" + ", " + \"]\"");
        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByExpression(logStatement));

        methodGenerator.addCodeLn("ResultSet resultSet = preparedStatement.executeQuery();");
        methodGenerator.addCodeLn(
                "List<" + voSimpleClassName + "> " + voListVarName + " = new ArrayList<>();"
        );
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("while (resultSet.next()) {");

        // Make for every entity-attribute an assignment from result set to local var
        // except attributes, that were passed to this method.
        List<Attribute> attributes = entity.getAttributes();
        for (Attribute attribute : attributes) {
            boolean exceptAttribute = false;
            for (Attribute fkAttribute : fkAttributes) {
                if (fkAttribute.equals(attribute)) {
                    exceptAttribute = true;
                    break;
                }
            }
            if (!exceptAttribute) {
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

        methodGenerator.addCodeLn(voListVarName + ".add(" + voVarName + ");");

        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("return " + voListVarName + ";");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException ignored) {}}");
        methodGenerator.addCodeLn("try { preparedStatement.close(); } catch (SQLException ignored) {}");
        methodGenerator.addCodeLn("}");
    }

    private static String getMethodName(ForeignKeyWrapper foreignKeyWrapper) {
        String foreignKeyName = foreignKeyWrapper.getForeignKeyName();
        return "findBy" + StringHelper.shiftFirstLetterToUpperCase(foreignKeyName);
    }

}
