package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.single.SingleJDBCConnectionConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        List<Entry<String, String>> entryList = new ArrayList<>(properties.entrySet());

        Entry<String, String> entry = entryList.get(0);
        assertEquals("para1", entry.getKey());
        assertEquals("test", entry.getValue());

        entry = entryList.get(1);
        assertEquals("para2", entry.getKey());
        assertEquals("foo", entry.getValue());

        entry = entryList.get(2);
        assertEquals("para3", entry.getKey());
        assertEquals("foo bar", entry.getValue());
    }

}
