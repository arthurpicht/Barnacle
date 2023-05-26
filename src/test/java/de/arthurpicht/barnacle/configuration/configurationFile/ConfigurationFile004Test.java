package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.single.SingleJDBCConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jndi.JNDIConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationFile004Test {

    @Test
    void generatorConfigTest() {
        Path configurationFile = Paths.get("src/test/conf/barnacleTest004.conf");
        BarnacleConfigurationFile barnacleConfigurationFile = new BarnacleConfigurationFile(configurationFile);
        BarnacleConfiguration barnacleConfiguration = barnacleConfigurationFile.getBarnacleConfiguration();

        assertTrue(barnacleConfiguration.hasGeneratorConfiguration());
        assertTrue(barnacleConfiguration.hasDbConnectionConfigurations());

        List<DbConnectionConfiguration> dbConnectionConfigurations
                = barnacleConfiguration.getDbConnectionConfigurations();
        assertEquals(1, dbConnectionConfigurations.size());

        DbConnectionConfiguration dbConnectionConfiguration = dbConnectionConfigurations.get(0);
        assertTrue(dbConnectionConfiguration instanceof SingleJDBCConnectionConfiguration);

        SingleJDBCConnectionConfiguration singleJDBCConnectionConfiguration = (SingleJDBCConnectionConfiguration) dbConnectionConfiguration;
        JDBCConfiguration jdbcConfiguration = singleJDBCConnectionConfiguration.asJdbcConfiguration();

        assertEquals("de.arthurpicht.barnacle.test.single.*", jdbcConfiguration.getDaoPackage());
        assertEquals("myDriverName", jdbcConfiguration.getDriverName());
        assertEquals("myDbUrl", jdbcConfiguration.getUrl());
        assertEquals("joe", jdbcConfiguration.getUser());
        assertEquals("secret", jdbcConfiguration.getPassword());

        Map<String, String> properties = jdbcConfiguration.getProperties();
        assertTrue(properties.isEmpty());
    }

}
