package de.arthurpicht.barnacle.configuration;

import de.arthurpicht.barnacle.BarnacleInitializer;
import de.arthurpicht.barnacle.connectionManager.ConnectionManager;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.barnacle.exceptions.EntityNotFoundException;
import de.arthurpicht.utils.core.strings.Strings;

public class GeneratorConfigurationBuilder {

    private BarnacleInitializer.Dialect dialect;
    private String srcDir;
    private String srcGenDir;
    private final String vofPackageName;
    private final String voPackageName;
    private final String vobPackageName;
    private final String daoPackageName;
    private boolean executeOnDb;
    private boolean createScript;
    private String scriptFile;
    private BarnacleInitializer.Encoding encodingDB;
    private BarnacleInitializer.Encoding encodingSource;
    private String connectionManagerCanonicalClassName;
    private String connectionExceptionCanonicalClassName;
    private String entityNotFoundExceptionCanonicalClassName;
    private String daoLoggerName;

    public GeneratorConfigurationBuilder(
            String vofPackageName,
            String voPackageName,
            String vobPackageName,
            String daoPackageName
    ) {
        this.dialect = BarnacleInitializer.Dialect.MYSQL;
        this.srcDir = "src";
        this.srcGenDir = "src-gen/";
        this.vofPackageName = vofPackageName;
        this.voPackageName = voPackageName;
        this.vobPackageName = vobPackageName;
        this.daoPackageName = daoPackageName;
        this.executeOnDb = false;
        this.createScript = false;
        this.scriptFile = "barnacle.sql";
        this.encodingDB = BarnacleInitializer.Encoding.DEFAULT;
        this.encodingSource = BarnacleInitializer.Encoding.UTF;
        this.connectionManagerCanonicalClassName = ConnectionManager.class.getCanonicalName();
        this.connectionExceptionCanonicalClassName = DBConnectionException.class.getCanonicalName();
        this.entityNotFoundExceptionCanonicalClassName = EntityNotFoundException.class.getCanonicalName();
        this.daoLoggerName = "";
    }

    public GeneratorConfigurationBuilder withDialect(BarnacleInitializer.Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    public GeneratorConfigurationBuilder withSrcDir(String srcDir) {
        this.srcDir = srcDir;
        return this;
    }

    public GeneratorConfigurationBuilder withSrcGenDir(String srcGenDir) {
        this.srcGenDir = srcGenDir;
        return this;
    }

    public GeneratorConfigurationBuilder withExecuteOnDb(boolean executeOnDb) {
        this.executeOnDb = executeOnDb;
        return this;
    }

    public GeneratorConfigurationBuilder withCreateScript(boolean createScript) {
        this.createScript = createScript;
        return this;
    }

    public GeneratorConfigurationBuilder withScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
        return this;
    }

    public GeneratorConfigurationBuilder withEncodingDB(BarnacleInitializer.Encoding encodingDB) {
        this.encodingDB = encodingDB;
        return this;
    }

    public GeneratorConfigurationBuilder withEncodingSource(BarnacleInitializer.Encoding encodingSource) {
        this.encodingSource = encodingSource;
        return this;
    }

    public GeneratorConfigurationBuilder withConnectionManagerCanonicalClassName(String connectionExceptionCanonicalClassName) {
        this.connectionManagerCanonicalClassName = connectionExceptionCanonicalClassName;
        return this;
    }

    public GeneratorConfigurationBuilder withConnectionExceptionCanonicalClassName(String connectionExceptionCanonicalClassName) {
        this.connectionExceptionCanonicalClassName = connectionExceptionCanonicalClassName;
        return this;
    }

    public GeneratorConfigurationBuilder withEntityNotFoundExceptionCanonicalClassName(String entityNotFoundExceptionCanonicalClassName) {
        this.entityNotFoundExceptionCanonicalClassName = entityNotFoundExceptionCanonicalClassName;
        return this;
    }

    public GeneratorConfigurationBuilder withDaoLoggerName(String daoLoggerName) {
        this.daoLoggerName = daoLoggerName;
        return this;
    }

    public GeneratorConfiguration build() {
        return new GeneratorConfiguration(
                this.dialect,
                this.srcDir,
                this.srcGenDir,
                this.vofPackageName,
                this.voPackageName,
                this.vobPackageName,
                this.daoPackageName,
                this.executeOnDb,
                this.createScript,
                this.scriptFile,
                this.encodingDB,
                this.encodingSource,
                this.connectionManagerCanonicalClassName,
                this.connectionExceptionCanonicalClassName,
                this.entityNotFoundExceptionCanonicalClassName,
                this.daoLoggerName
        );
    }

}
