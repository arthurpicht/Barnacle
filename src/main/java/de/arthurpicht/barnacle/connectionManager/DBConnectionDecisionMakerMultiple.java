package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DBConnectionDecisionMakerMultiple extends DBConnectionDecisionMaker{

	// Map: String = daoPackageName
	private Map<String, ConnectionWrapper> dbConnections;
	
	// Map: String = canonicalClassName
	private Map<String, ConnectionWrapper> dbConnectionsCache;
	
	public DBConnectionDecisionMakerMultiple(Map<String, ConnectionWrapper> dbConnections) {
		this.dbConnections = dbConnections;
		this.dbConnectionsCache = Collections.synchronizedMap(new HashMap<String, ConnectionWrapper>());
	}

	/**
	 * Wählt für übergebenen Klassennamen die passende DB-Konfiguration aus.
	 * Wenn mehrere Konfigurationen passen, wird die diejenige zurückgeliefert,
	 * die am spezifischsten ist.
	 * 
	 */
	@Override
	public ConnectionWrapper getDBCoonectionByDaoClass(String canonicalClassName) throws DBConnectionException {
		
		// Wenn im Cache schon vorhanden, dann nimm Inhalt
		// Performance!
		if (this.dbConnectionsCache.containsKey(canonicalClassName)) {
//			System.out.println("Connection im Cache gefunden.");
			return this.dbConnectionsCache.get(canonicalClassName);
		}
		
		// Auswahl treffen.
		// 1. Es muss passen
		// 2. Wenn mehrere passen, dann nimm den mit der höchsten Spezifität,
		//    d.h. der längesten Package-Teilbezeichnung.
//		System.out.println("Totale Anazahl der DBConnections: " + this.dbConnections.keySet().size());
		
		Set<String> packageNames = this.dbConnections.keySet();
		
		int length=-1;
		String matchedPackageName = "";
		
		for (String packageName : packageNames) {
			
			// Match? Und mit größerer Spezifität?
			if (canonicalClassName.startsWith(packageName) && packageName.length() > length) {
				matchedPackageName = packageName;
				length = packageName.length();
			}
		}
		
//		System.out.println("Gematchte Package-Deklarationen: " + matchedPackageName);
		
		// Wenn nichts zutreffen ist, dann Exception werfen
		if (length < 0) {
			throw new DBConnectionException("Keine DB-Konfiguration gefunden, die zu folgender Klasse passt: " + canonicalClassName);
		}
		
		// Im Cache unter Klassenname hinterlegen und zurückgeben.
		ConnectionWrapper connectionWrapper = this.dbConnections.get(matchedPackageName);
		
//		// TODO debug
//		if (connectionWrapper == null) {
//			System.out.println("------------");
//			System.out.println("Entahltene Packagedeklarationen:");
//			for (String packageName : packageNames) {
//				System.out.println(packageName);
//			}
//			
//			throw new RuntimeException("Ausgewählter ConnectionWrapper ist null.");
//		}
		
		this.dbConnectionsCache.put(canonicalClassName, connectionWrapper);
		
		return connectionWrapper;
	}

	
}
