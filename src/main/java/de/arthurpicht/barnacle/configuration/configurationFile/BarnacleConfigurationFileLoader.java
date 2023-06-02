package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.helper.ConfigurationHelper;
import de.arthurpicht.barnacle.exceptions.BarnacleIllegalStateException;
import de.arthurpicht.barnacle.exceptions.BarnacleInitializerException;
import de.arthurpicht.barnacle.helper.ConsoleHelper;
import de.arthurpicht.configuration.Configuration;
import de.arthurpicht.configuration.ConfigurationFactory;
import de.arthurpicht.configuration.ConfigurationFileNotFoundException;
import de.arthurpicht.utils.io.nio2.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class BarnacleConfigurationFileLoader {

    private static final String BARNACLE_SYSTEM_PROPERTY = "barnacle.conf";
    private static final String BARNACLE_CONF_FILE_NAME = "barnacle.conf";

    private final ConfigurationFactory configurationFactory;
    private final Optional<Configuration> generatorConfigurationOpt;
    private final DbConnectionConfigurationMap dbConnectionConfigurationMap;

    public BarnacleConfigurationFileLoader() {
        this.configurationFactory = bindConfigurationFile();
        this.generatorConfigurationOpt = obtainGeneratorConfiguration();
        this.dbConnectionConfigurationMap = obtainDbConnectionConfigurationMap();
        outputConfiguration();
    }

    public BarnacleConfigurationFileLoader(Path configurationFile) {
        if (!FileUtils.isExistingRegularFile(configurationFile))
            throw new BarnacleInitializerException("Specified barnacle configuration file no found: " +
                    "[" + configurationFile.toAbsolutePath() + "].");
        this.configurationFactory = bindConfigurationFile(configurationFile);
        this.generatorConfigurationOpt = obtainGeneratorConfiguration();
        this.dbConnectionConfigurationMap = obtainDbConnectionConfigurationMap();
        outputConfiguration();
    }

    private ConfigurationFactory bindConfigurationFile() {
        ConfigurationFactory configurationFactory = new ConfigurationFactory();
        String barnacleConfBySystemProp = null;
        try {
            barnacleConfBySystemProp = System.getProperty(BARNACLE_SYSTEM_PROPERTY);
        } catch (SecurityException ignore) {}
        if (barnacleConfBySystemProp != null) {
            try {
                File configurationFile = new File(barnacleConfBySystemProp);
                configurationFactory.addConfigurationFileFromFilesystem(configurationFile);
            } catch (ConfigurationFileNotFoundException | IOException e) {
                throw new RuntimeException("Barnacle configuration file not found as specified by system property " +
                        BARNACLE_SYSTEM_PROPERTY + "=[" + barnacleConfBySystemProp + "].");
            }
        } else {
            try {
                configurationFactory.addConfigurationFileFromClasspath(BARNACLE_CONF_FILE_NAME);
            } catch (ConfigurationFileNotFoundException | IOException e) {
                throw new RuntimeException("Barnacle configuration file [" + BARNACLE_CONF_FILE_NAME + "] " +
                        "not found on classpath.");
            }
        }
        return configurationFactory;
    }

    private ConfigurationFactory bindConfigurationFile(Path configurationFile) {
        ConfigurationFactory configurationFactory = new ConfigurationFactory();
        try {
            configurationFactory.addConfigurationFileFromFilesystem(configurationFile.toFile());
        } catch (ConfigurationFileNotFoundException | IOException e) {
            throw new RuntimeException("Barnacle configuration file not found: " +
                    "[" + configurationFile.toAbsolutePath() + "].");
        }
        return configurationFactory;
    }

    private Optional<Configuration> obtainGeneratorConfiguration() {
        if (configurationFactory.hasSection(SectionNames.GENERATOR)) {
            Configuration generatorConfiguration = configurationFactory.getConfiguration(SectionNames.GENERATOR);
            return Optional.of(generatorConfiguration);
        } else {
            return Optional.empty();
        }
    }

    private DbConnectionConfigurationMap obtainDbConnectionConfigurationMap() {
        DbConnectionConfigurationMap dbConnectionConfigurationMap = new DbConnectionConfigurationMap();
        Set<String> sectionNames = configurationFactory.getSectionNames();
        for (String sectionName : sectionNames) {
            if (SectionNames.isDbConnectionConfig(sectionName)) {
                Configuration configuration = configurationFactory.getConfiguration(sectionName);
                dbConnectionConfigurationMap.put(sectionName, configuration);
            } else if (!SectionNames.isGeneratorConfig(sectionName)) {
                throw new RuntimeException("Unrecognized section [" + sectionName + "] found in barnacle configuration.");
            }
        }
        return dbConnectionConfigurationMap;
    }

    private void outputConfiguration() {
        ConsoleHelper.veryVerbose(BARNACLE_CONF_FILE_NAME + ":");
        if (this.generatorConfigurationOpt.isEmpty()) {
            ConsoleHelper.veryVerbose("No [generator] section.");
        } else {
            ConfigurationHelper.outputAllPropertiesOnDebugLevel(this.generatorConfigurationOpt.get());
        }
        for (String sectionName : this.dbConnectionConfigurationMap.getSectionNames()) {
            Configuration configuration = this.dbConnectionConfigurationMap.getConfiguration(sectionName);
            ConsoleHelper.veryVerbose("[" + sectionName + "]");
            ConfigurationHelper.outputAllPropertiesOnDebugLevel(configuration);
        }
    }
    
    public boolean hasGeneratorConfiguration() {
        return this.generatorConfigurationOpt.isPresent();
    }

    public Configuration getGeneratorConfiguration() {
        if (this.generatorConfigurationOpt.isEmpty())
            throw new BarnacleIllegalStateException("No generator configuration specified. Check before requesting.");
        return this.generatorConfigurationOpt.get();
    }

    public DbConnectionConfigurationMap getDbConnectionConfigurationMap() {
        return dbConnectionConfigurationMap;
    }

}
