package de.arthurpicht.barnacle.configuration.db.jndi;

import de.arthurpicht.barnacle.configuration.helper.ConfigurationHelper;
import de.arthurpicht.configuration.Configuration;

import static de.arthurpicht.barnacle.configuration.db.jndi.JNDIConfiguration.DAO_PACKAGE;
import static de.arthurpicht.barnacle.configuration.db.jndi.JNDIConfiguration.LOOKUP_NAME;

public class JNDIConfigurationFactory {

    public static JNDIConfiguration create(Configuration configuration) {

        String daoPackage = ConfigurationHelper.getMandatoryStringParameter(configuration, DAO_PACKAGE);
        String lookupName = ConfigurationHelper.getMandatoryStringParameter(configuration, LOOKUP_NAME);

        return new JNDIConfiguration(daoPackage, lookupName);
    }

}
