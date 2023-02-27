package de.arthurpicht.barnacle.codeGenerator;

import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.codeGenerator.sql.StatementGenerator;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.codeGenerator.java.dao.DaoGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.PkGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.VoGenerator;
import de.arthurpicht.barnacle.codeGenerator.sql.SqlGenerator;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.EntityRelationshipModel;

public class CodeGenerator {

    public static void execute(EntityRelationshipModel entityRelationshipModel, GeneratorConfiguration generatorConfiguration)
            throws CodeGeneratorException {

        Const.Dialect dialect = generatorConfiguration.getDialect();
        StatementGenerator statementGenerator = StatementGenerator.getInstance(dialect);

        for (Entity entity : entityRelationshipModel.getEntities()) {

            VoGenerator voGenerator = new VoGenerator(entity, entityRelationshipModel);
            voGenerator.generate();

            if (entity.isComposedPk()) {
                PkGenerator pkGenerator = new PkGenerator(entity);
                pkGenerator.generate();
            }

            DaoGenerator daoGenerator = new DaoGenerator(entity);
            daoGenerator.generate();
        }

        SqlGenerator sqlGenerator = new SqlGenerator(statementGenerator, generatorConfiguration);
        sqlGenerator.generateBareEntities(entityRelationshipModel.getEntities(), generatorConfiguration.getEncodingDB());

        // Step 5: Generate
        //		- Foreign-Key-Constraints
        for (Entity entity : entityRelationshipModel.getEntities()) {
            sqlGenerator.generateForeignKeys(entity);
        }

        // Close resources
        sqlGenerator.close();
    }

}
