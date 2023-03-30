package de.arthurpicht.barnacle.configuration.db.single;

import java.util.Map;

public class JDBCConfigurationBF {

    public static final String DAO_PACKAGE = "dao_package";
    public static final String DRIVER_NAME = "driver_name";
    public static final String URL = "url";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String PROPERTIES = "properties";

    protected String daoPackage;
    protected String driverName;
    protected String url;
    protected String user;
    protected String password;
    protected Map<String, String> properties;

}
