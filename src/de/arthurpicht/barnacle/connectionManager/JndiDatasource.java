package de.arthurpicht.barnacle.connectionManager;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import de.arthurpicht.barnacle.exceptions.DBConnectionException;

public class JndiDatasource extends DBConnectionType {
	
	public JndiDatasource(DBConfiguration dbConfiguration) {
		super(dbConfiguration);
	}
	
	public Connection getConnection() throws DBConnectionException {
		
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/MySQLDB");
			Connection con = ds.getConnection();
			
			return con;
			
		} catch (NamingException e) {
			throw new DBConnectionException(e);
		} catch (SQLException e) {
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
