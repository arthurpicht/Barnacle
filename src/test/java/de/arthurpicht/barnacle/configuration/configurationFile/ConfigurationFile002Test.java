package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationFile002Test {

    @Test
    void generatorConfigTest() {
        Path configurationFile = Paths.get("src/test/conf/barnacleTest002.conf");
        BarnacleConfigurationFile barnacleConfigurationFile = new BarnacleConfigurationFile(configurationFile);
        BarnacleConfiguration barnacleConfiguration = barnacleConfigurationFile.getBarnacleConfiguration();

        assertTrue(barnacleConfiguration.hasGeneratorConfiguration());
        assertFalse(barnacleConfiguration.hasDbConnectionConfigurations());

        GeneratorConfiguration generatorConfiguration = barnacleConfiguration.getGeneratorConfiguration();

        // specified parameters
        assertEquals("src-test/", generatorConfiguration.getSrcDir());
        assertEquals("src-test-gen/", generatorConfiguration.getSrcGenDir());
        assertEquals("de.arthurpicht.barnacle.test.persistence.vof", generatorConfiguration.getVofPackageName());
        assertEquals("de.arthurpicht.barnacle.test.persistence.vo", generatorConfiguration.getVoPackageName());
        assertEquals("de.arthurpicht.barnacle.test.persistence.vob", generatorConfiguration.getVobPackageName());
        assertEquals("de.arthurpicht.barnacle.test.persistence.dao", generatorConfiguration.getDaoPackageName());
        assertFalse(generatorConfiguration.isExecuteOnDb());
        assertTrue(generatorConfiguration.isCreateScript());
        assertEquals("test-sql/barnacle_demo.sql", generatorConfiguration.getScriptFile());
        assertEquals("UTF", generatorConfiguration.getEncodingDB().name());
        assertEquals("UTF", generatorConfiguration.getEncodingSource().name());
    }

}
