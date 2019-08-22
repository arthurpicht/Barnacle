package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;

/**
 * Arthur Picht, Düsseldorf, 2018
 */
public class ConnectionManager {

    public static Connection openConnection(Class<?> callingDaoClass) throws DBConnectionException {

        String canonicalClassName = callingDaoClass.getCanonicalName();
        return ConnectionManagerBackend.openConnection(canonicalClassName);
    }

    public static void releaseConnection(Connection con, Class<?> callingDaoClass) throws DBConnectionException {

        String canonicalClassName = callingDaoClass.getCanonicalName();
        ConnectionManagerBackend.releaseConnection(con, canonicalClassName);
    }

}
