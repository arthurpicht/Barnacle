package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.configuration.db.DBConfigurationOLD;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * Kapselt verschiedene Typen von Connections in einem Wrapper-Objekt,
 * welche in Abhängigkeit von der zugehörigen Sektion der Konfiguration
 * initialisiert werden.
 * 
 * @author Picht
 *
 */
public class ConnectionWrapper {
	
	private static final int SINGLE_CONNECTION = 1;
	private static final int DIRECT_CONNECTIONS = 2;
	private static final int JNDI_DATASOURCE = 3;
	private static final int CONNECTION_POOL = 4;

	protected static final Logger logger = LoggerFactory.getLogger(ConnectionWrapper.class);
	
	private final DBConnectionType dbConnectionType;
	
	public ConnectionWrapper(DBConfigurationOLD dbConfiguration) {
		
		int connection_type = dbConfiguration.getConnectionType();
		
		String logString = "Barnacle Connection Manager initialized section [" + dbConfiguration.getSectionName() + "] with connection type ";

		if (connection_type == SINGLE_CONNECTION) {			
			logger.info(logString + "SINGLE_CONNECTION.");			
			this.dbConnectionType = new SingleConnection(dbConfiguration);
			
		} else if (connection_type == DIRECT_CONNECTIONS) {			
			logger.info(logString + "DIRECT_CONNECTIONS.");
			this.dbConnectionType = new DirectConnections(dbConfiguration);
			
		} else if (connection_type == JNDI_DATASOURCE) {			
			logger.error(logString + "JNDI_DATASOURCE.");
			this.dbConnectionType = new JndiDatasource(dbConfiguration);
			
		} else if (connection_type == CONNECTION_POOL) {
			logger.error(logString + "CONNECTION_POOL ... not implemented yet.");
			throw new RuntimeException(logString + "CONNECTION_POOL ... not implemented yet.");
		} else {
			String errorLog="Barnacle Connection Manager could not be initialized for section + [" + dbConfiguration.getSectionName() + "] due to missing parameter 'connection_type' in configuration.";
			logger.error(errorLog);
			throw new RuntimeException(errorLog);
		}
	}
	
	public Connection openConnection() throws DBConnectionException {
		return this.dbConnectionType.getConnection();
	}
	
	public void releaseConnection(Connection con) throws DBConnectionException {
		this.dbConnectionType.releaseConnection(con);
	}

}
