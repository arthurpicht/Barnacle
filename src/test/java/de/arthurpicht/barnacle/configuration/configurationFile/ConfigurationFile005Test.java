package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.single.SingleJDBCConnectionConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationFile005Test {

    @Test
    void generatorConfigTest() {
        Path configurationFile = Paths.get("src/test/conf/barnacleTest005.conf");
        BarnacleConfigurationFile barnacleConfigurationFile = new BarnacleConfigurationFile(configurationFile);
        BarnacleConfiguration barnacleConfiguration = barnacleConfigurationFile.getBarnacleConfiguration();

        assertTrue(barnacleConfiguration.hasGeneratorConfiguration());
        assertTrue(barnacleConfiguration.hasDbConnectionConfigurations());

        List<DbConnectionConfiguration> dbConnectionConfigurations
                = barnacleConfiguration.getDbConnectionConfigurations();
        assertEquals(1, dbConnectionConfigurations.size());

        DbConnectionConfiguration dbConnectionConfiguration = dbConnectionConfigurations.get(0);
        SingleJDBCConnectionConfiguration singleJDBCConnectionConfiguration = (SingleJDBCConnectionConfiguration) dbConnectionConfiguration;
        JDBCConfiguration jdbcConfiguration = singleJDBCConnectionConfiguration.asJdbcConfiguration();

        Map<String, String> properties = jdbcConfiguration.getProperties();
        assertEquals(3, properties.size());

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();

            switch (key) {
                case "para1":
                    assertEquals("5", entry.getValue());
                case "para2":
                    assertEquals("foo", entry.getValue());
                case "para3":
                    assertEquals("foo bar", entry.getValue());
                default:
                    fail("Unexpected key: [" + key + "].");
            }

        }

    }

}
