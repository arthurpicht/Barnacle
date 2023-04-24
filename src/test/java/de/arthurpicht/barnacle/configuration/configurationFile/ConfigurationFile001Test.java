package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.connectionManager.ConnectionManager;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.barnacle.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationFile001Test {

    @Test
    void generatorConfigMinimal() {
        Path configurationFile = Paths.get("src/test/conf/barnacleTest001.conf");
        BarnacleConfigurationFile barnacleConfigurationFile = new BarnacleConfigurationFile(configurationFile);
        BarnacleConfiguration barnacleConfiguration = barnacleConfigurationFile.getBarnacleConfiguration();

        assertTrue(barnacleConfiguration.hasGeneratorConfiguration());
        assertFalse(barnacleConfiguration.hasDbConnectionConfigurations());

        GeneratorConfiguration generatorConfiguration = barnacleConfiguration.getGeneratorConfiguration();

        // specified parameters
        assertEquals("de.arthurpicht.barnacle.test.persistence.vof", generatorConfiguration.getVofPackageName());
        assertEquals("de.arthurpicht.barnacle.test.persistence.vo", generatorConfiguration.getVoPackageName());
        assertEquals("de.arthurpicht.barnacle.test.persistence.vob", generatorConfiguration.getVobPackageName());
        assertEquals("de.arthurpicht.barnacle.test.persistence.dao", generatorConfiguration.getDaoPackageName());

        // default parameters
        assertEquals(Const.Dialect.MYSQL, generatorConfiguration.getDialect());
        assertEquals("src/", generatorConfiguration.getSrcDir());
        assertEquals("src-gen/", generatorConfiguration.getSrcGenDir());
        assertFalse(generatorConfiguration.isExecuteOnDb());
        assertFalse(generatorConfiguration.isCreateScript());
        assertEquals("barnacle.sql", generatorConfiguration.getScriptFile());
        assertEquals(Const.Encoding.DEFAULT, generatorConfiguration.getEncodingDB());
        assertEquals(Const.Encoding.UTF, generatorConfiguration.getEncodingSource());
        assertEquals(ConnectionManager.class.getCanonicalName(), generatorConfiguration.getConnectionManagerCanonicalClassName());
        assertEquals(DBConnectionException.class.getCanonicalName(), generatorConfiguration.getConnectionExceptionCanonicalClassName());
        assertEquals(EntityNotFoundException.class.getCanonicalName(), generatorConfiguration.getEntityNotFoundExceptionCanonicalClassName());
        assertEquals("", generatorConfiguration.getDaoLoggerName());
        assertFalse(generatorConfiguration.isOmitJavaDoc());
    }

}
