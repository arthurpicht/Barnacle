package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.ClassGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.LoggerGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.helper.StringHelper;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DaoGenerator extends ClassGenerator {

    private static final Logger logger = LoggerFactory.getLogger("BARNACLE");

    private final Entity entity;
    private final String connectionManagerSimpleClassName;
    private final String connectionExceptionCanonicalClassName;
    private final String connectionExceptionSimpleClassName;
    private final String entityNotFoundExceptionCanonicalClassName;
    private final String entityNotFoundExceptionSimpleClassName;

    public DaoGenerator(Entity entity) {
        super(entity.getDaoCanonicalClassName());

        logger.debug("Assembling class " + entity.getDaoSimpleClassName());

        this.entity = entity;

        String connectionManagerCanonicalClassName = GeneratorContext.getInstance().getGeneratorConfiguration().getConnectionManagerCanonicalClassName();
        this.connectionManagerSimpleClassName = JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(connectionManagerCanonicalClassName);
        this.connectionExceptionCanonicalClassName = getGeneratorConfiguration().getConnectionExceptionCanonicalClassName();
        this.connectionExceptionSimpleClassName = JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(this.connectionExceptionCanonicalClassName);
        this.entityNotFoundExceptionCanonicalClassName = getGeneratorConfiguration().getEntityNotFoundExceptionCanonicalClassName();
        this.entityNotFoundExceptionSimpleClassName = JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(this.entityNotFoundExceptionCanonicalClassName);

        // standard imports
        this.addStandardImports();
        this.addImportsForNonPrimitiveAttributes();

        // standard methods
        DaoGeneratorCreate.addCreateMethod(this);
        DaoGeneratorCreate.addCreateMethodByConnection(this);

        DaoGeneratorCreateBatch.addCreateMethod(this);
        DaoGeneratorCreateBatch.addCreateMethodByConnection(this);

        DaoGeneratorLoad.addPreparedStatementLoadAsLocalConst(this);
        DaoGeneratorLoad.addLoadMethod(this);
        DaoGeneratorLoad.addLoadMethodByConnection(this);

        DaoGeneratorDelete.addPreparedStatementDeleteAsLocalConst(this);
        DaoGeneratorDelete.addDeleteMethod(this);
        DaoGeneratorDelete.addDeleteMethodByConnection(this);

        DaoGeneratorUpdate.addPreparedStatementUpdateAsLocalConst(this);
        DaoGeneratorUpdate.addUpdateMethod(this);
        DaoGeneratorUpdate.addUpdateMethodByConnection(this);

        DaoGeneratorFindAll.addPreparedStatementFindAllAsLocalConst(this);
        DaoGeneratorFindAll.addFindAllMethod(this);
        DaoGeneratorFindAll.addFindAllMethodByConnection(this);

        // one finder method for each foreign key
        for (ForeignKeyWrapper foreignKeyWrapper : this.entity.getAllForeignKeys()) {
            DaoGeneratorFindByForeignKey.addPreparedStatement(foreignKeyWrapper, this);
            DaoGeneratorFindByForeignKey.addFindByFkMethod(foreignKeyWrapper, this);
            DaoGeneratorFindByForeignKey.addFindByForeignKeyMethod(foreignKeyWrapper, this);
        }

        // one finder methode for each unique key
        Set<String> uniqueKeyNameList = this.entity.getAllUniqueIndicesNames();
        for (String uniqueKeyName : uniqueKeyNameList) {
            List<Attribute> uniqueKeyAttributes = this.entity.getAttributesByUniqueIndexName(uniqueKeyName);
            this.addFindByUniqueKeyMethod(uniqueKeyName, uniqueKeyAttributes);
        }

        // two finder methods for association
        // addAssociation method for association
        if (this.entity.isAssociationTable()) {
            this.addGetAssociatedEntites(this.entity.getAssociationForeignKeyA(), this.entity.getAssociationForeignKeyB());
            this.addGetAssociatedEntites(this.entity.getAssociationForeignKeyB(), this.entity.getAssociationForeignKeyA());

            this.addAssociation(this.entity.getAssociationForeignKeyA(), this.entity.getAssociationForeignKeyB());
        }

        this.addGetValueExpressionMethod();
    }

    public Entity getEntity() {
        return this.entity;
    }

    public String getConnectionExceptionCanonicalClassName() {
        return this.connectionExceptionCanonicalClassName;
    }

    public String getConnectionManagerSimpleClassName() {
        return connectionManagerSimpleClassName;
    }

    public String getConnectionExceptionSimpleClassName() {
        return connectionExceptionSimpleClassName;
    }

    public String getEntityNotFoundExceptionCanonicalClassName() {
        return entityNotFoundExceptionCanonicalClassName;
    }

    public String getEntityNotFoundExceptionSimpleClassName() {
        return entityNotFoundExceptionSimpleClassName;
    }

    private void addStandardImports() {
        this.importGenerator.addImport(
                GeneratorContext.getInstance().getGeneratorConfiguration().getConnectionManagerCanonicalClassName());
        this.importGenerator.addImport(List.class);
        this.importGenerator.addImport(ArrayList.class);
        this.importGenerator.addImport(Connection.class);
        this.importGenerator.addImport(Statement.class);
        this.importGenerator.addImport(ResultSet.class);
        this.importGenerator.addImport(SQLException.class);
        this.importGenerator.addImport(PreparedStatement.class);
    }

    private void addImportsForNonPrimitiveAttributes() {
        List<Attribute> attributeList = entity.getAttributes();
        for (Attribute attribute : attributeList) {
            if (!attribute.isPrimitiveType()) {
                this.getImportGenerator().addImport(attribute.getJavaTypeCanonicalName());
            }
        }
    }

    private void addFindByForeignKeyMethod(ForeignKeyWrapper foreignKeyWrapper) {

        List<Attribute> fkAttributes = foreignKeyWrapper.getKeyFieldAttributes();

        String foreignKeyName = foreignKeyWrapper.getForeignKeyName();
        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName);
        String pkSimpleClassName = this.entity.getPkSimpleClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);
        String voListVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName) + "List";

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(this.entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("findBy" + StringHelper.shiftFirstLetterToUpperCase(foreignKeyName));
        for (Attribute fkAttribute : fkAttributes) {
            methodGenerator.addParameter(fkAttribute.getType(), fkAttribute.getFieldName());
        }
        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

//        methodGenerator.addCodeLn("Connection connection = openConnection(" + this.entity.getDaoSimpleClassName() + ".class" + ");");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(createGetConnectionStatement());
        methodGenerator.addCodeLn("Statement statement = null;");
        methodGenerator.addCodeLn("ResultSet resultSet = null;");
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("statement = connection.createStatement();");
        methodGenerator.addCodeLn("String sql = \"SELECT * FROM \" + " + voSimpleClassName + ".TABLENAME + \" WHERE \"");

        boolean sequence = false;
        for (int i=0; i<fkAttributes.size(); i++) {
            Attribute fkAttribute = fkAttributes.get(i);
            if (sequence) {
                methodGenerator.addCodeLn(" + \" AND \"");
            }
//			methodGenerator.addCode("+ " + voSimpleClassName + "." + fkAttribute.getFieldName().toUpperCase()
//					+ " + \" = \" + getValueExpression(" + fkAttribute.getFieldName() + ", \"" + fkAttribute.getSqlDataType() + "\")");			
            methodGenerator.addCode("+ " + voSimpleClassName + "." + fkAttribute.getConstName()
                    + " + \" = \" + getValueExpression(" + fkAttribute.getFieldName() + ", \"" + fkAttribute.getSqlDataType() + "\")");
            sequence = true;
        }
        methodGenerator.addCodeLn(";");

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

        methodGenerator.addCodeLn("resultSet = statement.executeQuery(sql);");
        methodGenerator.addCodeLn("List<" + voSimpleClassName + "> " + voListVarName + " = new ArrayList<" + voSimpleClassName + ">();");

        methodGenerator.addCodeLn("while (resultSet.next()) {");

        // Make for every entity-attribute an assignment from result set to local var
        // except attributes, that were passed to this method.
        List<Attribute> attributes = this.entity.getAttributes();
        for (Attribute attribute : attributes) {
            boolean exceptAttribute = false;
            for (Attribute fkAttribute : fkAttributes) {
                if (fkAttribute.equals(attribute)) {
                    exceptAttribute = true;
                    break;
                }
            }
            if (!exceptAttribute) {
                methodGenerator.addCodeLn(DaoGeneratorCommons.generateLocalVarFromResultSet(this.entity, attribute));
            }
        }

        if (this.entity.isComposedPk()) {
            methodGenerator.addCode(pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(");
            List<Attribute> pkAttributes = this.entity.getPkAttributes();
            sequence = false;
            for (Attribute attribute : pkAttributes) {
                if (sequence) {
                    methodGenerator.addCode(", ");
                }
                methodGenerator.addCode(attribute.getFieldName());
                sequence = true;
            }
            methodGenerator.addCodeLn(");");
            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkVarName + ");");

        } else {
            Attribute pkAttribute = this.entity.getSinglePkAttribute();

            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkAttribute.getFieldName() + ");");
        }

        List<Attribute> nonPkAttributes = this.entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName() + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn(voListVarName + ".add(" + voVarName + ");");

        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("return " + voListVarName + ";");
        methodGenerator.addCodeLn("} catch (SQLException e) {");
        methodGenerator.addCodeLn("throw new " + this.connectionExceptionSimpleClassName + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");

//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    private void addFindByUniqueKeyMethod(String uniqueKeyName, List<Attribute> uniqueKeyAttributeList) {

        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voSimpleClassName);
        String pkSimpleClassName = this.entity.getPkSimpleClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnTypeByCanonicalClassName(this.entity.getVoCanonicalClassName());
//		methodGenerator.setReturnTypeParameter(this.entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("findBy" + StringHelper.shiftFirstLetterToUpperCase(uniqueKeyName));
        for (Attribute uniqueKeyAttribute : uniqueKeyAttributeList) {
            methodGenerator.addParameter(uniqueKeyAttribute.getType(), uniqueKeyAttribute.getFieldName());
        }
        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);
        methodGenerator.addThrowsException(this.entityNotFoundExceptionCanonicalClassName);

