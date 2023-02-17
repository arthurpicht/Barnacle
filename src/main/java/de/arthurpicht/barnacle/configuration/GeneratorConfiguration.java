package de.arthurpicht.barnacle.configuration;

import de.arthurpicht.barnacle.Const.Dialect;
import de.arthurpicht.barnacle.Const.Encoding;

public class GeneratorConfiguration {

    public static final String DIALECT = "dialect";
    public static final String SRC_DIR = "src_dir";
    public static final String SRC_GEN_DIR = "src_gen_dir";
    public static final String EXECUTE_ON_DB = "execute_on_db";
    public static final String CREATE_SCRIPT = "create_skript";
    public static final String SCRIPT_FILE = "script_file";
    public static final String ENCODING_DB = "encoding_db";
    public static final String CONNECTION_MANAGER_CLASS = "connection_manager_class";
    public static final String CONNECTION_EXCEPTION_CLASS = "connection_exception_class";
    public static final String ENTITY_NOT_FOUND_EXCEPTION_CLASS = "entity_not_found_exception_class";
    public static final String DAO_LOGGER_NAME = "dao_logger_name";


    public static final String VOF_PACKAGE_NAME = "vof_package_name";
    public static final String VO_PACKAGE_NAME = "vo_package_name";
    public static final String VOB_PACKAGE_NAME = "vob_package_name";
    public static final String DAO_PACKAGE_NAME = "dao_package_name";

    private final Dialect dialect;
    private final String srcDir;
    private final String srcGenDir;
    private final String vofPackageName;
    private final String voPackageName;
    private final String vobPackageName;
    private final String daoPackageName;
    private final boolean executeOnDb;
    private final boolean createScript;
    private final String scriptFile;
    private final Encoding encodingDB;
    private final Encoding encodingSource;
    private final String connectionManagerCanonicalClassName;
    private final String connectionExceptionCanonicalClassName;
    private final String entityNotFoundExceptionCanonicalClassName;
    private final String daoLoggerName;

    public GeneratorConfiguration(
            Dialect dialect,
            String srcDir,
            String srcGenDir,
            String vofPackageName,
            String voPackageName,
            String vobPackageName,
            String daoPackageName,
            boolean executeOnDb,
            boolean createScript,
            String scriptFile,
            Encoding encodingDB,
            Encoding encodingSource,
            String connectionManagerCanonicalClassName,
            String connectionExceptionCanonicalClassName,
            String entityNotFoundExceptionCanonicalClassName,
            String daoLoggerName) {

        this.dialect = dialect;
        this.srcDir = srcDir;
        this.srcGenDir = srcGenDir;
        this.vofPackageName = vofPackageName;
        this.voPackageName = voPackageName;
        this.vobPackageName = vobPackageName;
        this.daoPackageName = daoPackageName;
        this.executeOnDb = executeOnDb;
        this.createScript = createScript;
        this.scriptFile = scriptFile;
        this.encodingDB = encodingDB;
        this.encodingSource = encodingSource;
        this.connectionManagerCanonicalClassName = connectionManagerCanonicalClassName;
        this.connectionExceptionCanonicalClassName = connectionExceptionCanonicalClassName;
        this.entityNotFoundExceptionCanonicalClassName = entityNotFoundExceptionCanonicalClassName;
        this.daoLoggerName = daoLoggerName;
    }

    public Dialect getDialect() {
        return this.dialect;
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

    public Encoding getEncodingDB() {
        return encodingDB;
    }

    public Encoding getEncodingSource() {
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
