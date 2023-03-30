package de.arthurpicht.barnacle.context;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfigurationFactory;
import de.arthurpicht.barnacle.configuration.helper.ConfigurationHelper;
import de.arthurpicht.barnacle.exceptions.BarnacleInitializerException;
import de.arthurpicht.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneratorContext {

    protected final static Logger logger = LoggerFactory.getLogger(GeneratorContext.class);

    private static GeneratorContext generatorContext;

    private final GeneratorConfiguration generatorConfiguration;

    public static GeneratorContext getInstance() {
        if (generatorContext == null) {
            generatorContext = new GeneratorContext();
        }
        return generatorContext;
    }

    public static void invalidate() {
        generatorContext = null;
    }

    private GeneratorContext() {
        BarnacleConfiguration barnacleConfiguration = new BarnacleConfiguration();

        if (!barnacleConfiguration.hasGeneratorConfiguration()) {
            throw new BarnacleInitializerException("No [generator]-Configuration found in barnacle.conf!");
        } else {
            Configuration configuration = barnacleConfiguration.getGeneratorConfiguration();
            this.generatorConfiguration = GeneratorConfigurationFactory.create(configuration);
            logConfig(configuration);
        }
    }

    private void logConfig(Configuration configuration) {
        logger.debug("Barnacle [generator] configuration:");
        ConfigurationHelper.logAllPropertiesOnDebugLevel(configuration, logger);
    }

    public GeneratorConfiguration getGeneratorConfiguration() {
        return this.generatorConfiguration;
    }

}
