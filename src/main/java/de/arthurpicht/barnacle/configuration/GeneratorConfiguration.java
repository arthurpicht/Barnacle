package de.arthurpicht.barnacle.configuration;

import de.arthurpicht.barnacle.BarnacleInitializer;
import de.arthurpicht.barnacle.connectionManager.ConnectionManager;
import de.arthurpicht.barnacle.exceptions.BarnacleInititalizerException;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.barnacle.exceptions.EntityNotFoundException;
import de.arthurpicht.configuration.Configuration;

public class GeneratorConfiguration {

    private String srcDir;
    private String srcGenDir;
    private String vofPackageName;
    private String voPackageName;
    private String vobPackageName;
    private String daoPackageName;
    private boolean executeOnDb = true;
    private boolean createScript = true;
    private String scriptFile = "barnacle.sql";
    private BarnacleInitializer.Encoding encodingDB;
    private BarnacleInitializer.Encoding encodingSource;
    private String connectionManagerCanonicalClassName;
    private String connectionExceptionCanonicalClassName;
    private String entityNotFoundExceptionCanonicalClassName;
    private String daoLoggerName;

    public GeneratorConfiguration(Configuration configuration) {

        // TODO Rework, validate catch KeyNotFoundExceptions for mandatory properties, throw Exception

        this.srcDir = configuration.getString("src_dir", "src");

        this.srcGenDir = configuration.getString("src_gen_dir", "src-gen");
        if (!this.srcGenDir.endsWith("/")) {
            this.srcGenDir += "/";
        }

        this.vofPackageName = configuration.getString("vof_package_name");

        this.voPackageName = configuration.getString("vo_package_name");

        this.vobPackageName = configuration.getString("vob_package_name");

        this.daoPackageName = configuration.getString("dao_package_name");

        this.executeOnDb = configuration.getBoolean("execute_on_db", false);

        this.createScript = configuration.getBoolean("create_skript", false);

        this.scriptFile = configuration.getString("script_file");

        String encoding_db = configuration.getString("encoding_db", "DEFAULT");
        if (encoding_db.equals(BarnacleInitializer.Encoding.DEFAULT.name())) {
            this.encodingDB = BarnacleInitializer.Encoding.DEFAULT;
        } else if (encoding_db.equals(BarnacleInitializer.Encoding.ISO.name())) {
            this.encodingDB = BarnacleInitializer.Encoding.ISO;
        } else if (encoding_db.equals(BarnacleInitializer.Encoding.UTF.name())) {
            this.encodingDB = BarnacleInitializer.Encoding.UTF;
        } else {
            throw new BarnacleInititalizerException("Illegaler Parameter für 'encoding_db' in [generator]-Sektion von barnacle.conf: " + encoding_db);
        }

        String encoding_source = configuration.getString("encoding_source", "UTF");
        if (encoding_source.equals(BarnacleInitializer.Encoding.DEFAULT.name())) {
            this.encodingSource = BarnacleInitializer.Encoding.DEFAULT;
        } else if (encoding_source.equals(BarnacleInitializer.Encoding.ISO.name())) {
            this.encodingSource = BarnacleInitializer.Encoding.ISO;
        } else if (encoding_source.equals(BarnacleInitializer.Encoding.UTF.name())) {
            this.encodingSource = BarnacleInitializer.Encoding.UTF;
        } else {
            throw new BarnacleInititalizerException("Illegaler Parameter für 'encoding_source' in [generator]-Sektion von barnacle.conf: " + encoding_db);
        }

        this.connectionManagerCanonicalClassName = configuration.getString("connection_manager_class", ConnectionManager.class.getCanonicalName());

        this.connectionExceptionCanonicalClassName = configuration.getString("connection_exception_class", DBConnectionException.class.getCanonicalName());

        this.entityNotFoundExceptionCanonicalClassName = configuration.getString("entity_not_found_exception_class", EntityNotFoundException.class.getCanonicalName());

        this.daoLoggerName = configuration.getString("dao_logger_name", "");

    }

    public void validate() throws BarnacleInititalizerException {

        if (this.srcDir == null || this.srcDir.equals("")) throw new BarnacleInititalizerException("Generator-Parameter 'src_dir' ist nicht gesetzt oder leer.");
        if (this.srcGenDir == null || this.srcGenDir.equals("")) throw new BarnacleInititalizerException("Generator-Parameter 'src_gen_dir' ist nicht gesetzt oder leer.");
        if (this.vofPackageName == null || this.vofPackageName.equals("")) throw new BarnacleInititalizerException("Generator-Parameter 'vof_package_name' ist nicht gesetzt oder leer.");
        if (this.vobPackageName == null || this.vobPackageName.equals("")) throw new BarnacleInititalizerException("Generator-Parameter 'vob_package_name' ist nicht gesetzt oder leer.");
        if (this.daoPackageName == null || this.daoPackageName.equals("")) throw new BarnacleInititalizerException("Generator-Parameter 'dao_package_name' ist nicht gesetzt oder leer.");
        if (this.createScript) {
            if (this.scriptFile == null || this.scriptFile.equals("")) throw new BarnacleInititalizerException("Generator-Parameter 'script_file' ist nicht gesetzt oder leer.");
        }
    }

    public String getSrcDir() {
        return srcDir;
    }

    public String getSrcGenDir() {
        return srcGenDir;
    }

    public String getVofPackageName() {
        return vofPackageName;
    }

    public String getVoPackageName() {
        return voPackageName;
    }

    public String getVobPackageName() {
        return vobPackageName;
    }

    public String getDaoPackageName() {
        return daoPackageName;
    }

    public boolean isExecuteOnDb() {
        return executeOnDb;
    }

    public boolean isCreateScript() {
        return createScript;
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public BarnacleInitializer.Encoding getEncodingDB() {
        return encodingDB;
    }

    public BarnacleInitializer.Encoding getEncodingSource() {
        return encodingSource;
    }

    public String getConnectionManagerCanonicalClassName() {
        return this.connectionManagerCanonicalClassName;
    }

    public String getConnectionExceptionCanonicalClassName() {
        return connectionExceptionCanonicalClassName;
    }

    public String getEntityNotFoundExceptionCanonicalClassName() {
        return entityNotFoundExceptionCanonicalClassName;
    }

    public String getDaoLoggerName() {
        return daoLoggerName;
    }

    public boolean hasDaoLoggerName() {
        return this.daoLoggerName != null && !this.daoLoggerName.equals("");
    }
}
