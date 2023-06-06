package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.EntityRelationshipModel;
import de.arthurpicht.console.Console;

public class SchemaGenerator {

    public static SqlStatements execute(GeneratorConfiguration generatorConfiguration, EntityRelationshipModel entityRelationshipModel) {
        Console.verbose("Generate SQL schema.");

        Const.Dialect dialect = generatorConfiguration.getDialect();
        StatementGenerator statementGenerator = StatementGenerator.getInstance(dialect);
        SqlGenerator sqlGenerator = new SqlGenerator(statementGenerator, generatorConfiguration);
        sqlGenerator.generateBareEntities(entityRelationshipModel.getEntities(), generatorConfiguration.getEncodingDB());

        for (Entity entity : entityRelationshipModel.getEntities()) {
            sqlGenerator.generateForeignKeys(entity);
        }

        return sqlGenerator.getSqlStatements();
    }

}
