package de.arthurpicht.barnacle.generator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.connectionManager.ConnectionManagerBackend;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;

public class SqlDbExecuter extends ConnectionManagerBackend {
	
	private static SqlDbExecuter sqlDbExecuter = null;
	private Connection connection;
	
	private String daoDummyClassname;
	
	private SqlDbExecuter() throws DBConnectionException {
		GeneratorConfiguration generatorConfiguration = GeneratorContext.getInstance().getGeneratorConfiguration();
		this.daoDummyClassname = generatorConfiguration.getDaoPackageName() + ".Dummy";
		this.connection = openConnection(daoDummyClassname);
	}
	
	public static SqlDbExecuter getInstance() throws DBConnectionException {
		if (sqlDbExecuter == null) {
			sqlDbExecuter = new SqlDbExecuter();
		}
		return sqlDbExecuter;
	}
	
	public void executeSql(String sqlString) throws DBConnectionException {
		Statement statement;
		try {
			statement = this.connection.createStatement();
			statement.execute(sqlString);
		} catch (SQLException e) {
			throw new DBConnectionException(e);
		}
	}
	
	public void close() throws DBConnectionException {
		releaseConnection(this.connection, daoDummyClassname);
	}
	

}
