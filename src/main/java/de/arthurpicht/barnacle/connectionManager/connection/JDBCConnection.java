package de.arthurpicht.barnacle.connectionManager.connection;

import de.arthurpicht.barnacle.configuration.db.jdbc.JDBCConfiguration;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class JDBCConnection extends DbConnection {

    protected JDBCConfiguration jdbcConfiguration;

    public JDBCConnection(JDBCConfiguration jdbcConfiguration) {
        super(jdbcConfiguration);
        this.jdbcConfiguration = jdbcConfiguration;
    }

    protected Connection getJdbcConnection() throws DBConnectionException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.jdbcConfiguration.getUser());
        connectionProps.put("password", this.jdbcConfiguration.getPassword());
        connectionProps.putAll(this.jdbcConfiguration.getProperties());
        try {
            Class.forName(this.jdbcConfiguration.getDriverName());
            return DriverManager.getConnection(this.jdbcConfiguration.getUrl(), connectionProps);
        } catch (ClassNotFoundException | SQLException e) {
            throw new DBConnectionException(e);
        }
    }

}
