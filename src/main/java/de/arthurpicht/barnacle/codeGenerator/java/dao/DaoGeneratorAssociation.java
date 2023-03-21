package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;
import de.arthurpicht.utils.core.strings.Strings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DaoGeneratorAssociation {

    public static void addPreparedStatement(ForeignKeyWrapper foreignKeyWrapperA, ForeignKeyWrapper foreignKeyWrapperB, DaoGenerator daoGenerator) {

        Entity entity = daoGenerator.getEntity();
        Entity targetEntity = foreignKeyWrapperB.getTargetEntity();
        List<Attribute> targetEntityAttributes = targetEntity.getAttributes();
        List<Attribute> targetFkTargetAttributes = foreignKeyWrapperB.getTargetFieldAttributes();
        List<Attribute> targetFkKeyAttributes = foreignKeyWrapperB.getKeyFieldAttributes();

        String statement = "SELECT ";

        List<String> targetColumnNames = targetEntityAttributes.stream()
                .map(Attribute::getColumnName)
                .collect(Collectors.toList());
        String columnListing
                = Strings.listing(targetColumnNames, ", ", "", "", "t.", "");
        statement += columnListing + " FROM ";

        statement += targetEntity.getTableName() + " AS t, ";
        statement += entity.getTableName() + " AS a WHERE ";

        List<String> conditions = new ArrayList<>();
        for (int i=0; i<targetFkKeyAttributes.size(); i++) {
            Attribute keyAttribute = targetFkKeyAttributes.get(i);
            Attribute targetAttribute = targetFkTargetAttributes.get(i);

            conditions.add("a." + keyAttribute.getColumnName() + " = t." + targetAttribute.getColumnName());
        }
        statement += Strings.listing(conditions, " AND ");

        List<Attribute> sourceFkKeyAttributes = foreignKeyWrapperA.getKeyFieldAttributes();

        statement += " AND ";

        conditions = sourceFkKeyAttributes.stream()
                .map(attribute -> attribute.getColumnName() + " = ?")
                .collect(Collectors.toList());

        statement += Strings.listing(conditions, " AND ");

        String constName = getPreparedStatementName(targetEntity);
        daoGenerator.getLocalStringConstGenerator().add(constName, statement);
    }

    public static void addGetAssociatedEntities(ForeignKeyWrapper foreignKeyWrapperA, ForeignKeyWrapper foreignKeyWrapperB, DaoGenerator daoGenerator) {
        Entity sourceEntity = foreignKeyWrapperA.getTargetEntity();
        Entity targetEntity = foreignKeyWrapperB.getTargetEntity();
        String sourceEntityVoCanonicalClassName = sourceEntity.getVoCanonicalClassName();
        String sourceEntityVoSimpleClassName = sourceEntity.getVoSimpleClassName();
        String targetEntityVoCanonicalClassName = targetEntity.getVoCanonicalClassName();

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(targetEntityVoCanonicalClassName);
        methodGenerator.setMethodName(getMethodName(targetEntity));
        String sourceEntityVoVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(sourceEntityVoSimpleClassName);
        methodGenerator.addParameter(sourceEntityVoCanonicalClassName, sourceEntityVoVarName);
        methodGenerator.addThrowsException(daoGenerator.getConnectionExceptionCanonicalClassName());

        methodGenerator.addCodeLn(daoGenerator.createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("return " + getMethodName(targetEntity) + "(" + sourceEntityVoVarName + ", connection);");
        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + daoGenerator.getConnectionExceptionSimpleClassName() + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn(daoGenerator.generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    private static String getPreparedStatementName(Entity targetEntity) {
        return "ASSOC_" + targetEntity.getTableName().toUpperCase() + "_STATEMENT";
    }

    private static String getMethodName(Entity targetEntity) {
        return "getAssociated" + targetEntity.getVoSimpleClassName();
    }

    public static void addGetAssociatedEntitiesByConnection(ForeignKeyWrapper foreignKeyWrapperA, ForeignKeyWrapper foreignKeyWrapperB, DaoGenerator daoGenerator) {
        Entity sourceEntity = foreignKeyWrapperA.getTargetEntity();
        Entity targetEntity = foreignKeyWrapperB.getTargetEntity();
        String sourceEntityVoCanonicalClassName = sourceEntity.getVoCanonicalClassName();
        String sourceEntityVoSimpleClassName = sourceEntity.getVoSimpleClassName();
        String targetEntityVoCanonicalClassName = targetEntity.getVoCanonicalClassName();
        String targetEntityVoSimpleClassName = targetEntity.getVoSimpleClassName();
        String voListVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(targetEntityVoSimpleClassName) + "List";
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(targetEntityVoSimpleClassName);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(targetEntityVoCanonicalClassName);
        methodGenerator.setMethodName(getMethodName(targetEntity));
        String sourceEntityVoVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(sourceEntityVoSimpleClassName);
        methodGenerator.addParameter(sourceEntityVoCanonicalClassName, sourceEntityVoVarName);
        methodGenerator.addParameter(Connection.class, "connection");
        methodGenerator.addThrowsException(SQLException.class);

        String preparedStatementConstName = getPreparedStatementName(targetEntity);
        methodGenerator.addCodeLn("PreparedStatement preparedStatement " +
                "= connection.prepareStatement(" + preparedStatementConstName + ");");

        List<Attribute> sourceFkTargetAttributes = foreignKeyWrapperA.getTargetFieldAttributes();
        List<Attribute> sourceFkKeyAttributes = foreignKeyWrapperA.getKeyFieldAttributes();

        List<String> valueList = new ArrayList<>();
        for (int i=0; i<sourceFkKeyAttributes.size(); i++) {
            Attribute targetAttribute = sourceFkTargetAttributes.get(i);
            methodGenerator.addCodeLn(
                    DaoGeneratorCommons.generatePreparedStatementSetterFromVO(i+1, sourceEntity, targetAttribute, valueList)
            );
        }

        String loggingString
                = DaoGeneratorCommons.generateLogStringForPreparedStatement(
                getPreparedStatementName(targetEntity), valueList
        );
        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByExpression(loggingString));

        methodGenerator.addCodeLn("ResultSet resultSet = preparedStatement.executeQuery();");
        methodGenerator.addCodeLn(
                "List<" + targetEntityVoSimpleClassName + "> " + voListVarName + " = new ArrayList<>();"
        );
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("while (resultSet.next()) {");

        for (Attribute attribute : targetEntity.getAttributes()) {
            methodGenerator.addCodeLn(DaoGeneratorCommons.generateLocalVarFromResultSet(targetEntity, attribute));
        }

        if (targetEntity.isComposedPk()) {
            daoGenerator.getImportGenerator().addImport(targetEntity.getPkCanonicalClassName());
            String pkSimpleClassName = targetEntity.getPkSimpleClassName();
            String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);
            methodGenerator.addCode(pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(");
            List<Attribute> pkAttributes = targetEntity.getPkAttributes();
            List<String> attributeFieldNames = pkAttributes.stream()
                    .map(Attribute::getFieldName)
                    .collect(Collectors.toList());
            String fieldNameListing = Strings.listing(attributeFieldNames, ", ");
            methodGenerator.addCode(fieldNameListing);
            methodGenerator.addCodeLn(");");
            methodGenerator.addCodeLn(targetEntityVoSimpleClassName + " " + voVarName + " = new " + targetEntityVoSimpleClassName
                    + "(" + pkVarName + ");");

        } else {
            Attribute pkAttribute = targetEntity.getSinglePkAttribute();
            methodGenerator.addCodeLn(targetEntityVoSimpleClassName + " " + voVarName + " = new " + targetEntityVoSimpleClassName
                    + "(" + pkAttribute.getFieldName() + ");");
        }

        List<Attribute> nonPkAttributes = targetEntity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName()
                    + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn(voListVarName + ".add(" + voVarName + ");");
        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("return " + voListVarName + ";");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException ignored) {}}");
        methodGenerator.addCodeLn("try { preparedStatement.close(); } catch (SQLException ignored) {}");
        methodGenerator.addCodeLn("}");
    }

    public static void addAssociation(ForeignKeyWrapper foreignKeyWrapperA, ForeignKeyWrapper foreignKeyWrapperB, DaoGenerator daoGenerator) {

        Entity entity = daoGenerator.getEntity();
        Entity entityA = foreignKeyWrapperA.getTargetEntity();
        Entity entityB = foreignKeyWrapperB.getTargetEntity();

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);

        methodGenerator.setMethodName("addAssociation");

        String entityAVoVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(entityA.getVoSimpleClassName());
        String entityBVoVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(entityB.getVoSimpleClassName());
        methodGenerator.addParameter(entityA.getVoCanonicalClassName(), entityAVoVarName);
        methodGenerator.addParameter(entityB.getVoCanonicalClassName(), entityBVoVarName);

        methodGenerator.addThrowsException(daoGenerator.getConnectionExceptionCanonicalClassName());

        String pkClassName = entity.getPkSimpleClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkClassName);
        methodGenerator.addCode(pkClassName + " " + pkVarName + " = new " + pkClassName + "(");

        List<Attribute> pkAttributes = entity.getPkAttributes();
        boolean sequence = false;
        for (Attribute pkAttribute : pkAttributes) {
            if (sequence) {
                methodGenerator.addCode(", ");
            }
            Attribute referencedAttribute = foreignKeyWrapperA.getReferencedFieldAttributeByReferencingFieldAttribute(pkAttribute);
            if (referencedAttribute != null) {
                methodGenerator.addCode(entityAVoVarName + "." + referencedAttribute.generateGetterMethodName() + "()");
            } else {
                referencedAttribute = foreignKeyWrapperB.getReferencedFieldAttributeByReferencingFieldAttribute(pkAttribute);
                methodGenerator.addCode(entityBVoVarName + "." + referencedAttribute.generateGetterMethodName() + "()");
            }
            sequence = true;
        }

        methodGenerator.addCodeLn(");");

        String voClassName = entity.getVoSimpleClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voClassName);

        methodGenerator.addCodeLn(voClassName + " " + voVarName + " = new " + voClassName + "(" + pkVarName + ");");
        methodGenerator.addCodeLn(entity.getDaoSimpleClassName() + ".create(" + voVarName + ");");
    }

}
