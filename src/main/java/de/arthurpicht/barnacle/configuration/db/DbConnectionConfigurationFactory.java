package de.arthurpicht.barnacle.configuration.db;

import de.arthurpicht.barnacle.configuration.configurationFile.DbConfigurationSectionName;
import de.arthurpicht.barnacle.configuration.db.jdbc.direct.DirectJDBCConnectionConfigurationFactory;
import de.arthurpicht.barnacle.configuration.db.jdbc.single.SingleJDBCConnectionConfigurationFactory;
import de.arthurpicht.barnacle.configuration.db.jndi.JNDIConfigurationFactory;
import de.arthurpicht.configuration.Configuration;

public class DbConnectionConfigurationFactory {

    public static DbConnectionConfiguration create(Configuration configuration) {
        String sectionName = configuration.getSectionName();
        DbConfigurationSectionName dbConfigurationSectionName = new DbConfigurationSectionName(sectionName);
        DbConfigurationType dbConfigurationType = dbConfigurationSectionName.getDbConfigurationType();

        switch (dbConfigurationType) {
            case SINGLE:
                return SingleJDBCConnectionConfigurationFactory.create(configuration);
            case JNDI:
                return JNDIConfigurationFactory.create(configuration);
            case DIRECT:
                return DirectJDBCConnectionConfigurationFactory.create(configuration);
        }
        throw new IllegalStateException("Unknown dbConfiguration type: [" + dbConfigurationType.name() + "].");
    }

}
