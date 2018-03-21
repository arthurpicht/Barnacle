package de.arthurpicht.barnacle.context;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.exceptions.BarnacleInititalizerException;
import de.arthurpicht.configuration.Configuration;

/**
 * Author: Arthur Picht, Düsseldorf and Düren, Germany, 21.02.18.
 */
public class GeneratorContext {

    private static GeneratorContext generatorContext;

    private GeneratorConfiguration generatorConfiguration;

    public static GeneratorContext getInstance() {
        if (generatorContext == null) {
            generatorContext = new GeneratorContext();
        }
        return generatorContext;
    }

    private GeneratorContext() {
        // Konfiguration holen
        if (!BarnacleConfiguration.hasGeneratorConfiguration()) {
            throw new BarnacleInititalizerException("No [generator]-Configuration found in barnacle.conf!");
        }
        Configuration configuration = BarnacleConfiguration.getGeneratorConfiguration();
        this.generatorConfiguration = new GeneratorConfiguration(configuration);
    }

    public GeneratorConfiguration getGeneratorConfiguration() {
        return this.generatorConfiguration;
    }

}
