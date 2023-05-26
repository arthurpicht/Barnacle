package de.arthurpicht.barnacle.codeGenerator.java.dao;

import de.arthurpicht.barnacle.codeGenerator.java.ClassGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;
import de.arthurpicht.console.Console;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.arthurpicht.barnacle.helper.ConsoleHelper.verbose;

public class DaoGenerator extends ClassGenerator {

    private final Entity entity;
    private final String connectionManagerSimpleClassName;
    private final String connectionExceptionCanonicalClassName;
    private final String connectionExceptionSimpleClassName;
    private final String entityNotFoundExceptionCanonicalClassName;
    private final String entityNotFoundExceptionSimpleClassName;

    public DaoGenerator(Entity entity, GeneratorConfiguration generatorConfiguration) {
        super(entity.getDaoCanonicalClassName(), generatorConfiguration);

        Console.out(verbose("Generating DAO class [" + entity.getDaoCanonicalClassName() + "]."));

        this.entity = entity;

        String connectionManagerCanonicalClassName = generatorConfiguration.getConnectionManagerCanonicalClassName();
        this.connectionManagerSimpleClassName
                = JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(connectionManagerCanonicalClassName);
        this.connectionExceptionCanonicalClassName
                = generatorConfiguration.getConnectionExceptionCanonicalClassName();
        this.connectionExceptionSimpleClassName
                = JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(this.connectionExceptionCanonicalClassName);
        this.entityNotFoundExceptionCanonicalClassName
                = generatorConfiguration.getEntityNotFoundExceptionCanonicalClassName();
        this.entityNotFoundExceptionSimpleClassName
                = JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(this.entityNotFoundExceptionCanonicalClassName);

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
            DaoGeneratorFindByUniqueKey.addPreparedStatement(uniqueKeyName, uniqueKeyAttributes, this);
            DaoGeneratorFindByUniqueKey.addFindByUniqueKeyMethod(uniqueKeyName, uniqueKeyAttributes, this);
            DaoGeneratorFindByUniqueKey.addFindByUkConnectionMethod(uniqueKeyName, uniqueKeyAttributes, this);
        }

        // two finder methods for association
        // addAssociation method for association
        if (this.entity.isAssociationTable()) {
            DaoGeneratorAssociation.addPreparedStatement(this.entity.getAssociationForeignKeyA(), this.entity.getAssociationForeignKeyB(), this);
            DaoGeneratorAssociation.addPreparedStatement(this.entity.getAssociationForeignKeyB(), this.entity.getAssociationForeignKeyA(), this);

            DaoGeneratorAssociation.addGetAssociatedEntities(this.entity.getAssociationForeignKeyA(), this.entity.getAssociationForeignKeyB(), this);
            DaoGeneratorAssociation.addGetAssociatedEntitiesByConnection(this.entity.getAssociationForeignKeyA(), this.entity.getAssociationForeignKeyB(), this);

            DaoGeneratorAssociation.addGetAssociatedEntities(this.entity.getAssociationForeignKeyB(), this.entity.getAssociationForeignKeyA(), this);
            DaoGeneratorAssociation.addGetAssociatedEntitiesByConnection(this.entity.getAssociationForeignKeyB(), this.entity.getAssociationForeignKeyA(), this);

            DaoGeneratorAssociation.addAssociation(this.entity.getAssociationForeignKeyA(), this.entity.getAssociationForeignKeyB(), this);
        }
    }

    public Entity getEntity() {
        return this.entity;
    }

    public String getConnectionExceptionCanonicalClassName() {
        return this.connectionExceptionCanonicalClassName;
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
        this.importGenerator.addImport(this.generatorConfiguration.getConnectionManagerCanonicalClassName());
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

}
