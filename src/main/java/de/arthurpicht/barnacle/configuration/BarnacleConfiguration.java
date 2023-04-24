package de.arthurpicht.barnacle.configuration;

import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.utils.core.assertion.MethodPreconditions;

import java.util.List;

public class BarnacleConfiguration {

    private final GeneratorConfiguration generatorConfiguration;
    private final List<DbConnectionConfiguration> dbConnectionConfigurations;

    public BarnacleConfiguration(
            GeneratorConfiguration generatorConfiguration,
            List<DbConnectionConfiguration> dbConnectionConfigurations) {
        MethodPreconditions.assertArgumentNotNull("dbConnectionConfigurations", dbConnectionConfigurations);
        this.generatorConfiguration = generatorConfiguration;
        this.dbConnectionConfigurations = dbConnectionConfigurations;
    }

    public boolean hasGeneratorConfiguration() {
        return this.generatorConfiguration != null;
    }

    public GeneratorConfiguration getGeneratorConfiguration() {
        return generatorConfiguration;
    }

    public boolean hasDbConnectionConfigurations() {
        return !this.dbConnectionConfigurations.isEmpty();
    }

    public List<DbConnectionConfiguration> getDbConnectionConfigurations() {
        return dbConnectionConfigurations;
    }

}
