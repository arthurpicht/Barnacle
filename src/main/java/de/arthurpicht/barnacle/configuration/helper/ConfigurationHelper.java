package de.arthurpicht.barnacle.configuration.helper;

import de.arthurpicht.configuration.Configuration;
import de.arthurpicht.utils.core.strings.Strings;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public static Map<String, String> asProperties(List<String> stringList) {
        Map<String, String> properties = new LinkedHashMap<>();
        for (String property : stringList) {
            if (!property.contains("="))
                throw new RuntimeException("No equal sign found in configured property: [" + property + "].");
            String[] propertySplit = Strings.splitAtDelimiter(property, "=");
            String key = propertySplit[0];
            if (Strings.isNullOrEmpty(key))
                throw new RuntimeException("No key found in configured property: [" + property + "].");
            String value = propertySplit[1];
            if (Strings.isNullOrEmpty(value))
                throw new RuntimeException("No value found in configured property: [" + property + "].");
            properties.put(key.trim(), value.trim());
        }
        return properties;
    }

}
