package de.arthurpicht.barnacle.configuration.db.jndi;

public class JNDIConfiguration {

    public static final String DAO_PACKAGE = "dao_package";
    public static final String LOOKUP_NAME = "lookup_name";

    private final String daoPackage;
    private final String lookupName;

    public JNDIConfiguration(String daoPackage, String lookupName) {
        this.daoPackage = daoPackage;
        this.lookupName = lookupName;
    }

    public String getDaoPackage() {
        return daoPackage;
    }

    public String getLookupName() {
        return lookupName;
    }

}
