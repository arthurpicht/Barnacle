package de.arthurpicht.barnacle.configuration.db.jdbc.direct;

import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfiguration;

public class DirectJDBCConnectionConfiguration implements DbConnectionConfiguration {

    private final JDBCConfiguration jdbcConfiguration;

    public DirectJDBCConnectionConfiguration(JDBCConfiguration jdbcConfiguration) {
        this.jdbcConfiguration = jdbcConfiguration;
    }

    public JDBCConfiguration asJdbcConfiguration() {
        return this.jdbcConfiguration;
    }

}
