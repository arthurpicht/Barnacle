package de.arthurpicht.barnacle.configuration.db.single;

import java.util.LinkedHashMap;
import java.util.Map;

public class JDBCConfigurationBuilder extends JDBCConfigurationBF {

    public JDBCConfigurationBuilder(
            String daoPackage,
            String driverName,
            String url,
            String user,
            String password
    ) {
        this.daoPackage = daoPackage;
        this.driverName = driverName;
        this.url = url;
        this.user = user;
        this.password = password;
        this.properties = new LinkedHashMap<>();
    }

    public JDBCConfigurationBuilder withProperty(String key, String value) {
        this.properties.put(key, value);
        return this;
    }

    public JDBCConfigurationBuilder withProperties(Map<String, String> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public JDBCConfiguration build() {
        return new JDBCConfiguration(
                this.daoPackage,
                this.driverName,
                this.url,
                this.user,
                this.password,
                this.properties
        );
    }

}
