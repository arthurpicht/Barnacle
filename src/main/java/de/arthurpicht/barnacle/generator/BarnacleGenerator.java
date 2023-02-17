package de.arthurpicht.barnacle.generator;

import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.codeGenerator.CodeGenerator;
import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.model.EntityRelationshipModel;
import de.arthurpicht.barnacle.model.EntityRelationshipModelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BarnacleGenerator {

    private static final Logger logger = LoggerFactory.getLogger("BARNACLE");

    public static void process() {

        logger.info(Const.VERSION);

        GeneratorContext generatorContext = GeneratorContext.getInstance();
        GeneratorConfiguration generatorConfiguration = generatorContext.getGeneratorConfiguration();

        try {
            GeneratorPreconditions.assure(generatorConfiguration);

            EntityRelationshipModel entityRelationshipModel
                    = EntityRelationshipModelBuilder.execute(generatorConfiguration);
            logger.trace(entityRelationshipModel.debugOut());

            CodeGenerator.execute(entityRelationshipModel, generatorConfiguration);
            logger.info("Barnacle generation successfully completed!");

        } catch (GeneratorException e) {
            logger.error("Error on barnacle generation: " + e.getMessage(), e.fillInStackTrace());
            throw e;
        }
    }

}
