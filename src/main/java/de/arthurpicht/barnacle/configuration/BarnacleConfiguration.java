package de.arthurpicht.barnacle.configuration;

import de.arthurpicht.configuration.Configuration;
import de.arthurpicht.configuration.ConfigurationFactory;
import de.arthurpicht.configuration.ConfigurationFileNotFoundException;
import de.arthurpicht.utils.core.collection.Maps;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Lädt die Barnacle-Konfiguration. Hierzu wird zunächst 'barnacle-default.conf' aus
 * dem Barnacle-Projekt geladen und dann mit einer projektspezifischen Konfigurations-
 * datei überschrieben. Im Regelfall ist dies die Datei 'barnacle.conf', welche im 
 * Klassenpfad zu finden sein muss. Eine abweichende Datei wird stattdessen gebunden,
 * wenn die System-Property 'barnacle.conf' gesetzt ist, ihren Namen enthält und
 * im Klassenpfad zu finden ist. 
 * 
 * @author Arthur Picht, Arthur Picht GmbH, Düsseldorf, (c) 2013
 *
 */
public class BarnacleConfiguration {
	
	private final ConfigurationFactory configurationFactory;
	private final Configuration generatorConfiguration;
	private final Map<String, Configuration> connectionConfigurationMap;
	
	public BarnacleConfiguration() {

		configurationFactory = new ConfigurationFactory();
		
		// barnacle-default.conf laden
//		try {
//			configurationFactory.addConfigurationFileFromClasspath("barnacle-default.conf");
//		} catch (ConfigurationFileNotFoundException | IOException e) {
//			throw new RuntimeException("Barnacle default configuration 'barnacle-default.conf' not found!");
//		}

		// Konfigurationsdatei laden, deren Namen als SysProp barnacle.conf übergeben wurde.
		// Wenn SysProp nicht besteht, barnacle.conf laden.
		String barnacleConfBySystemProp = null;
		try {
			barnacleConfBySystemProp = System.getProperty("barnacle.conf");			
		} catch (SecurityException ignore) {
		}
		
		if (barnacleConfBySystemProp != null) {
			try {
				configurationFactory.addConfigurationFileFromFilesystem(new File(barnacleConfBySystemProp));
			} catch (ConfigurationFileNotFoundException | IOException e) {
				throw new RuntimeException("Barnacle configuration file not found as specified by system property " +
						"barnacle.conf [" + barnacleConfBySystemProp + "].");
			}
		} else {
			try {
				configurationFactory.addConfigurationFileFromClasspath("barnacle.conf");
			} catch (ConfigurationFileNotFoundException | IOException e) {
				throw new RuntimeException("Barnacle configuration file [barnacle.conf] not found on classpath.");
			}
		}

		if (configurationFactory.hasSection("generator")) {
			generatorConfiguration = configurationFactory.getConfiguration("generator");
		} else {
			generatorConfiguration = null;
		}

		connectionConfigurationMap = new LinkedHashMap<>();
		Set<String> sectionNames = configurationFactory.getSectionNames();
		for (String sectionName : sectionNames) {
			if (sectionName.startsWith("db:")) {
				Configuration configuration = configurationFactory.getConfiguration(sectionName);
				connectionConfigurationMap.put(sectionName, configuration);
			} else if (!sectionName.equals("generator")) {
				throw new RuntimeException("Unknown section [" + sectionName + "] found in barnacle configuration.");
			}
		}
	}

	public ConfigurationFactory getConfigurationFactory() {
		return configurationFactory;
	}

	public boolean hasGeneratorConfiguration() {
		return generatorConfiguration != null;
	}
	
	public Configuration getGeneratorConfiguration() {
		return generatorConfiguration;
	}

	public boolean hasConnectionConfigurations() {
		return !connectionConfigurationMap.isEmpty();
	}

	public Map<String, Configuration> getConnectionConfigurationMap() {
		return Maps.immutableMap(connectionConfigurationMap);
	}

}
