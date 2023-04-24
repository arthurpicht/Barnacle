package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfigurationFactory;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfigurationFactory;
import de.arthurpicht.configuration.Configuration;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
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
        Optional<Configuration> generatorConfigurationOpt = barnacleConfigurationFileLoader.getGeneratorConfiguration();
        GeneratorConfiguration generatorConfiguration = null;
        if (generatorConfigurationOpt.isPresent()) {
            generatorConfiguration = GeneratorConfigurationFactory.create(generatorConfigurationOpt.get());
        }

        DbConnectionConfigurationMap dbConnectionConfigurationMap
                = barnacleConfigurationFileLoader.getDbConnectionConfigurationMap();

        List<DbConnectionConfiguration> dbConnectionConfigurations = dbConnectionConfigurationMap.getConfigurations().stream()
                .map(DbConnectionConfigurationFactory::create)
                .collect(Collectors.toList());

        return new BarnacleConfiguration(generatorConfiguration, dbConnectionConfigurations);
    }

    public BarnacleConfiguration getBarnacleConfiguration() {
        return barnacleConfiguration;
    }

}
