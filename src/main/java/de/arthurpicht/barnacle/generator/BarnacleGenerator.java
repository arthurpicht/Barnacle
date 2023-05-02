package de.arthurpicht.barnacle.generator;

import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.codeGenerator.CodeGenerator;
import de.arthurpicht.barnacle.codeGenerator.sql.SchemaGenerator;
import de.arthurpicht.barnacle.codeGenerator.sql.SqlDbExecutor;
import de.arthurpicht.barnacle.codeGenerator.sql.SqlScriptWriter;
import de.arthurpicht.barnacle.codeGenerator.sql.SqlStatements;
import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.configurationFile.BarnacleConfigurationFile;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.exceptions.BarnacleInitializerException;
import de.arthurpicht.barnacle.exceptions.VofClassLoaderException;
import de.arthurpicht.barnacle.model.ERMBuilderException;
import de.arthurpicht.barnacle.model.EntityRelationshipModel;
import de.arthurpicht.barnacle.model.EntityRelationshipModelBuilder;
import de.arthurpicht.barnacle.vofClassLoader.VofClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarnacleGenerator {

    private static final Logger logger = LoggerFactory.getLogger(BarnacleGenerator.class);

    public static void process() {
        BarnacleConfigurationFile barnacleConfigurationFile = new BarnacleConfigurationFile();
        BarnacleConfiguration barnacleConfiguration = barnacleConfigurationFile.getBarnacleConfiguration();
        if (barnacleConfiguration.hasGeneratorConfiguration()) {
            process(barnacleConfiguration);
        } else {
            throw new BarnacleInitializerException("No generator configuration found in barnacle configuration file.");
        }
    }

    public static void process(Path configurationFile) {
        BarnacleConfigurationFile barnacleConfigurationFile = new BarnacleConfigurationFile(configurationFile);
        BarnacleConfiguration barnacleConfiguration = barnacleConfigurationFile.getBarnacleConfiguration();
        if (barnacleConfiguration.hasGeneratorConfiguration()) {
            process(barnacleConfiguration);
        } else {
            throw new BarnacleInitializerException("No generator configuration found in configuration file " +
                    "[" + configurationFile.toAbsolutePath() + "].");
        }
    }

    public static void process(BarnacleConfiguration barnacleConfiguration) {
        logger.info(Const.VERSION);
        GeneratorConfiguration generatorConfiguration = barnacleConfiguration.getGeneratorConfiguration();
        try {
            GeneratorPreconditions.assure(generatorConfiguration);
            List<Class<?>> classList = loadVofClasses(generatorConfiguration);
            EntityRelationshipModel entityRelationshipModel
                    = EntityRelationshipModelBuilder.execute(generatorConfiguration, classList);
            logger.trace(entityRelationshipModel.debugOut());

            CodeGenerator.execute(generatorConfiguration, entityRelationshipModel);
            SqlStatements sqlStatements = SchemaGenerator.execute(generatorConfiguration, entityRelationshipModel);

            if (generatorConfiguration.isCreateScript()) {
                Path scriptFile = Paths.get(generatorConfiguration.getScriptFile());
                SqlScriptWriter.write(scriptFile, sqlStatements);
            }

            if (generatorConfiguration.isExecuteOnDb()) {
                SqlDbExecutor.execute(barnacleConfiguration, sqlStatements);
            }

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
