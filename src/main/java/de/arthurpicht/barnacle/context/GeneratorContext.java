package de.arthurpicht.barnacle.context;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.configuration.GeneratorConfigurationFactory;
import de.arthurpicht.barnacle.exceptions.BarnacleInitializerException;
import de.arthurpicht.configuration.Configuration;

/**
 * Author: Arthur Picht, Düsseldorf and Düren, Germany, 21.02.18.
 */
public class GeneratorContext {

    private static GeneratorContext generatorContext;

    private final GeneratorConfiguration generatorConfiguration;

    public static GeneratorContext getInstance() {
        if (generatorContext == null) {
            generatorContext = new GeneratorContext();
        }
        return generatorContext;
    }

    private GeneratorContext() {
        // Konfiguration holen
        if (!BarnacleConfiguration.hasGeneratorConfiguration()) {
            throw new BarnacleInitializerException("No [generator]-Configuration found in barnacle.conf!");
        }
        Configuration configuration = BarnacleConfiguration.getGeneratorConfiguration();
        this.generatorConfiguration = GeneratorConfigurationFactory.create(configuration);
    }

    public GeneratorConfiguration getGeneratorConfiguration() {
        return this.generatorConfiguration;
    }

}
