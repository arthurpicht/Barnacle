package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.configurationFile.BarnacleConfigurationFile;
import de.arthurpicht.barnacle.configuration.configurationFile.BarnacleConfigurationFileLoader;
import de.arthurpicht.barnacle.configuration.db.DBConfigurationOLD;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfigurationFactory;
import de.arthurpicht.barnacle.configuration.helper.ConfigurationHelper;
import de.arthurpicht.barnacle.connectionManager.connection.DbConnection;
import de.arthurpicht.barnacle.connectionManager.connection.DbConnectionFactory;
import de.arthurpicht.barnacle.exceptions.BarnacleInitializerException;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.configuration.Configuration;
import de.arthurpicht.configuration.ConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectionManagerBackend {

    private final static Logger logger = LoggerFactory.getLogger(ConnectionManagerBackend.class);
    private static final ConnectionResolver connectionResolver;
    private static final ConnectionCash connectionCash;

    static {
        Map<String, DbConnection> dbConnections = new HashMap<>();

        BarnacleConfigurationFile barnacleConfigurationFile = new BarnacleConfigurationFile();
        BarnacleConfiguration barnacleConfiguration = barnacleConfigurationFile.getBarnacleConfiguration();
        if (!barnacleConfiguration.hasDbConnectionConfigurations())
            throw new BarnacleInitializerException("No database configurations found in barnacle configuration file.");

        List<DbConnectionConfiguration> dbConnectionConfigurations = barnacleConfiguration.getDbConnectionConfigurations();
        for (DbConnectionConfiguration dbConnectionConfiguration : dbConnectionConfigurations) {
            DbConnection dbConnection = DbConnectionFactory.getConnection(dbConnectionConfiguration);
            dbConnections.put(dbConnectionConfiguration.getDaoPackage(), dbConnection);
        }

        connectionResolver = new ConnectionResolver(dbConnections);
        connectionCash = new ConnectionCash();
    }

    /**
     * Return open JDBC-Connection.
     */
    public static Connection openConnection(Class<?> daoClass) throws DBConnectionException {
        DbConnection dbConnection = obtainConnection(daoClass);
        return dbConnection.getConnection();
    }

    /**
     * Defines a JDBC-Connection as closed, that was obtained before by invoking method
     * openConnection.
     */
    public static void releaseConnection(Connection con, Class<?> daoClass) throws DBConnectionException {
        DbConnection dbConnection = obtainConnection(daoClass);
        dbConnection.releaseConnection(con);
    }

    private static DbConnection obtainConnection(Class<?> daoClass) throws DBConnectionException {
        if (connectionCash.hasDaoClass(daoClass)) {
            return connectionCash.getDbConnection(daoClass);
        } else {
            DbConnection dbConnection = connectionResolver.getDbConnection(daoClass);
            connectionCash.putDbConnection(daoClass, dbConnection);
            return dbConnection;
        }
    }

}
