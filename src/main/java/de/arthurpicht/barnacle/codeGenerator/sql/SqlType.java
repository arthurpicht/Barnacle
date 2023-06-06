package de.arthurpicht.barnacle.codeGenerator.sql;

public enum SqlType {

    VARCHAR("VARCHAR(255)", "Types.VARCHAR"),
    TINYINT("TINYINT", "Types.TINYINT"),
    SMALLINT("SMALLINT", "Types.SMALLINT"),
    INTEGER("INTEGER", "Types.INTEGER"),
    BIGINT("BIGINT", "Types.BIGINT"),
    DOUBLE("DOUBLE", "Types.DOUBLE"),
    TINYINT1("TINYINT(1)", "Types.TINYINT"),
    DECIMAL("DECIMAL(10,2)", "Types.DECIMAL"),
    DATE("DATE", "Types.DATE");

    private final String sqlTypeString;
    private final String typesLiteral;

    SqlType(String sqlTypeString, String typesLiteral) {
        this.sqlTypeString = sqlTypeString;
        this.typesLiteral = typesLiteral;
    }

    public String getSqlTypeString() {
        return this.sqlTypeString;
    }

    public String getTypesLiteral() {
        return this.typesLiteral;
    }

}
