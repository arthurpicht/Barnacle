package de.arthurpicht.barnacle.codeGenerator.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlStatementCache {

    private final List<String> sqlStatementList;

    public SqlStatementCache() {
        this.sqlStatementList = new ArrayList<>();
    }

    public void add(String sqlStatement) {
        this.sqlStatementList.add(sqlStatement);
    }

    public void add(String[] sqlStatements) {
        this.sqlStatementList.addAll(Arrays.asList(sqlStatements));
    }

    public SqlStatements getSqlStatements() {
        return new SqlStatements(this.sqlStatementList);
    }

}
