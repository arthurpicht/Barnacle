package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.configuration.BarnacleConfiguration;
import de.arthurpicht.barnacle.configuration.general.GeneralConfiguration;
import de.arthurpicht.barnacle.configuration.general.GeneralConfigurationFactory;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.configuration.Configuration;
import de.arthurpicht.configuration.ConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConnectionManagerBackend {

    private static final DBConnectionDecisionMaker dbConnectionDecisionMaker;

    protected static Logger BARNACLE_LOGGER;

    static {

        BarnacleConfiguration barnacleConfiguration = new BarnacleConfiguration();
        GeneralConfiguration generalConfiguration
                = GeneralConfigurationFactory.create(barnacleConfiguration.getGeneralConfiguration());

        // Logger initialisieren
        String loggerName = generalConfiguration.getLogger();
        BARNACLE_LOGGER = LoggerFactory.getLogger(loggerName);

        // Logging der Konfiguration, wenn angefordert
        if (generalConfiguration.isLogConfigOnInit()) {
            BARNACLE_LOGGER.info(Const.VERSION);
            BARNACLE_LOGGER.info("Barnacle configuration:");
            BARNACLE_LOGGER.info("[general]");
            logAllProperties(barnacleConfiguration.getGeneralConfiguration());

            if (barnacleConfiguration.hasGeneratorConfiguration()) {
                BARNACLE_LOGGER.info("[generator]");
                logAllProperties(barnacleConfiguration.getGeneratorConfiguration());
            }
        }

        //
        // Map daoPackage-ConnectionWrapper aufbauen, dann abschließend DecisionMaker davon erzeugen
        //
        Map<String, ConnectionWrapper> dbConnections = new HashMap<>();

        // Über alle Sektionen der Konfiguration mit Ausnahme
        // von [general] und [generator] iterieren
        ConfigurationFactory configurationFactory = barnacleConfiguration.getConfigurationFactory();
        Set<String> sectionNames = configurationFactory.getSectionNames();
        for (String sectionName : sectionNames) {
            if (sectionName.equals("general") || sectionName.equals("generator")) continue;

            // Config loggen, wenn angefordert
            Configuration configuration = configurationFactory.getConfiguration(sectionName);
            if (generalConfiguration.isLogConfigOnInit()) {
                BARNACLE_LOGGER.info("[" + configuration.getSectionName() + "]");
                logAllProperties(configuration);
            }

            // DBConfig ableiten
            DBConfiguration dbConfiguration = new DBConfiguration(configuration);

            // ConnectionWrapper erzeugen und hinterlegen
            ConnectionWrapper connectionWrapper = new ConnectionWrapper(dbConfiguration);
            String daoPackageName = dbConfiguration.getDaoPackageName();
            if (daoPackageName.endsWith("*")) {
                daoPackageName = daoPackageName.substring(0, daoPackageName.length() - 1);
            }
            dbConnections.put(daoPackageName, connectionWrapper);
        }

        // DecisionMaker erzeugen und als statische Property hinterlegen.
        dbConnectionDecisionMaker = DBConnectionDecisionMaker.getDBConnectionDecisionMaker(dbConnections);
    }

    private static void logAllProperties(Configuration configuration) {
        Set<String> keys = configuration.getKeys();
        for (String key : keys) {
            BARNACLE_LOGGER.info(key + " = " + configuration.getString(key));
        }
    }

    /**
     * Return open JDBC-Connection.
     */
    public static Connection openConnection(String daoCanonicalClassName) throws DBConnectionException {

        ConnectionWrapper connectionWrapper = dbConnectionDecisionMaker.getDBConnectionByDaoClass(daoCanonicalClassName);
        return connectionWrapper.openConnection();
    }

    /**
     * Defines a JDBC-Connection as closed, that was obtained before by invoking method
     * openConnection.
     */
    public static void releaseConnection(Connection con, String daoCanonicalClassName) throws DBConnectionException {

        ConnectionWrapper connectionWrapper = dbConnectionDecisionMaker.getDBConnectionByDaoClass(daoCanonicalClassName);
        connectionWrapper.releaseConnection(con);
    }

}
