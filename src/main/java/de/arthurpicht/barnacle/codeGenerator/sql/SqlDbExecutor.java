package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.codeGenerator.CodeGeneratorException;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.connectionManager.ConnectionManagerBackend;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlDbExecutor extends ConnectionManagerBackend {
	
	private final Connection connection;
	private final String daoDummyClassname;
	
	public SqlDbExecutor(GeneratorConfiguration generatorConfiguration) {
		this.daoDummyClassname = generatorConfiguration.getDaoPackageName() + ".Dummy";
		try {
			this.connection = openConnection(daoDummyClassname);
		} catch (DBConnectionException e) {
			throw new CodeGeneratorException(e.getMessage(), e);
		}
	}

	public void executeSql(String sqlString) {
		Statement statement;
		try {
			statement = this.connection.createStatement();
			statement.execute(sqlString);
		} catch (SQLException e) {
			throw new CodeGeneratorException(e.getMessage(), e);
		}
	}
	
	public void close() throws DBConnectionException {
		releaseConnection(this.connection, daoDummyClassname);
	}

}
