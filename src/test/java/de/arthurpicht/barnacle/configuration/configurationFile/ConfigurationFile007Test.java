package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.direct.DirectJDBCConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.single.SingleJDBCConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jndi.JNDIConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationFile007Test {

    @Test
    void dbConfigOrderTest() {
        Path configurationFile = Paths.get("src/test/conf/barnacleTest007.conf");
        BarnacleConfigurationFile barnacleConfigurationFile = new BarnacleConfigurationFile(configurationFile);
        BarnacleConfiguration barnacleConfiguration = barnacleConfigurationFile.getBarnacleConfiguration();

        List<DbConnectionConfiguration> dbConnectionConfigurations
                = barnacleConfiguration.getDbConnectionConfigurations();
        assertEquals(7, dbConnectionConfigurations.size());

        DbConnectionConfiguration dbConnectionConfiguration = dbConnectionConfigurations.get(0);
        assertDirect(dbConnectionConfiguration, "de.arthurpicht.barnacle.test.direct.*");

        dbConnectionConfiguration = dbConnectionConfigurations.get(1);
        assertSingle(dbConnectionConfiguration, "de.arthurpicht.barnacle.test.single.*");

        dbConnectionConfiguration = dbConnectionConfigurations.get(2);
        assertSingle(dbConnectionConfiguration, "de.arthurpicht.barnacle.test.single2.*");

        dbConnectionConfiguration = dbConnectionConfigurations.get(3);
        assertJndi(dbConnectionConfiguration, "de.arthurpicht.barnacle.test.jndi.*");

        dbConnectionConfiguration = dbConnectionConfigurations.get(4);
        assertSingle(dbConnectionConfiguration, "de.arthurpicht.barnacle.test.single3.*");

        dbConnectionConfiguration = dbConnectionConfigurations.get(5);
        assertDirect(dbConnectionConfiguration, "de.arthurpicht.barnacle.test.direct2.*");

        dbConnectionConfiguration = dbConnectionConfigurations.get(6);
        assertJndi(dbConnectionConfiguration, "de.arthurpicht.barnacle.test.jndi2.*");
    }

    private void assertDirect(DbConnectionConfiguration dbConnectionConfiguration, String expectedDaoPackage) {
        assertTrue(dbConnectionConfiguration instanceof DirectJDBCConnectionConfiguration);
        DirectJDBCConnectionConfiguration directJDBCConnectionConfiguration
                = (DirectJDBCConnectionConfiguration) dbConnectionConfiguration;
        JDBCConfiguration jdbcConfiguration = directJDBCConnectionConfiguration.asJdbcConfiguration();
        assertEquals(expectedDaoPackage, jdbcConfiguration.getDaoPackage());
    }

    private void assertSingle(DbConnectionConfiguration dbConnectionConfiguration, String expectedDaoPackage) {
        assertTrue(dbConnectionConfiguration instanceof SingleJDBCConnectionConfiguration);
        SingleJDBCConnectionConfiguration singleJDBCConnectionConfiguration
                = (SingleJDBCConnectionConfiguration) dbConnectionConfiguration;
        JDBCConfiguration jdbcConfiguration = singleJDBCConnectionConfiguration.asJdbcConfiguration();
        assertEquals(expectedDaoPackage, jdbcConfiguration.getDaoPackage());
    }

    private void assertJndi(DbConnectionConfiguration dbConnectionConfiguration, String expectedDaoPackage) {
        assertTrue(dbConnectionConfiguration instanceof JNDIConfiguration);
        JNDIConfiguration jndiConfiguration
                = (JNDIConfiguration) dbConnectionConfiguration;
        assertEquals(expectedDaoPackage, jndiConfiguration.getDaoPackage());
    }

}
