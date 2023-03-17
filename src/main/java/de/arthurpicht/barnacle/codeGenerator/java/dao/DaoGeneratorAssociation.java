package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;

import java.util.List;

public class DaoGeneratorAssociation {

    public static void addGetAssociatedEntities(ForeignKeyWrapper foreignKeyWrapperA, ForeignKeyWrapper foreignKeyWrapperB, DaoGenerator daoGenerator) {

        Entity entity = daoGenerator.getEntity();
        Entity sourceEntity = foreignKeyWrapperA.getTargetEntity();
        Entity targetEntity = foreignKeyWrapperB.getTargetEntity();
        String sourceEntityVoCanonicalClassName = sourceEntity.getVoCanonicalClassName();
        String sourceEntityVoSimpleClassName = sourceEntity.getVoSimpleClassName();
        String targetEntityVoCanonicalClassName = targetEntity.getVoCanonicalClassName();
        String targetEntityVoSimpleClassName = targetEntity.getVoSimpleClassName();
        String associationEntityVoSimpleClassName = entity.getVoSimpleClassName();
        String voListVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(targetEntityVoSimpleClassName) + "List";
        String pkSimpleClassName = foreignKeyWrapperB.getTargetEntity().getPkSimpleClassName();
        String pkCanonicalClassName = foreignKeyWrapperB.getTargetEntity().getPkCanonicalClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(targetEntityVoSimpleClassName);

        MethodGenerator methodGenerator = daoGenerator.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(targetEntityVoCanonicalClassName);

        methodGenerator.setMethodName("getAssociated" + targetEntityVoSimpleClassName);

        String sourceEntityVoVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(sourceEntityVoSimpleClassName);
        methodGenerator.addParameter(sourceEntityVoCanonicalClassName, sourceEntityVoVarName);

        methodGenerator.addThrowsException(daoGenerator.getConnectionExceptionCanonicalClassName());

        List<Attribute> targetEntityAttributes = foreignKeyWrapperB.getTargetEntity().getAttributes();
        List<Attribute> targetFkTargetAttributes = foreignKeyWrapperB.getTargetFieldAttributes();
        List<Attribute> targetFkKeyAttributes = foreignKeyWrapperB.getKeyFieldAttributes();

        methodGenerator.addCode("String sql = \"SELECT ");
        boolean sequence = false;

        for (int i=0; i<targetEntityAttributes.size(); i++) {
            Attribute attribute = targetEntityAttributes.get(i);
            if (sequence) {
                methodGenerator.addCode(" + \", ");
            }
            methodGenerator.addCode("t.\" + " + targetEntityVoSimpleClassName + "." + attribute.getConstName());
//					+ " + \" = \" + getValueExpression(" + fkAttribute.getFieldName() + ", \"" + fkAttribute.getSqlDataType() + "\")");
            sequence = true;
        }
        methodGenerator.addLn();

        methodGenerator.addCodeLn("+ \" FROM \" + "
                + targetEntityVoSimpleClassName + ".TABLENAME + \" AS t, \" + "
                + associationEntityVoSimpleClassName + ".TABLENAME + \" AS a\"");

        methodGenerator.addCode("+ \" WHERE \" ");
        sequence = false;
        for (int i=0; i<targetFkKeyAttributes.size(); i++) {
            Attribute keyAttribute = targetFkKeyAttributes.get(i);
            Attribute targetAttribute = targetFkTargetAttributes.get(i);
            if (sequence) {
                methodGenerator.addCodeLn(" + \" AND \" ");
            }
            methodGenerator.addCode("+ \"a.\" + " + associationEntityVoSimpleClassName + "." + keyAttribute.getConstName()
                    + " + \" = \" + \"t.\" + " + targetEntityVoSimpleClassName + "." + targetAttribute.getConstName());
            sequence = true;
        }

        List<Attribute> sourceFkTargetAttributes = foreignKeyWrapperA.getTargetFieldAttributes();
        List<Attribute> sourceFkKeyAttributes = foreignKeyWrapperA.getKeyFieldAttributes();

        methodGenerator.addCodeLn(" + \" AND \" ");
        sequence = false;
        for (int i=0; i<sourceFkKeyAttributes.size(); i++) {
            Attribute keyAttribute = sourceFkKeyAttributes.get(i);
            Attribute targetAttribute = sourceFkTargetAttributes.get(i);
            if (sequence) {
                methodGenerator.addCodeLn(" + \" AND \" ");
            }
            methodGenerator.addCode("+ \"a.\" + " + associationEntityVoSimpleClassName + "." + keyAttribute.getConstName()
                    + " + \" = \" + getValueExpression(" + sourceEntityVoVarName + "." + targetAttribute.generateGetterMethodName() + "(), \"" + targetAttribute.getSqlDataType() + "\")");
            sequence = true;
        }

        methodGenerator.addCodeLn(";");

        LoggerGenerator loggerGenerator = daoGenerator.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

//        methodGenerator.addCodeLn("Connection connection = openConnection(" + this.entity.getDaoSimpleClassName() + ".class" + ");");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(daoGenerator.createGetConnectionStatement());
        methodGenerator.addCodeLn("Statement statement = null;");
        methodGenerator.addCodeLn("ResultSet resultSet = null;");
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("statement = connection.createStatement();");
        methodGenerator.addCodeLn("resultSet = statement.executeQuery(sql);");

        methodGenerator.addCodeLn("List<" + targetEntityVoSimpleClassName + "> " + voListVarName + " = new ArrayList<" + targetEntityVoSimpleClassName + ">();");

        methodGenerator.addCodeLn("while (resultSet.next()) {");

        // Make for every entity-attribute an assignment from result set to local var
        List<Attribute> attributes = targetEntity.getAttributes();
        for (Attribute attribute : attributes) {
            methodGenerator.addCodeLn(DaoGeneratorCommons.generateLocalVarFromResultSet(targetEntity, attribute));
        }

        if (targetEntity.isComposedPk()) {
            methodGenerator.addImport(pkCanonicalClassName);
            methodGenerator.addCode(pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(");
            List<Attribute> pkAttributes = targetEntity.getPkAttributes();
            sequence = false;
            for (Attribute attribute : pkAttributes) {
                if (sequence) {
                    methodGenerator.addCode(", ");
                }
                methodGenerator.addCode(attribute.getFieldName());
                sequence = true;
            }
            methodGenerator.addCodeLn(");");
            methodGenerator.addCodeLn(targetEntityVoSimpleClassName + " " + voVarName + " = new " + targetEntityVoSimpleClassName + "(" + pkVarName + ");");

        } else {
            Attribute pkAttribute = targetEntity.getSinglePkAttribute();

            methodGenerator.addCodeLn(targetEntityVoSimpleClassName + " " + voVarName + " = new " + targetEntityVoSimpleClassName + "(" + pkAttribute.getFieldName() + ");");
        }

        List<Attribute> nonPkAttributes = targetEntity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName() + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn(voListVarName + ".add(" + voVarName + ");");

        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("return " + voListVarName + ";");


        methodGenerator.addCodeLn("} catch (SQLException e) {");
        methodGenerator.addCodeLn("throw new " + daoGenerator.getConnectionExceptionSimpleClassName() + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");
//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(daoGenerator.generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

}
