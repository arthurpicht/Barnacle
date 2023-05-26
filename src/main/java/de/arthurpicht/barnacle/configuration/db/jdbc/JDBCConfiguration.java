package de.arthurpicht.barnacle.configuration.db.jdbc;

import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.utils.core.collection.Maps;

import java.util.Map;

public class JDBCConfiguration implements DbConnectionConfiguration {

    private final String daoPackage;
    private final String driverName;
    private final String url;
    private final String user;
    private final String password;
    private final Map<String, String> properties;

    public JDBCConfiguration(
            String daoPackage,
            String driverName,
            String url,
            String user,
            String password,
            Map<String, String> properties) {
        this.daoPackage = daoPackage;
        this.driverName = driverName;
        this.url = url;
        this.user = user;
        this.password = password;
        this.properties = Maps.immutableMap(properties);
    }

    @Override
    public String getDaoPackage() {
        return daoPackage;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

}
