package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.connectionManager.connection.DbConnection;
import de.arthurpicht.barnacle.exceptions.BarnacleIllegalStateException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConnectionCash {

    private final Map<String, DbConnection> cache;

    public ConnectionCash() {
        this.cache = Collections.synchronizedMap(new HashMap<>());
    }

    public boolean hasDaoClass(Class<?> daoClass) {
        return this.cache.containsKey(daoClass.getCanonicalName());
    }

    public DbConnection getDbConnection(Class<?> daoClass) {
        DbConnection dbConnection = this.cache.get(daoClass.getCanonicalName());
        if (dbConnection == null)
            throw new BarnacleIllegalStateException("No DbConnection for specified DAO class. Check before requesting.");
        return dbConnection;
    }

    public void putDbConnection(Class<?> daoClass, DbConnection dbConnection) {
        this.cache.put(daoClass.getCanonicalName(), dbConnection);
    }

}
