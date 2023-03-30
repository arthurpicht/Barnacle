package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.configuration.db.DBConfigurationOLD;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;

public abstract class DBConnectionType {
	
	protected final DBConfigurationOLD dbConfiguration;
	
	public DBConnectionType(DBConfigurationOLD dbConfiguration) {
		this.dbConfiguration = dbConfiguration;
	}
	
	public abstract Connection getConnection() throws DBConnectionException;
	
	public abstract void releaseConnection(Connection con) throws DBConnectionException;
	
	public DBConfigurationOLD getDBConfiguration() {
		return this.dbConfiguration;
	}

}
