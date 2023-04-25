package de.arthurpicht.barnacle.configuration.db.jdbc.direct;

import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfigurationFactory;
import de.arthurpicht.barnacle.configuration.db.jdbc.single.SingleJDBCConnectionConfiguration;
import de.arthurpicht.configuration.Configuration;

public class DirectJDBCConnectionConfigurationFactory {

    public static DirectJDBCConnectionConfiguration create(Configuration configuration) {
        JDBCConfiguration jdbcConfiguration = JDBCConfigurationFactory.create(configuration);
        return new DirectJDBCConnectionConfiguration(jdbcConfiguration);
    }

}
