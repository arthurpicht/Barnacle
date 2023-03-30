package de.arthurpicht.barnacle.configuration.db.single;

import de.arthurpicht.barnacle.configuration.helper.ConfigurationHelper;
import de.arthurpicht.configuration.Configuration;
import de.arthurpicht.utils.core.strings.Strings;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.arthurpicht.barnacle.configuration.db.single.JDBCConfigurationBF.*;

public class JDBCConfigurationFactory {

    public static JDBCConfiguration create(Configuration configuration) {

        String daoPackage = ConfigurationHelper.getMandatoryStringParameter(configuration, DAO_PACKAGE);
        String driverName = ConfigurationHelper.getMandatoryStringParameter(configuration, DRIVER_NAME);
        String url = ConfigurationHelper.getMandatoryStringParameter(configuration, URL);
        String user = ConfigurationHelper.getMandatoryStringParameter(configuration, USER);
        String password = ConfigurationHelper.getMandatoryStringParameter(configuration, PASSWORD);

        JDBCConfigurationBuilder jdbcConfigurationBuilder
                = new JDBCConfigurationBuilder(
                daoPackage,
                driverName,
                url,
                user,
                password
        );

        if (configuration.containsKey(PROPERTIES)) {
            List<String> propertiesList = configuration.getStringList(PROPERTIES);
            Map<String, String> properties = getProperties(propertiesList);
            jdbcConfigurationBuilder.withProperties(properties);
        }

        return jdbcConfigurationBuilder.build();
    }

    private static Map<String, String> getProperties(List<String> propertiesList) {
        Map<String, String> properties = new LinkedHashMap<>();
        for (String property : propertiesList) {
            if (!property.contains("="))
                throw new RuntimeException("No equal sign found in configured property: [" + property + "].");
            String[] propertySplit = Strings.splitAtDelimiter(property, "=");
            String key = propertySplit[0];
            if (Strings.isNullOrEmpty(key))
                throw new RuntimeException("No key found in configured property: [" + property + "].");
            String value = propertySplit[1];
            if (Strings.isNullOrEmpty(value))
                throw new RuntimeException("No value found in configured property: [" + property + "].");
            properties.put(key, value);
        }
        return properties;
    }

}
