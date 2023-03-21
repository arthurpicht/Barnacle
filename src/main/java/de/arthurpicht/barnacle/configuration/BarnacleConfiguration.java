package de.arthurpicht.barnacle.configuration;

import de.arthurpicht.configuration.Configuration;
import de.arthurpicht.configuration.ConfigurationFactory;
import de.arthurpicht.configuration.ConfigurationFileNotFoundException;

import java.io.File;
import java.io.IOException;

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
	private final Configuration generalConfiguration;
	private final Configuration generatorConfiguration;
	
	public BarnacleConfiguration() {

		configurationFactory = new ConfigurationFactory();
		
		// barnacle-default.conf laden
		try {
			configurationFactory.addConfigurationFileFromClasspath("barnacle-default.conf");
		} catch (ConfigurationFileNotFoundException | IOException e) {
			throw new RuntimeException("Barnacle default configuration 'barnacle-default.conf' not found!");
		}

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
				throw new RuntimeException("Barnacle project specific configuration '"
						+ barnacleConfBySystemProp + "' not found!");
			}
		} else {
			try {
				configurationFactory.addConfigurationFileFromClasspath("barnacle.conf");
			} catch (ConfigurationFileNotFoundException | IOException e) {
				throw new RuntimeException("Barnacle project specific configuration 'barnacle.conf' not found!");
			}
		}

		// Sektion [general] auswerten.
		generalConfiguration = configurationFactory.getConfiguration("general");
		
		// Sektion [generator] auswerten, sonst null hinterlegen.
		generatorConfiguration = configurationFactory.getConfiguration("generator");
	}
	

	public ConfigurationFactory getConfigurationFactory() {
		return configurationFactory;
	}
	
	public boolean hasGeneralConfiguration() {
		return generalConfiguration != null;
	}

	public Configuration getGeneralConfiguration() {
		return generalConfiguration;
	}

	public boolean hasGeneratorConfiguration() {
		return generatorConfiguration != null;
	}
	
	public Configuration getGeneratorConfiguration() {
		return generatorConfiguration;
	}

}
