package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.connectionManager.connection.DbConnection;
import de.arthurpicht.barnacle.connectionManager.connection.DbConnectionFactory;
import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.console.Console;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlDbExecutor {

    public static void execute(BarnacleConfiguration barnacleConfiguration, SqlStatements sqlStatements) {
        Console.verbose("Executing SQL schema creation on DB.");
        if (!barnacleConfiguration.hasDbConnectionConfigurations())
            throw new BarnacleRuntimeException("Could not execute schema creation on DB. No db connection " +
                    "configuration found in barnacle configuration.");

        DbConnectionConfiguration dbConnectionConfiguration = barnacleConfiguration.getDbConnectionConfigurations().get(0);
        DbConnection dbConnection = DbConnectionFactory.getConnection(dbConnectionConfiguration);
        executeOnDb(sqlStatements, dbConnection);
    }

    private static void executeOnDb(SqlStatements sqlStatements, DbConnection dbConnection) {
        try {
            Connection connection = dbConnection.getConnection();
            for (String sqlString : sqlStatements.getSqlStatementList()) {
                Statement statement = connection.createStatement();
                statement.execute(sqlString);
            }
            connection.close();
        } catch (DBConnectionException | SQLException e) {
            throw new BarnacleRuntimeException("Error on executing schema sql on db.", e);
        }
    }

}