//        methodGenerator.addCodeLn("Connection connection = openConnection(" + this.entity.getDaoSimpleClassName() + ".class" + ");");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(createGetConnectionStatement());
        methodGenerator.addCodeLn("Statement statement = null;");
        methodGenerator.addCodeLn("ResultSet resultSet = null;");
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("statement = connection.createStatement();");
        methodGenerator.addCodeLn("String sql = \"SELECT * FROM \" + " + voSimpleClassName + ".TABLENAME + \" WHERE \"");

        boolean sequence = false;
        for (Attribute attribute : uniqueKeyAttributeList) {
            if (sequence) {
                methodGenerator.addCodeLn(" + \" AND \"");
            }
            methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getConstName() + " + \" = \" + getValueExpression(" + attribute.getFieldName() + ", \"" + attribute.getSqlDataType() + "\")");
            sequence = true;
        }
        methodGenerator.addCodeLn(";");

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

        methodGenerator.addCodeLn("resultSet = statement.executeQuery(sql);");
        methodGenerator.addCodeLn("if (resultSet.next()) {");

        List<Attribute> attributes = this.entity.getAttributes();
        for (Attribute attribute : attributes) {
            if (!uniqueKeyAttributeList.contains(attribute)) {
                methodGenerator.addCodeLn(DaoGeneratorCommons.generateLocalVarFromResultSet(this.entity, attribute));
            }
        }

        if (this.entity.isComposedPk()) {

            methodGenerator.addCode(pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(");
            List<Attribute> pkAttributes = this.entity.getPkAttributes();
            sequence = false;
            for (Attribute attribute : pkAttributes) {
                if (sequence) {
                    methodGenerator.addCode(", ");
                }
                methodGenerator.addCode(attribute.getFieldName());
                sequence = true;
            }
            methodGenerator.addCodeLn(");");
            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkVarName + ");");

        } else {
            Attribute pkAttribute = this.entity.getSinglePkAttribute();

            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkAttribute.getFieldName() + ");");
        }

        List<Attribute> nonPkAttributes = this.entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName() + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn("return " + voVarName + ";");
        methodGenerator.addCodeLn("} else {");

        methodGenerator.addCode("throw new " + this.entityNotFoundExceptionSimpleClassName
                + "(" + voSimpleClassName + ".TABLENAME + \"-Entity with unique key '" + uniqueKeyName + "'[");
        sequence = false;
        for (Attribute attribute : uniqueKeyAttributeList) {
            if (sequence) {
                methodGenerator.addCode(" + \", ");
            }
            methodGenerator.addCode(attribute.getFieldName() + " = \" + " + attribute.getFieldName());
            sequence = true;
        }

        methodGenerator.addCodeLn(" + \"] does not exist!\");");
        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("} catch(" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + this.connectionExceptionSimpleClassName + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");
//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");

    }

    private void addGetAssociatedEntites(ForeignKeyWrapper foreignKeyWrapperA, ForeignKeyWrapper foreignKeyWrapperB) {

        Entity sourceEntity = foreignKeyWrapperA.getTargetEntity();
        Entity targetEntity = foreignKeyWrapperB.getTargetEntity();
        String sourceEntityVoCanonicalClassName = sourceEntity.getVoCanonicalClassName();
        String sourceEntityVoSimpleClassName = sourceEntity.getVoSimpleClassName();
        String targetEntityVoCanonicalClassName = targetEntity.getVoCanonicalClassName();
        String targetEntityVoSimpleClassName = targetEntity.getVoSimpleClassName();
        String associationEntityVoSimpleClassName = this.entity.getVoSimpleClassName();
        String voListVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(targetEntityVoSimpleClassName) + "List";
        String pkSimpleClassName = foreignKeyWrapperB.getTargetEntity().getPkSimpleClassName();
        String pkCanonicalClassName = foreignKeyWrapperB.getTargetEntity().getPkCanonicalClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(targetEntityVoSimpleClassName);

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(targetEntityVoCanonicalClassName);

        methodGenerator.setMethodName("getAssociated" + targetEntityVoSimpleClassName);

        String sourceEntityVoVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(sourceEntityVoSimpleClassName);
        methodGenerator.addParameter(sourceEntityVoCanonicalClassName, sourceEntityVoVarName);

        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

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

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

//        methodGenerator.addCodeLn("Connection connection = openConnection(" + this.entity.getDaoSimpleClassName() + ".class" + ");");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(createGetConnectionStatement());
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
        methodGenerator.addCodeLn("throw new " + this.connectionExceptionSimpleClassName + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");
//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");

    }

    private void addAssociation(ForeignKeyWrapper foreignKeyWrapperA, ForeignKeyWrapper foreignKeyWrapperB) {

        Entity entityA = foreignKeyWrapperA.getTargetEntity();
        Entity entityB = foreignKeyWrapperB.getTargetEntity();

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
//		methodGenerator.setReturnType(List.class);

        methodGenerator.setMethodName("addAssociation");

        String entityAVoVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(entityA.getVoSimpleClassName());
        String entityBVoVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(entityB.getVoSimpleClassName());
        methodGenerator.addParameter(entityA.getVoCanonicalClassName(), entityAVoVarName);
        methodGenerator.addParameter(entityB.getVoCanonicalClassName(), entityBVoVarName);

        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

        String pkClassName = this.entity.getPkSimpleClassName();
        String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkClassName);
        methodGenerator.addCode(pkClassName + " " + pkVarName + " = new " + pkClassName + "(");

        List<Attribute> pkAttributes = this.entity.getPkAttributes();
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

        String voClassName = this.entity.getVoSimpleClassName();
        String voVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(voClassName);

        methodGenerator.addCodeLn(voClassName + " " + voVarName + " = new " + voClassName + "(" + pkVarName + ");");
        methodGenerator.addCodeLn(this.entity.getDaoSimpleClassName() + ".create(" + voVarName + ");");
    }

    private void addGetValueExpressionMethod() {

        MethodGenerator methodGenerator = this.getNewMethodGenerator();

        methodGenerator.setAccessibility(MethodGenerator.Accessibility.PRIVATE);
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(String.class);

        methodGenerator.setMethodName("getValueExpression");
        methodGenerator.addParameter(Object.class, "o");
        methodGenerator.addParameter(String.class, "sqlType");

        methodGenerator.addCodeLn("if (o == null) { return \"NULL\"; }");
        methodGenerator.addCodeLn("if (sqlType.startsWith(\"VARCHAR\") || sqlType.equals(\"DATE\")) { return \"'\" + o + \"'\"; }");
        methodGenerator.addCodeLn("return \"\" + o;");
    }

    public String generateVoAssignmentFromResultSet(Entity entity, Attribute attribute) {
        // example: personCompositeVO.setAge(resultSet.getInt("age"));
        return JavaGeneratorHelper.getVoVarName(entity) + "."
                + attribute.generateSetterMethodName() + "(resultSet."
                + TypeMapper.getResultSetGetMethod(attribute.getJavaTypeSimpleName()) + "(\""
                + attribute.getColumnName() + "\"));";
    }

    public String createGetConnectionStatement() {
        return "Connection connection = " + this.connectionManagerSimpleClassName
                + ".openConnection(" + this.entity.getDaoSimpleClassName() + ".class);";
    }

    public String generateReleaseConnectionStatement() {
        return this.connectionManagerSimpleClassName + ".releaseConnection(connection, "
                + this.entity.getDaoSimpleClassName() + ".class);";
    }

    private GeneratorConfiguration getGeneratorConfiguration() {
        return GeneratorContext.getInstance().getGeneratorConfiguration();
    }

}

