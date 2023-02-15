package de.arthurpicht.barnacle;

import de.arthurpicht.barnacle.exceptions.BarnacleInitializerException;

/**
 * Dies ist die Start-Klasse für die Barnacle-Generierung. Sie kann aufgerufen werden
 * durch ein Ant-Skript, per Kommandozeile oder in der IDE. Entscheidend ist dass alle
 * Abhängigkeiten und das die rojektspezifische Konfigurationsdatei barnacle.conf
 * im Klassenpfad liegen.
 * 
 * @author Arthur Picht, Arthur Picht GmbH, Düsseldorf, (c) 2013
 *
 */
public class GeneratorInit {

	public static void main(String[] args) {
		
		try {
			BarnacleInitializer.process();
		} catch (BarnacleInitializerException e) {
			e.printStackTrace();
		}
	}
	
}
