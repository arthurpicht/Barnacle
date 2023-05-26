package de.arthurpicht.barnacle.connectionManager.connection;

import de.arthurpicht.barnacle.configuration.db.jdbc.direct.DirectJDBCConnectionConfiguration;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;
import java.sql.SQLException;

public class DirectJDBCConnection extends JDBCConnection {

	public DirectJDBCConnection(DirectJDBCConnectionConfiguration directJDBCConnectionConfiguration) {
		super(directJDBCConnectionConfiguration.asJdbcConfiguration());
	}

	@Override
	public Connection getConnection() throws DBConnectionException {
		return this.getJdbcConnection();
	}

	@Override
	public void releaseConnection(Connection con) throws DBConnectionException {
		try {
			con.close();
		} catch (SQLException e) {
			throw new DBConnectionException(e);
		}
	}

}
