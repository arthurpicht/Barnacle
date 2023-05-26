package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;

/**
 * Arthur Picht, Düsseldorf, 2018
 */
public class ConnectionManager {

    public static void initialize(BarnacleConfiguration barnacleConfiguration) {
        ConnectionManagerBackend.init(barnacleConfiguration);
    }
    public static Connection openConnection(Class<?> callingDaoClass) throws DBConnectionException {
        return ConnectionManagerBackend.openConnection(callingDaoClass);
    }

    public static void releaseConnection(Connection con, Class<?> callingDaoClass) throws DBConnectionException {
        ConnectionManagerBackend.releaseConnection(con, callingDaoClass);
    }

}
