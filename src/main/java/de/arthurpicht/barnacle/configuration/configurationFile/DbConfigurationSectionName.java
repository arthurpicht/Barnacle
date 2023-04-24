package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.db.DbConfigurationType;
import de.arthurpicht.utils.core.strings.Strings;

import java.util.regex.Pattern;

public class DbConfigurationSectionName {

    private static final Pattern DB_CONFIG_PATTERN
            = Pattern.compile("db:(" + DbConfigurationType.SINGLE.name().toLowerCase()
            + "|"
            + DbConfigurationType.JNDI.name().toLowerCase() + ")"
            + ":[a-zA-Z0-9_-]+");

    private final DbConfigurationType dbConfigurationType;
    private final String name;

    public static boolean isDbConfigurationSectionName(String sectionName) {
        try {
            new DbConfigurationSectionName(sectionName);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public DbConfigurationSectionName(String sectionName) {
        if (!DB_CONFIG_PATTERN.matcher(sectionName).matches())
            throw new IllegalArgumentException(
                    "No valid section name for a barnacle db configuration: [" + sectionName + "].");
        String[] sectionNameChunks = Strings.multiSplit(sectionName, ":", ":");
        String typeString = sectionNameChunks[1];
        this.dbConfigurationType = DbConfigurationType.valueOf(typeString.toUpperCase());
        this.name = sectionNameChunks[2];
    }

    public DbConfigurationType getDbConfigurationType() {
        return this.dbConfigurationType;
    }

    public String getName() {
        return this.name;
    }

}