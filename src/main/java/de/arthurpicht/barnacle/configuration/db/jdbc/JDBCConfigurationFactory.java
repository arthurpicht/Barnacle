package de.arthurpicht.barnacle.configuration.db.jdbc;

import de.arthurpicht.barnacle.configuration.helper.ConfigurationHelper;
import de.arthurpicht.configuration.Configuration;

import java.util.List;
import java.util.Map;

import static de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfigurationBF.*;

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
            Map<String, String> properties = ConfigurationHelper.asProperties(propertiesList);
            jdbcConfigurationBuilder.withProperties(properties);
        }

        return jdbcConfigurationBuilder.build();
    }

}
