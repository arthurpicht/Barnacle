package de.arthurpicht.barnacle;

import de.arthurpicht.barnacle.annotations.Annotations.Barnacle;
import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.exceptions.BarnacleInititalizerException;
import de.arthurpicht.barnacle.exceptions.GeneratorException;
import de.arthurpicht.barnacle.exceptions.VofClassLoaderException;
import de.arthurpicht.barnacle.generator.java.DaoGenerator;
import de.arthurpicht.barnacle.generator.java.LoggerGenerator.LoggerTypes;
import de.arthurpicht.barnacle.generator.java.PkGenerator;
import de.arthurpicht.barnacle.generator.java.VoGenerator;
import de.arthurpicht.barnacle.generator.sql.Databases;
import de.arthurpicht.barnacle.generator.sql.SqlGenerator;
import de.arthurpicht.barnacle.helper.PreconditionChecker;
import de.arthurpicht.barnacle.mapping.Entity;
import de.arthurpicht.barnacle.mapping.EntityCollection;
import de.arthurpicht.barnacle.processor.*;
import de.arthurpicht.barnacle.vofClassLoader.VofClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


/**
 * Initializer class for Barnacle processing and
 * generating.
 *
 * @author Arthur Picht, 2007 - 2019
 *
 */
public class BarnacleInitializer {

    public static enum Encoding {ISO, UTF, DEFAULT}

    public static final String VERSION = "Barnacle Version 0.2.1-snapshot (2019.11)";

    private static Logger logger = LoggerFactory.getLogger("BARNACLE");

    private static LoggerTypes loggerType = LoggerTypes.SLF4J;

    // FIXME Fest verdrahtet?
    private static Databases database = Databases.MYSQL;

    private static GeneratorConfiguration generatorConfiguration;

    /**
     * Starts Barnacle generator.
     *
     * @throws BarnacleInititalizerException
     */
    public static void process() throws BarnacleInititalizerException {

        logger.info(VERSION);

        GeneratorContext generatorContext = GeneratorContext.getInstance();
        GeneratorConfiguration generatorConfiguration = generatorContext.getGeneratorConfiguration();

        try {

            // Step 0: Check preconditions ... create folders if needed
            PreconditionChecker.check();

            // Step 1: Processing ... Map annotated Value-Object-Field-Files
            // (VOF-Files) to abstract Entitycollection
            // including sql data type determination.

            Class[] classArray = VofClassLoader.getClassesFromPackage(generatorConfiguration.getSrcDir(), generatorConfiguration.getVofPackageName());
            for (Class<?> clazz : classArray) {
                if (clazz.getSimpleName().endsWith("VOF")) {
                    if (clazz.isAnnotationPresent(Barnacle.class)) {
                        VOFProcessorStage1.process(clazz);
                    }
                }
            }

            // Step 2: Processing VOF-Files again: Stage2.
            // Now determing relations.
            Set<Entity> entities = EntityCollection.getEntities();
            for (Entity entity : entities) {
                VOFProcessorStage2.process(entity);
            }

            // Association-Tables
            for (Entity entity : entities) {
                VOFProcessorStage3.process(entity);
            }

            // Step 3: Validating entities and relations
            for (Entity entity : entities) {
                EntityValidator.validate(entity);
            }
            for (Entity entity : entities) {
                RelationValidator.validate(entity);
            }

            // Step 4:  Generate
            //		- Value-Object-Files (VO-Files)
            //		- DB-Tables (Stage1)
            //		- Data-Access-Object-Files (DAO-Files)
            System.out.println(EntityCollection.debugOut());

            for (Entity entity : entities) {

                VoGenerator voGenerator = new VoGenerator(entity);
                voGenerator.generate();

                if (entity.isComposedPk()) {
                    PkGenerator pkGenerator = new PkGenerator(entity);
                    pkGenerator.generate();
                }

                DaoGenerator daoGenerator = new DaoGenerator(entity);
                daoGenerator.generate();
            }

            SqlGenerator.generateStage1(entities, generatorConfiguration.getEncodingDB());

            // Step 5: Generate
            //		- Foreign-Key-Constraints

            for (Entity entity : entities) {
                SqlGenerator.generateStage2(entity);
            }

            // Close resources
            SqlGenerator.close();

            logger.info("Barnacle generation successfully completed!");

        } catch (GeneratorException e) {
            logger.error("GeneratorException", e.fillInStackTrace());
            throw new BarnacleInititalizerException(e);
        } catch (VofClassLoaderException e) {
            logger.error("GeneratorException", e.fillInStackTrace());
            throw new BarnacleInititalizerException(e);
        }
    }

    // TODO Die beiden Parameter in eigene GeneralConfiguration auslagern, so wie GeneratorConfiguration.

	public static LoggerTypes getLoggerType() {
		return loggerType;
	}

	public static Databases getDatabase() {
		return database;
	}

}
