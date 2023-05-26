package de.arthurpicht.barnacle.connectionManager;

import de.arthurpicht.barnacle.connectionManager.connection.DbConnection;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.utils.core.strings.SimplifiedGlob;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ConnectionResolver {

    private static class Entry {
        private final Pattern pattern;
        private final DbConnection dbConnection;
        public Entry(Pattern pattern, DbConnection dbConnection) {
            this.pattern = pattern;
            this.dbConnection = dbConnection;
        }
    }

    private final List<Entry> entryList;

    public ConnectionResolver(Map<String, DbConnection> dbConnectionMap) {
        this.entryList = new ArrayList<>();
        for (Map.Entry<String, DbConnection> mapEntry : dbConnectionMap.entrySet()) {
            Pattern pattern = SimplifiedGlob.compile(mapEntry.getKey());
            Entry entry = new Entry(pattern, mapEntry.getValue());
            this.entryList.add(entry);
        }
    }

    public DbConnection getDbConnection(Class<?> daoClass) throws DBConnectionException {
        for (Entry entry : this.entryList) {
            if (entry.pattern.matcher(daoClass.getCanonicalName()).matches())
                return entry.dbConnection;
        }
        throw new DBConnectionException("No db connection configuration found in barnacle configuration matching " +
                "DAO class name: [" + daoClass.getCanonicalName() + "].");
    }

}
