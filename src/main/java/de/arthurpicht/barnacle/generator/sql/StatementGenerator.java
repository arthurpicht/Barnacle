package de.arthurpicht.barnacle.generator.sql;

import de.arthurpicht.barnacle.BarnacleInitializer;
import de.arthurpicht.barnacle.BarnacleInitializer.Dialect;
import de.arthurpicht.barnacle.mapping.Attribute;
import de.arthurpicht.barnacle.mapping.ForeignKeyWrapper;

import java.util.List;

public abstract class StatementGenerator {

	protected final String tempColName ="tempBarnacleGenerator";
	
	public static StatementGenerator getInstance(Dialect dialect) {
		if (dialect == Dialect.MYSQL) {
			return new StatementGeneratorMySQL();
		} else if (dialect == Dialect.H2) {
			return new StatementGeneratorH2();
		}
		throw new RuntimeException("Unsupported statement generator requested!");
	}
	
	public abstract String[] deactivateForeignKeyChecks();
	
	public abstract String[] activateForeignKeyChecks();

	public String dropTableIfExists(String tablename) {
		return "DROP TABLE IF EXISTS " + tablename + ";";

		// Oracle-SQL: 'drop {tablename} cascade constraints'
		// deletes all foreign keys
		// that reference the table to be dropped, than
		// drops the table
	}

	public String createTable(String tablename) {
		return "CREATE TABLE " + tablename + " (" + tempColName + " varchar(1));";
	}

	public String addColumn(String tablename, String columnName, String sqlType, String defaultValue, boolean notNull) {
		String sql = "ALTER TABLE " + tablename + " ADD COLUMN (" + columnName + " " + sqlType;
		if (defaultValue != null) {
			sql += " DEFAULT '" + defaultValue + "'";
		}
		if (notNull) {
			sql += " NOT NULL";
		}
		sql += ");";
		return sql;
	}

	public String addPrimaryKey(String tablename, List<String> columnNames) {
		StringBuilder sql = new StringBuilder("ALTER TABLE " + tablename + " ADD PRIMARY KEY (");
		boolean sequence = false;
		for (String columnName : columnNames) {
			if (sequence) {
				sql.append(", ");
			}
			sql.append(columnName);
			sequence = true;
		}
		sql.append(");");
		return sql.toString();
	}

	public String addUniqueKey(String tablename, String indexName, List<String> columnNames) {
		StringBuilder sql = new StringBuilder("ALTER TABLE " + tablename + " ADD UNIQUE KEY " + indexName + " (");
		boolean sequence = false;
		for (String columnName : columnNames) {
			if (sequence) {
				sql.append(", ");
			}
			sql.append(columnName);
			sequence = true;
		}
		sql.append(");");
		return sql.toString();
	}

	public String dropTempColumn(String tablename) {
		return "ALTER TABLE " + tablename + " DROP COLUMN " + tempColName + ";";
	}

	public String addForeignKey(String tablename, String foreignKeyName, ForeignKeyWrapper foreignKeyWrapper) {
		StringBuilder sql = new StringBuilder("ALTER TABLE " + tablename + " ADD CONSTRAINT " + foreignKeyName + " FOREIGN KEY (");
		boolean sequence = false;
		for (Attribute attribute : foreignKeyWrapper.getKeyFieldAttributes()) {
			if (sequence) {
				sql.append(", ");
			}
			sql.append(attribute.getColumnName());
			sequence = true;
		}
		sql.append(") REFERENCES ").append(foreignKeyWrapper.getTargetEntity().getTableName()).append(" (");

		sequence = false;
		for (Attribute attribute : foreignKeyWrapper.getTargetFieldAttributes()) {
			if (sequence) {
				sql.append(", ");
			}
			sql.append(attribute.getColumnName());
			sequence = true;
		}
		sql.append(")");

		if (foreignKeyWrapper.isOnDeleteCascade()) {
			sql.append(" ON DELETE CASCADE");
		}

		if (foreignKeyWrapper.isOnUpdateCascade()) {
			sql.append(" ON UPDATE CASCADE");
		}

		sql.append(";");

		return sql.toString();
	}
	
	public abstract String addAutoIncrement(String tablename, String columnName, String sqlType, String defaultValue, boolean notNull);
	
	/**
	 * Generates sql to configure encoding for table;
	 * 
	 * TODO: Configuring encoding on table level seems to be specific for MySQL. Oracle for example
	 * needs configuration on database level. Needs rework when extending Barnacle for other RDBMS. 
	 * 
	 * @param encoding
	 * @return
	 */
	public abstract String configureEncoding(String tablename, BarnacleInitializer.Encoding encoding);

}
