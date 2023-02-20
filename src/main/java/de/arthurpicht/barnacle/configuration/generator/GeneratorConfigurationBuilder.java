package de.arthurpicht.barnacle.configuration.generator;

import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.connectionManager.ConnectionManager;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.barnacle.exceptions.EntityNotFoundException;

public class GeneratorConfigurationBuilder {

    private Const.Dialect dialect;
    private String srcDir;
    private String srcGenDir;
    private final String vofPackageName;
    private final String voPackageName;
    private final String vobPackageName;
    private final String daoPackageName;
    private boolean executeOnDb;
    private boolean createScript;
    private String scriptFile;
    private Const.Encoding encodingDB;
    private Const.Encoding encodingSource;
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
        this.dialect = Const.Dialect.MYSQL;
        this.srcDir = "src";
        this.srcGenDir = "src-gen/";
        this.vofPackageName = vofPackageName;
        this.voPackageName = voPackageName;
        this.vobPackageName = vobPackageName;
        this.daoPackageName = daoPackageName;
        this.executeOnDb = false;
        this.createScript = false;
        this.scriptFile = "barnacle.sql";
        this.encodingDB = Const.Encoding.DEFAULT;
        this.encodingSource = Const.Encoding.UTF;
        this.connectionManagerCanonicalClassName = ConnectionManager.class.getCanonicalName();
        this.connectionExceptionCanonicalClassName = DBConnectionException.class.getCanonicalName();
        this.entityNotFoundExceptionCanonicalClassName = EntityNotFoundException.class.getCanonicalName();
        this.daoLoggerName = "";
    }

    public GeneratorConfigurationBuilder withDialect(Const.Dialect dialect) {
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

    public GeneratorConfigurationBuilder withEncodingDB(Const.Encoding encodingDB) {
        this.encodingDB = encodingDB;
        return this;
    }

    public GeneratorConfigurationBuilder withEncodingSource(Const.Encoding encodingSource) {
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
