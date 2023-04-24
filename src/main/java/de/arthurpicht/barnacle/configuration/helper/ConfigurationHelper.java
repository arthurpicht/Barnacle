package de.arthurpicht.barnacle.configuration.helper;

import de.arthurpicht.configuration.Configuration;
import org.slf4j.Logger;

import java.util.Set;

public class ConfigurationHelper {

    public static String getMandatoryStringParameter(Configuration configuration, String parameterName) {
        if (!configuration.containsKey(parameterName))
            throw new MandatoryConfigParameterMissing(configuration.getSectionName(), parameterName);
        return configuration.getString(parameterName);
    }

    public static void logAllPropertiesOnDebugLevel(Configuration configuration, Logger logger) {
        Set<String> keys = configuration.getKeys();
        for (String key : keys) {
            if (key.equals("password")) {
                logger.debug(key + " = <*****>");
            } else {
                logger.debug(key + " = " + configuration.getString(key));
            }
        }
    }

}
