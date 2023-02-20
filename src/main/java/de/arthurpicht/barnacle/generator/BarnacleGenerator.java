package de.arthurpicht.barnacle.generator;

import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.codeGenerator.CodeGenerator;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.exceptions.VofClassLoaderException;
import de.arthurpicht.barnacle.model.ERMBuilderException;
import de.arthurpicht.barnacle.model.EntityRelationshipModel;
import de.arthurpicht.barnacle.model.EntityRelationshipModelBuilder;
import de.arthurpicht.barnacle.vofClassLoader.VofClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarnacleGenerator {

    private static final Logger logger = LoggerFactory.getLogger("BARNACLE");

    public static void process() {

        logger.info(Const.VERSION);

        GeneratorContext generatorContext = GeneratorContext.getInstance();
        GeneratorConfiguration generatorConfiguration = generatorContext.getGeneratorConfiguration();

        try {
            GeneratorPreconditions.assure(generatorConfiguration);
            List<Class<?>> classList = loadVofClasses(generatorConfiguration);
            EntityRelationshipModel entityRelationshipModel
                    = EntityRelationshipModelBuilder.execute(generatorConfiguration, classList);
            logger.trace(entityRelationshipModel.debugOut());

            CodeGenerator.execute(entityRelationshipModel, generatorConfiguration);
            logger.info("Barnacle generation successfully completed!");

        } catch (GeneratorException e) {
            logger.error("Error on barnacle generation: " + e.getMessage(), e.fillInStackTrace());
            throw e;
        }
    }

    private static List<Class<?>> loadVofClasses(GeneratorConfiguration generatorConfiguration) {
        try {
            Class<?>[] classes = VofClassLoader.getClassesFromPackage(
                    generatorConfiguration.getSrcDir(),
                    generatorConfiguration.getVofPackageName());
            return new ArrayList<>(Arrays.asList(classes));
        } catch (VofClassLoaderException e) {
            throw new ERMBuilderException(e.getMessage(), e);
        }
    }

}
