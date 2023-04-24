package de.arthurpicht.barnacle.configuration.db.jdbc.single;

import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfigurationFactory;
import de.arthurpicht.configuration.Configuration;

public class SingleJDBCConnectionConfigurationFactory {

    public static SingleJDBCConnectionConfiguration create(Configuration configuration) {
        JDBCConfiguration jdbcConfiguration = JDBCConfigurationFactory.create(configuration);
        return new SingleJDBCConnectionConfiguration(jdbcConfiguration);
    }

}
