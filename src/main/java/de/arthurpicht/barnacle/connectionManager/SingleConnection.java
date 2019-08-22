package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingleConnection extends DBConnectionType {
	
	private Connection connection = null;
	private String jdbcConnectionString;
	
	public SingleConnection(DBConfiguration dbConfiguration) {
		super(dbConfiguration);
		this.connection = null;
		this.jdbcConnectionString = ConnectionHelper.getJDBCConnectionString(dbConfiguration);
	}
	
	public Connection getConnection() throws DBConnectionException {
		
		try {			
			
			if ((this.connection != null) && (!this.connection.isClosed())) {
				
				return this.connection;
				
			} else {
			
				String driverName = this.dbConfiguration.getDriverName();
				Class.forName(driverName).newInstance();
				
				Connection con = DriverManager.getConnection(this.jdbcConnectionString);
				
				this.connection = con;
				
				return con;
			}
			
		} catch (InstantiationException e) {
			throw new DBConnectionException(e);
		} catch (IllegalAccessException e) {
			throw new DBConnectionException(e);
		} catch (ClassNotFoundException e) {
			throw new DBConnectionException(e);
		} catch (SQLException e) {
			throw new DBConnectionException(e);
		}
	}

	@Override
	public void releaseConnection(Connection con) throws DBConnectionException {
		// do nothing
	}
	
	

}
