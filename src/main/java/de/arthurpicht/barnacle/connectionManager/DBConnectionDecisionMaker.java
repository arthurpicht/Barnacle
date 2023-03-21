package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.util.Map;
import java.util.Set;

/**
 * Aus Gründen der Performance wird zwischen dem Fall nur einer DB-Konfiguration
 * (Single) und mehreren DB-Konfigurationen (Multiple) unterschieden. Nur im
 * letzten Fall muss tatsächlich eine Entscheidung über die Auswahl getroffen
 * werden.
 * 
 * @author Arthur Picht, (c) 2013 Arthur Picht GmbH, Düsseldorf
 *
 */
public abstract class DBConnectionDecisionMaker {
	
	/**
	 * Factory-Methode. Liefert geeignetes DBConnectionDecisionMaker Objekt in Abhängigkeit
	 * von den übergebenen dbConnections zurück.
	 * 
	 * @param dbConnections
	 * @return Objekt vom Typ DBConnectionDecisionMaker
	 */
	public static DBConnectionDecisionMaker getDBConnectionDecisionMaker(Map<String, ConnectionWrapper> dbConnections) {

		// Voraussetzung prüfen
		if (dbConnections == null || dbConnections.size() == 0) {
			throw new RuntimeException("Initialisierung von " + DBConnectionDecisionMaker.class.getCanonicalName() + " ohne DBConnections");
		}
		
		if (dbConnections.size() == 1) {
			
//			System.out.println("Initialisiere DBConnectionDecisionMakerSingle");
			
			Set<String> keys = dbConnections.keySet();
			String key = keys.iterator().next();
			ConnectionWrapper connectionWrapper = dbConnections.get(key);
			return new DBConnectionDecisionMakerSingle(connectionWrapper);
		}
		
//		System.out.println("Initialisiere DBConnectionDecisionMakerMultiple");
		return new DBConnectionDecisionMakerMultiple(dbConnections);
	}
	
	public abstract ConnectionWrapper getDBConnectionByDaoClass(String canonicalClassName) throws DBConnectionException;

}
