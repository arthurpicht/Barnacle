package de.arthurpicht.barnacle.codeGenerator;

import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.codeGenerator.java.DaoGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.PkGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.VoGenerator;
import de.arthurpicht.barnacle.codeGenerator.sql.SqlGenerator;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.EntityRelationshipModel;

public class CodeGenerator {

    public static void execute(EntityRelationshipModel entityRelationshipModel, GeneratorConfiguration generatorConfiguration)
            throws CodeGeneratorException {

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

        SqlGenerator.generateStage1(entityRelationshipModel.getEntities(), generatorConfiguration.getEncodingDB());

        // Step 5: Generate
        //		- Foreign-Key-Constraints
        for (Entity entity : entityRelationshipModel.getEntities()) {
            SqlGenerator.generateStage2(entity);
        }

        // Close resources
        SqlGenerator.close();
    }

}
