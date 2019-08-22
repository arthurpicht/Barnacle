package de.arthurpicht.barnacle.configuration;

import de.arthurpicht.configuration.Configuration;
import de.arthurpicht.configuration.ConfigurationFactory;
import de.arthurpicht.configuration.ConfigurationFileNotFoundException;

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
	
	private static ConfigurationFactory configurationFactory;
	private static Configuration generalConfiguration;
	private static Configuration generatorConfiguration;
	
	static {
		
		configurationFactory = new ConfigurationFactory();	
		
		// barnacle-default.conf laden

		try {
			configurationFactory.addConfigurationFileFromClasspath("barnacle-default.conf");
		} catch (ConfigurationFileNotFoundException | IOException e) {
			// TODO suboptimal. static initializer aufheben, besseres Exception-Konzept
			throw new RuntimeException("Barnacle default configuration 'barnacle-default.conf' not found!");
		}

		// Konfigurationsdatei laden, deren Namen als SysProp barnacle.conf übergeben wurde.
		// Wenn SysProp nicht besteht, barnacle.conf laden.
		
		String barnacleConfFileName = "barnacle.conf";
		String barnacleConfBySystemProp = null;
		
		try {
			barnacleConfBySystemProp = System.getProperty("barnacle.conf");			
		} catch (SecurityException e) {
			// do nothing
		}
		
		if (barnacleConfBySystemProp != null) {
			barnacleConfFileName = barnacleConfBySystemProp;
		}

		try {
			configurationFactory.addConfigurationFileFromClasspath(barnacleConfFileName);
		} catch (ConfigurationFileNotFoundException | IOException e) {
			// TODO suboptimal. static initializer aufheben, besseres Exception-Konzept
			throw new RuntimeException("Barnacle project specific configuration '" + barnacleConfFileName + "' not found!");
		}

		// Sektion [general] auswerten.
		generalConfiguration = configurationFactory.getConfiguration("general");
		
		// Sektion [generator] auswerten, sonst null hinterlegen.
		generatorConfiguration = configurationFactory.getConfiguration("generator");
		
	}
	
	public static Configuration getGeneralConfiguration() {
		return generalConfiguration;
	}
	
	public static ConfigurationFactory getConfigurationFactory() {
		return configurationFactory;
	}
	
	public static String getLoggerName() {
		return generalConfiguration.getString("logger", "BARNACLE");
	}
	
	public static boolean isLogConfigOnInit() {
		return generalConfiguration.getBoolean("log_init_config", true);
	}
	
	public static boolean hasGeneratorConfiguration() {
		if (generatorConfiguration == null) {
			return false;
		}
		return true;
	}
	
	public static Configuration getGeneratorConfiguration() {
		return generatorConfiguration;
	}
	
}
