package de.arthurpicht.barnacle.context;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.general.GeneralConfiguration;
import de.arthurpicht.barnacle.configuration.general.GeneralConfigurationBuilder;
import de.arthurpicht.barnacle.configuration.general.GeneralConfigurationFactory;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfigurationFactory;
import de.arthurpicht.barnacle.exceptions.BarnacleInitializerException;
import de.arthurpicht.configuration.Configuration;

public class GeneratorContext {

    private static GeneratorContext generatorContext;

    private final GeneratorConfiguration generatorConfiguration;
    private final GeneralConfiguration generalConfiguration;

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
        }

        if (!barnacleConfiguration.hasGeneralConfiguration()) {
            this.generalConfiguration = new GeneralConfigurationBuilder().build();
        } else {
            Configuration configuration = barnacleConfiguration.getGeneralConfiguration();
            this.generalConfiguration = GeneralConfigurationFactory.create(configuration);
        }
    }

    public GeneratorConfiguration getGeneratorConfiguration() {
        return this.generatorConfiguration;
    }

    public GeneralConfiguration getGeneralConfiguration() {
        return this.generalConfiguration;
    }

}
