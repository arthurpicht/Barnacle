package de.arthurpicht.barnacle.connectionManager.connection;

import de.arthurpicht.barnacle.configuration.db.DbConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.direct.DirectJDBCConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jdbc.single.SingleJDBCConnectionConfiguration;
import de.arthurpicht.barnacle.configuration.db.jndi.JNDIConfiguration;
import de.arthurpicht.barnacle.exceptions.BarnacleIllegalStateException;

public class DbConnectionFactory {

    public static DbConnection getConnection(DbConnectionConfiguration dbConnectionConfiguration) {
        if (dbConnectionConfiguration instanceof DirectJDBCConnectionConfiguration) {
            return new DirectJDBCConnection((DirectJDBCConnectionConfiguration) dbConnectionConfiguration);
        } else if (dbConnectionConfiguration instanceof SingleJDBCConnectionConfiguration) {
            return new SingleJDBCConnection((SingleJDBCConnectionConfiguration) dbConnectionConfiguration);
        } else if (dbConnectionConfiguration instanceof JNDIConfiguration) {
            return new JNDIConnection((JNDIConfiguration) dbConnectionConfiguration);
        } else {
            throw new BarnacleIllegalStateException("Unknown DbConnection type: "
                    + dbConnectionConfiguration.getClass().getCanonicalName());
        }
    }

}
