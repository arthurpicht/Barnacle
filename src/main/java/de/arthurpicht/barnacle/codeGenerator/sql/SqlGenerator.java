package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.Const.Encoding;
import de.arthurpicht.barnacle.codeGenerator.CodeGeneratorException;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SqlGenerator {

    private final StatementGenerator statementGenerator;
    private final SqlStatementCache sqlStatementCache;

    public SqlGenerator(StatementGenerator statementGenerator, GeneratorConfiguration generatorConfiguration) {
        this.statementGenerator = statementGenerator;
        this.sqlStatementCache = new SqlStatementCache();
    }

    public void generateBareEntities(List<Entity> entities, Encoding encoding) {
        // pre drop, e.g. turning off foreign key checks
        preDrop();

        // drop all tables
        // Do this first, not to leave foreign key references from left tables
        // to dropped tables. If so, a new table with same name can not be created,
        // although 'foreign key checks' is turned off.
        for (Entity entity : entities) {
            dropEntity(entity);
        }

        // post drop, e.g. turning on again foreign key checks
        postDrop();

        // create table, columns, and unique keys, set encoding
        for (Entity entity : entities) {
            generateBareEntities(entity, encoding);
        }

    }

    private void preDrop() {
        String[] sqlStatements = this.statementGenerator.deactivateForeignKeyChecks();
        this.sqlStatementCache.add(sqlStatements);
    }

    private void postDrop() {
        String[] sqlStatements = this.statementGenerator.activateForeignKeyChecks();
        this.sqlStatementCache.add(sqlStatements);
    }

    private void dropEntity(Entity entity) {
        String sqlStatement = this.statementGenerator.dropTableIfExists(entity.getTableName());
        this.sqlStatementCache.add(sqlStatement);
    }

    private void generateBareEntities(Entity entity, Encoding encoding) {
        // create table
        String sqlStatement = this.statementGenerator.createTable(entity.getTableName());
        this.sqlStatementCache.add(sqlStatement);

        // create columns
        List<Attribute> attributeList = entity.getAttributes();
        for (Attribute attribute : attributeList) {

            boolean isNotNull = attribute.isNotNull() || attribute.isPrimaryKey();
            sqlStatement = this.statementGenerator.addColumn(
                    entity.getTableName(),
                    attribute.getColumnName(),
                    attribute.getSqlDataType(),
                    attribute.getDefaultValue(),
                    isNotNull);
            this.sqlStatementCache.add(sqlStatement);
        }

        // remove temporary column
        sqlStatement = this.statementGenerator.dropTempColumn(entity.getTableName());
        this.sqlStatementCache.add(sqlStatement);

        // primary keys
        attributeList = entity.getPkAttributes();
        List<String> pkColumnNames = new ArrayList<>();
        for (Attribute attribute : attributeList) {
            pkColumnNames.add(attribute.getColumnName());
        }

        sqlStatement = this.statementGenerator.addPrimaryKey(entity.getTableName(), pkColumnNames);
        this.sqlStatementCache.add(sqlStatement);

        // unique keys
        Set<String> indexNames = entity.getAllUniqueIndicesNames();
        for (String indexName : indexNames) {
            attributeList = entity.getAttributesByUniqueIndexName(indexName);
            List<String> uniqueColumnNames = new ArrayList<>();
            for (Attribute attribute : attributeList) {
                uniqueColumnNames.add(attribute.getColumnName());
            }

            sqlStatement = this.statementGenerator.addUniqueKey(entity.getTableName(), indexName, uniqueColumnNames);
            this.sqlStatementCache.add(sqlStatement);
        }

        // auto increment
        if (entity.hasAutoIncrementAttribute()) {
            Attribute attribute = entity.getAutoIncrementAttribute();
            sqlStatement = this.statementGenerator.addAutoIncrement(entity.getTableName(), attribute.getColumnName());
            this.sqlStatementCache.add(sqlStatement);
        }

        // configure encoding
        sqlStatement = this.statementGenerator.configureEncoding(entity.getTableName(), encoding);
        if (sqlStatement != null && !sqlStatement.equals("")) {
            this.sqlStatementCache.add(sqlStatement);
        }
    }

    public void generateForeignKeys(Entity entity) throws CodeGeneratorException {
        Set<String> foreignKeyNames = entity.getAllForeignKeyNames();
        for (String foreignKeyName : foreignKeyNames) {
            ForeignKeyWrapper foreignKeyWrapper = entity.getOrCreateForeignKeyByName(foreignKeyName);

            String sqlStatement = this.statementGenerator.addForeignKey(entity.getTableName(), foreignKeyName, foreignKeyWrapper);
            this.sqlStatementCache.add(sqlStatement);
        }
    }

    public SqlStatements getSqlStatements() {
        return this.sqlStatementCache.getSqlStatements();
    }

}
