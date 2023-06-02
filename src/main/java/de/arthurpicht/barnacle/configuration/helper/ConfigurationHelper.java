package de.arthurpicht.barnacle.configuration.helper;

import de.arthurpicht.configuration.Configuration;
import de.arthurpicht.console.Console;
import de.arthurpicht.console.message.Level;
import de.arthurpicht.console.message.MessageBuilder;
import de.arthurpicht.utils.core.strings.Strings;

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

    public static void outputAllPropertiesOnDebugLevel(Configuration configuration) {
        Set<String> keys = configuration.getKeys();
        for (String key : keys) {
            if (key.equals("password")) {
                Console.out(
                        new MessageBuilder()
                                .asLevel(Level.VERY_VERBOSE)
                                .addText(key + " = <*****>")
                                .build());
            } else {
                Console.out(
                        new MessageBuilder()
                                .asLevel(Level.VERY_VERBOSE)
                                .addText(key + " = " + configuration.getString(key))
                                .build());
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
