package de.arthurpicht.barnacle.configuration.configurationFile;

public class SectionNames {

    public static final String GENERATOR = "generator";

    public static boolean isGeneratorConfig(String sectionName) {
        return sectionName.equals(GENERATOR);
    }

    public static boolean isDbConnectionConfig(String sectionName) {
        return DbConfigurationSectionName.isDbConfigurationSectionName(sectionName);
    }

}
