package de.arthurpicht.barnacle.connectionManager.connection;

import de.arthurpicht.barnacle.configuration.db.DBConfigurationOLD;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;

public abstract class DbConnection {
	
	protected final DbConnectionConfiguration dbConnectionConfiguration;
	
	public DbConnection(DbConnectionConfiguration dbConnectionConfiguration) {
		this.dbConnectionConfiguration = dbConnectionConfiguration;
	}
	
	public abstract Connection getConnection() throws DBConnectionException;
	
	public abstract void releaseConnection(Connection con) throws DBConnectionException;
}
