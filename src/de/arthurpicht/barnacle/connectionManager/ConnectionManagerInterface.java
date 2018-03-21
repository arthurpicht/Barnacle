package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;

/**
 * Arthur Picht, parcs IT-Consulting GmbH, 22.02.18.
 */
public interface ConnectionManagerInterface {

    public Connection openConnection(Class<?> callingDaoClass) throws DBConnectionException;

    public void releaseConnection(Connection con, Class<?> callingDaoClass) throws DBConnectionException;

}
