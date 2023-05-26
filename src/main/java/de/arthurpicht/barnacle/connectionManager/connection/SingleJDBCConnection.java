package de.arthurpicht.barnacle.connectionManager.connection;

import de.arthurpicht.barnacle.configuration.db.jdbc.single.SingleJDBCConnectionConfiguration;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;
import java.sql.SQLException;

public class SingleJDBCConnection extends JDBCConnection {
	
	private Connection connection;

	public SingleJDBCConnection(SingleJDBCConnectionConfiguration singleJDBCConnectionConfiguration) {
		super(singleJDBCConnectionConfiguration.asJdbcConfiguration());
		this.connection = null;
	}
	
	@Override
	public Connection getConnection() throws DBConnectionException {
		try {
			if ((this.connection != null) && (!this.connection.isClosed())) {
				return this.connection;
			} else {
				this.connection = this.getJdbcConnection();
				return this.connection;
			}
		} catch (SQLException e) {
			throw new DBConnectionException(e);
		}
	}

	@Override
	public void releaseConnection(Connection con) throws DBConnectionException {
		// do intentionally nothing
	}

}
