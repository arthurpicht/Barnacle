package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;

public abstract class DBConnectionType {
	
	protected DBConfiguration dbConfiguration;
	
	public DBConnectionType(DBConfiguration dbConfiguration) {
		this.dbConfiguration = dbConfiguration;
	}
	
	public abstract Connection getConnection() throws DBConnectionException;
	
	public abstract void releaseConnection(Connection con) throws DBConnectionException;
	
	public DBConfiguration getDBConfiguration() {
		return this.dbConfiguration;
	}

}
