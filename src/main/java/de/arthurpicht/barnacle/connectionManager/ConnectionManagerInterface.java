package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;

@Deprecated
public interface ConnectionManagerInterface {

    Connection openConnection(Class<?> callingDaoClass) throws DBConnectionException;

    void releaseConnection(Connection con, Class<?> callingDaoClass) throws DBConnectionException;

}
