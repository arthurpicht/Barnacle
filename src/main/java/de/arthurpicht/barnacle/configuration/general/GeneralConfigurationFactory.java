package de.arthurpicht.barnacle.configuration.general;

import de.arthurpicht.configuration.Configuration;

public class GeneralConfigurationFactory {

    public static GeneralConfiguration create(Configuration configuration) {

        GeneralConfigurationBuilder generalConfigurationBuilder = new GeneralConfigurationBuilder();

        if (configuration.containsKey(GeneralConfiguration.LOGGER)) {
            generalConfigurationBuilder.withLogger(
                    configuration.getString(GeneralConfiguration.LOGGER));
        }

        if (configuration.containsKey(GeneralConfiguration.LOG_CONFIG_ON_INIT)) {
            generalConfigurationBuilder.withLogConfigOnInit(
                    configuration.getBoolean(GeneralConfiguration.LOG_CONFIG_ON_INIT));
        }

        return generalConfigurationBuilder.build();
    }

}
