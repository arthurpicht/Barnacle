package de.arthurpicht.barnacle.configuration.db;

import de.arthurpicht.configuration.Configuration;

public class DBConfigurationOLD {
	
	private final Configuration configuration;

	private final String daoPackageName;
	private final String driverName;
	private final String dbName;
	private final String dbHost;
	private final String dbUser;
	private final String dbPassword;
	private final String rdbms;
	private final int connectionType;
	
	public DBConfigurationOLD(Configuration configuration) {
		this.configuration = configuration;
		
		this.daoPackageName = configuration.getString("dao_package");
		this.driverName = configuration.getString("driver_name");
		this.dbName = configuration.getString("db_name");
		this.dbHost = configuration.getString("db_host");
		this.dbUser = configuration.getString("db_user");
		this.dbPassword = configuration.getString("db_password");
		this.rdbms = configuration.getString("rdbms");
		this.connectionType = configuration.getInt("connection_type");
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public String getDaoPackageName() {
		return daoPackageName;
	}

	public String getDriverName() {
		return driverName;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbHost() {
		return dbHost;
	}

	public String getDbUser() {
		return dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public String getRdbms() {
		return rdbms;
	}

	public int getConnectionType() {
		return connectionType;
	}
	
	public String getSectionName() {
		return this.configuration.getSectionName();
	}

}
