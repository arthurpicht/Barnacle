package de.arthurpicht.barnacle;

import de.arthurpicht.barnacle.annotations.Annotations.Barnacle;
import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.exceptions.BarnacleInitializerException;
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

    public enum Dialect {MYSQL, H2}
    public enum Encoding {ISO, UTF, DEFAULT}

//    public static final String VERSION = "Barnacle Version 0.2.1-snapshot (2019.11)";
    public static final String VERSION = "Barnacle Version 0.2.2-SNAPSHOT (2023-02-12)";
    private static final Logger logger = LoggerFactory.getLogger("BARNACLE");
    private static final LoggerTypes loggerType = LoggerTypes.SLF4J;

    public static void process() throws BarnacleInitializerException {

        logger.info(VERSION);

        GeneratorContext generatorContext = GeneratorContext.getInstance();
        GeneratorConfiguration generatorConfiguration = generatorContext.getGeneratorConfiguration();

        EntityCollection entityCollection = new EntityCollection();

        try {

            // Step 0: Check preconditions ... create folders if needed
            PreconditionChecker.check();

            // Step 1: Processing ... Map annotated Value-Object-Field-Files
            // (VOF-Files) to abstract Entitycollection
            // including sql data type determination.

            Class<?>[] classArray = VofClassLoader.getClassesFromPackage(
                    generatorConfiguration.getSrcDir(),
                    generatorConfiguration.getVofPackageName());
            for (Class<?> clazz : classArray) {
                if (clazz.getSimpleName().endsWith("VOF")) {
                    if (clazz.isAnnotationPresent(Barnacle.class)) {
                        Entity entity = VOFProcessorEntityStage.process(clazz, generatorConfiguration);
                        entityCollection.addEntity(entity);
                    }
                }
            }

            // Step 2: Processing VOF-Files again: Stage2.
            // Now determing relations.
            for (Entity entity : entityCollection.getEntities()) {
                VOFProcessorStage2.process(entity, entityCollection);
            }

            // Association-Tables
            for (Entity entity : entityCollection.getEntities()) {
                VOFProcessorStage3.process(entity);
            }

            // Step 3: Validating entities and relations
            for (Entity entity : entityCollection.getEntities()) {
                EntityValidator.validate(entity);
            }
            for (Entity entity : entityCollection.getEntities()) {
                RelationValidator.validate(entity);
            }

            // Step 4:  Generate
            //		- Value-Object-Files (VO-Files)
            //		- DB-Tables (Stage1)
            //		- Data-Access-Object-Files (DAO-Files)
//            System.out.println(EntityCollection.debugOut());

            for (Entity entity : entityCollection.getEntities()) {

                VoGenerator voGenerator = new VoGenerator(entity, entityCollection);
                voGenerator.generate();

                if (entity.isComposedPk()) {
                    PkGenerator pkGenerator = new PkGenerator(entity);
                    pkGenerator.generate();
                }

                DaoGenerator daoGenerator = new DaoGenerator(entity);
                daoGenerator.generate();
            }

            SqlGenerator.generateStage1(entityCollection.getEntities(), generatorConfiguration.getEncodingDB());

            // Step 5: Generate
            //		- Foreign-Key-Constraints

            for (Entity entity : entityCollection.getEntities()) {
                SqlGenerator.generateStage2(entity);
            }

            // Close resources
            SqlGenerator.close();

            logger.info("Barnacle generation successfully completed!");

        } catch (GeneratorException | VofClassLoaderException e) {
            logger.error("GeneratorException", e.fillInStackTrace());
            throw new BarnacleInitializerException(e);
        }
    }

    // TODO Die beiden Parameter in eigene GeneralConfiguration auslagern, so wie GeneratorConfiguration.

	public static LoggerTypes getLoggerType() {
		return loggerType;
	}

}
