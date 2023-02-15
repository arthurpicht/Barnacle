package de.arthurpicht.barnacle.configuration;

import de.arthurpicht.barnacle.BarnacleInitializer;
import de.arthurpicht.barnacle.exceptions.BarnacleInitializerException;
import de.arthurpicht.configuration.Configuration;

import static de.arthurpicht.barnacle.configuration.GeneratorConfiguration.*;

public class GeneratorConfigurationFactory {

    public static GeneratorConfiguration create(Configuration configuration) {

        String vofPackageName = getMandatoryStringParameter(configuration, VOF_PACKAGE_NAME);
        String voPackageName = getMandatoryStringParameter(configuration, VO_PACKAGE_NAME);
        String vobPackageName = getMandatoryStringParameter(configuration, VOB_PACKAGE_NAME);
        String daoPackageName = getMandatoryStringParameter(configuration, DAO_PACKAGE_NAME);

        GeneratorConfigurationBuilder generatorConfigurationBuilder = new GeneratorConfigurationBuilder(
                vofPackageName,
                voPackageName,
                vobPackageName,
                daoPackageName
        );

        if (configuration.containsKey(DIALECT)) {
            String dialectString = configuration.getString(DIALECT);
            if (dialectString.toUpperCase().equals(BarnacleInitializer.Dialect.MYSQL.name())) {
                generatorConfigurationBuilder.withDialect(BarnacleInitializer.Dialect.MYSQL);
            } else if (dialectString.toUpperCase().equals(BarnacleInitializer.Dialect.H2.name())) {
                generatorConfigurationBuilder.withDialect(BarnacleInitializer.Dialect.H2);
            } else {
                throw new BarnacleInitializerException("Illegaler Parameter für 'dialect' in [generator]-Sektion " +
                        "von barnacle.conf: " + dialectString);
            }
        }

        if (configuration.containsKey(SRC_DIR))
            generatorConfigurationBuilder.withSrcDir(configuration.getString(SRC_DIR));

        if (configuration.containsKey(SRC_GEN_DIR)) {
            String srcGenDir = configuration.getString("src_gen_dir", "src-gen");
            if (!srcGenDir.endsWith("/")) srcGenDir += "/";
            generatorConfigurationBuilder.withSrcGenDir(srcGenDir);
        }

        if (configuration.containsKey(EXECUTE_ON_DB))
            generatorConfigurationBuilder.withExecuteOnDb(
                    configuration.getBoolean(EXECUTE_ON_DB));

        if (configuration.containsKey(CREATE_SCRIPT))
            generatorConfigurationBuilder.withCreateScript(
                    configuration.getBoolean(CREATE_SCRIPT));

        if (configuration.containsKey(SCRIPT_FILE))
            generatorConfigurationBuilder.withScriptFile(
                    configuration.getString(SCRIPT_FILE));

        if (configuration.containsKey(ENCODING_DB)) {
            String encoding_db = configuration.getString("encoding_db");
            if (encoding_db.equals(BarnacleInitializer.Encoding.DEFAULT.name())) {
                generatorConfigurationBuilder.withEncodingDB(BarnacleInitializer.Encoding.DEFAULT);
            } else if (encoding_db.equals(BarnacleInitializer.Encoding.ISO.name())) {
                generatorConfigurationBuilder.withEncodingDB(BarnacleInitializer.Encoding.ISO);
            } else if (encoding_db.equals(BarnacleInitializer.Encoding.UTF.name())) {
                generatorConfigurationBuilder.withEncodingDB(BarnacleInitializer.Encoding.UTF);
            } else {
                throw new BarnacleInitializerException("Illegaler Parameter für 'encoding_db' in [generator]-Sektion von " +
                        "barnacle.conf: " + encoding_db);
            }
        }

        if (configuration.containsKey(CONNECTION_MANAGER_CLASS))
            generatorConfigurationBuilder.withConnectionManagerCanonicalClassName(
                    configuration.getString(CONNECTION_MANAGER_CLASS));

        if (configuration.containsKey(CONNECTION_EXCEPTION_CLASS))
            generatorConfigurationBuilder.withConnectionExceptionCanonicalClassName(
                    configuration.getString(CONNECTION_EXCEPTION_CLASS));

        if (configuration.containsKey(ENTITY_NOT_FOUND_EXCEPTION_CLASS))
            generatorConfigurationBuilder.withEntityNotFoundExceptionCanonicalClassName(
                    configuration.getString(ENTITY_NOT_FOUND_EXCEPTION_CLASS));

        if (configuration.containsKey(DAO_LOGGER_NAME))
            generatorConfigurationBuilder.withDaoLoggerName(
                    configuration.getString(DAO_LOGGER_NAME));

        return generatorConfigurationBuilder.build();
    }

    private static String getMandatoryStringParameter(Configuration configuration, String parameterName) {
        if (!configuration.containsKey(parameterName))
            throw new MandatoryConfigParameterMissing(parameterName);
        return configuration.getString(parameterName);
    }

}
