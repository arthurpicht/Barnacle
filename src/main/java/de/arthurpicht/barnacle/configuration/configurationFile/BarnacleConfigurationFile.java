package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfigurationFactory;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfigurationFactory;
import de.arthurpicht.configuration.Configuration;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class BarnacleConfigurationFile {

    private final BarnacleConfiguration barnacleConfiguration;

    public BarnacleConfigurationFile() {
        BarnacleConfigurationFileLoader barnacleConfigurationFileLoader = new BarnacleConfigurationFileLoader();
        this.barnacleConfiguration = init(barnacleConfigurationFileLoader);
    }

    public BarnacleConfigurationFile(Path configurationFile) {
        BarnacleConfigurationFileLoader barnacleConfigurationFileLoader
                = new BarnacleConfigurationFileLoader(configurationFile);
        this.barnacleConfiguration = init(barnacleConfigurationFileLoader);
    }

    private BarnacleConfiguration init(BarnacleConfigurationFileLoader barnacleConfigurationFileLoader) {
        GeneratorConfiguration generatorConfiguration
                = obtainGeneratorConfiguration(barnacleConfigurationFileLoader);
        List<DbConnectionConfiguration> dbConnectionConfigurations
                = obtainDbConnectionConfigurations(barnacleConfigurationFileLoader);
        return new BarnacleConfiguration(generatorConfiguration, dbConnectionConfigurations);
    }

    private GeneratorConfiguration obtainGeneratorConfiguration(BarnacleConfigurationFileLoader barnacleConfigurationFileLoader) {
        if (barnacleConfigurationFileLoader.hasGeneratorConfiguration()) {
            Configuration configuration = barnacleConfigurationFileLoader.getGeneratorConfiguration();
            return GeneratorConfigurationFactory.create(configuration);
        } else {
            return null;
        }
    }

    private List<DbConnectionConfiguration> obtainDbConnectionConfigurations(BarnacleConfigurationFileLoader barnacleConfigurationFileLoader) {
        DbConnectionConfigurationMap dbConnectionConfigurationMap
                = barnacleConfigurationFileLoader.getDbConnectionConfigurationMap();
        return dbConnectionConfigurationMap.getConfigurations().stream()
                .map(DbConnectionConfigurationFactory::create)
                .collect(Collectors.toList());
    }

    public BarnacleConfiguration getBarnacleConfiguration() {
        return barnacleConfiguration;
    }

}
