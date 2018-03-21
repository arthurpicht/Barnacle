package de.arthurpicht.barnacle.connectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.arthurpicht.barnacle.exceptions.DBConnectionException;

public class DirectConnections extends DBConnectionType {

	private String jdbcConnectionString;
	
	public DirectConnections(DBConfiguration dbConfiguration) {
		super(dbConfiguration);
		this.jdbcConnectionString = ConnectionHelper.getJDBCConnectionString(dbConfiguration);
	}
	
	/**
	 * Öffnet eine JDBC-Verbindung zum konfigurierten DB-Server.
	 */
	public Connection getConnection() throws DBConnectionException {
	

		try {			
			
			String driverName = this.dbConfiguration.getDriverName();
			Class.forName(driverName).newInstance();

			Connection con = DriverManager.getConnection(this.jdbcConnectionString);

			return con;
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			throw new DBConnectionException(e);
		}

	}
	
	/**
	 * Schließt eine offene JDBC-Verbindung zum konfigurierten DB-Server.
	 */
	public void releaseConnection(Connection con) throws DBConnectionException {		
		try {
			con.close();
//			System.out.println("Connection closed");
		} catch (SQLException e) {
			throw new DBConnectionException(e);
		}
	}


}
