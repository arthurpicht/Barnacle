package de.arthurpicht.barnacle.codeGenerator.sql;

import java.util.Collections;
import java.util.List;

public class SqlStatements {

    private final List<String> sqlStatementList;

    public SqlStatements(List<String> sqlStatementList) {
        this.sqlStatementList = Collections.unmodifiableList(sqlStatementList);
    }

    public List<String> getSqlStatementList() {
        return this.sqlStatementList;
    }

}
