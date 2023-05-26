package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jndi.JNDIConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationFile003Test {

    @Test
    void generatorConfigTest() {
        Path configurationFile = Paths.get("src/test/conf/barnacleTest003.conf");
        BarnacleConfigurationFile barnacleConfigurationFile = new BarnacleConfigurationFile(configurationFile);
        BarnacleConfiguration barnacleConfiguration = barnacleConfigurationFile.getBarnacleConfiguration();

        assertTrue(barnacleConfiguration.hasGeneratorConfiguration());
        assertTrue(barnacleConfiguration.hasDbConnectionConfigurations());

        List<DbConnectionConfiguration> dbConnectionConfigurations
                = barnacleConfiguration.getDbConnectionConfigurations();
        assertEquals(1, dbConnectionConfigurations.size());

        DbConnectionConfiguration dbConnectionConfiguration = dbConnectionConfigurations.get(0);
        assertTrue(dbConnectionConfiguration instanceof JNDIConfiguration);

        JNDIConfiguration jndiConfiguration = (JNDIConfiguration) dbConnectionConfiguration;

        assertEquals("de.arthurpicht.barnacle.test.*", jndiConfiguration.getDaoPackage());
        assertEquals("myJndiLookupName", jndiConfiguration.getLookupName());
    }

}
