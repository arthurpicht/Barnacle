package de.arthurpicht.barnacle.connectionManager.connection;

import de.arthurpicht.barnacle.configuration.db.jndi.JNDIConfiguration;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JNDIConnection extends DbConnection {

	private final JNDIConfiguration jndiConfiguration;

	public JNDIConnection(JNDIConfiguration jndiConfiguration) {
		super(jndiConfiguration);
		this.jndiConfiguration = jndiConfiguration;
	}
	
	public Connection getConnection() throws DBConnectionException {
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(this.jndiConfiguration.getLookupName());
//			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/MySQLDB");
			return ds.getConnection();
		} catch (NamingException | SQLException e) {
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
