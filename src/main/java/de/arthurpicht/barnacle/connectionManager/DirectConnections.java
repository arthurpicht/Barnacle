package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DirectConnections extends DBConnectionType {

	private final String jdbcConnectionString;
	
	public DirectConnections(DBConfiguration dbConfiguration) {
		super(dbConfiguration);
		this.jdbcConnectionString = ConnectionHelper.getJDBCConnectionString(dbConfiguration);
	}
	
	public Connection getConnection() throws DBConnectionException {
		try {
			String driverName = this.dbConfiguration.getDriverName();
			Class.forName(driverName);
			return DriverManager.getConnection(this.jdbcConnectionString);
		} catch (ClassNotFoundException | SQLException e) {
			throw new DBConnectionException(e);
		}
	}
	
	public void releaseConnection(Connection con) throws DBConnectionException {
		try {
			con.close();
		} catch (SQLException e) {
			throw new DBConnectionException(e);
		}
	}

}
