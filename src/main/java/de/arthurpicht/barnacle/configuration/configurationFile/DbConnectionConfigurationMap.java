package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.configuration.Configuration;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DbConnectionConfigurationMap {

    private final Map<String, Configuration> dbConnectionConfigurationMap;

    public DbConnectionConfigurationMap() {
        this.dbConnectionConfigurationMap = new LinkedHashMap<>();
    }

    public void put(String sectionName, Configuration configuration) {
        this.dbConnectionConfigurationMap.put(sectionName, configuration);
    }

    public Set<String> getSectionNames() {
        return this.dbConnectionConfigurationMap.keySet();
    }

    public Collection<Configuration> getConfigurations() {
        return this.dbConnectionConfigurationMap.values();
    }

    public Configuration getConfiguration(String sectionName) {
        Configuration configuration = this.dbConnectionConfigurationMap.get(sectionName);
        if (configuration == null)
            throw new IllegalArgumentException("No configuration available for section name [" + sectionName + "].");
        return configuration;
    }

    public boolean hasConfigurations() {
        return !this.dbConnectionConfigurationMap.isEmpty();
    }

}
