package de.arthurpicht.barnacle.configuration.db.jdbc.single;

import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfiguration;

public class SingleJDBCConnectionConfiguration implements DbConnectionConfiguration {

    private final JDBCConfiguration jdbcConfiguration;

    public SingleJDBCConnectionConfiguration(JDBCConfiguration jdbcConfiguration) {
        this.jdbcConfiguration = jdbcConfiguration;
    }

    public JDBCConfiguration asJdbcConfiguration() {
        return this.jdbcConfiguration;
    }

    @Override
    public String getDaoPackage() {
        return this.jdbcConfiguration.getDaoPackage();
    }
}
