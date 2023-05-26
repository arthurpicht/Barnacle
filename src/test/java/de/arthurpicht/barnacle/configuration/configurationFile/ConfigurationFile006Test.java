package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.direct.DirectJDBCConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.single.SingleJDBCConnectionConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationFile006Test {

    @Test
    void dbDirectConfigTest() {
        Path configurationFile = Paths.get("src/test/conf/barnacleTest006.conf");
        BarnacleConfigurationFile barnacleConfigurationFile = new BarnacleConfigurationFile(configurationFile);
        BarnacleConfiguration barnacleConfiguration = barnacleConfigurationFile.getBarnacleConfiguration();

        assertFalse(barnacleConfiguration.hasGeneratorConfiguration());
        assertTrue(barnacleConfiguration.hasDbConnectionConfigurations());

        List<DbConnectionConfiguration> dbConnectionConfigurations
                = barnacleConfiguration.getDbConnectionConfigurations();
        assertEquals(1, dbConnectionConfigurations.size());

        DbConnectionConfiguration dbConnectionConfiguration = dbConnectionConfigurations.get(0);
        assertTrue(dbConnectionConfiguration instanceof DirectJDBCConnectionConfiguration);

        DirectJDBCConnectionConfiguration directJDBCConnectionConfiguration
                = (DirectJDBCConnectionConfiguration) dbConnectionConfiguration;
        JDBCConfiguration jdbcConfiguration = directJDBCConnectionConfiguration.asJdbcConfiguration();

        assertEquals("de.arthurpicht.barnacle.test.direct.*", jdbcConfiguration.getDaoPackage());
    }

}
