package de.arthurpicht.barnacle.codeGenerator;

import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.codeGenerator.java.dao.DaoGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.PkGenerator;
import de.arthurpicht.barnacle.codeGenerator.java.VoGenerator;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.EntityRelationshipModel;

public class CodeGenerator {

    public static void execute(GeneratorConfiguration generatorConfiguration, EntityRelationshipModel entityRelationshipModel)
            throws CodeGeneratorException {

        for (Entity entity : entityRelationshipModel.getEntities()) {

            VoGenerator voGenerator = new VoGenerator(entity, entityRelationshipModel, generatorConfiguration);
            voGenerator.generate();

            if (entity.isComposedPk()) {
                PkGenerator pkGenerator = new PkGenerator(entity, generatorConfiguration);
                pkGenerator.generate();
            }

            DaoGenerator daoGenerator = new DaoGenerator(entity, generatorConfiguration);
            daoGenerator.generate();
        }
    }

}
