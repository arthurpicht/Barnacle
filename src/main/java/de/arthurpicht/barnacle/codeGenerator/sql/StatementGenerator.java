package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.Const.Dialect;
import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;
import de.arthurpicht.utils.core.strings.Strings;

import java.util.List;

public abstract class StatementGenerator {

	protected final String tempColName ="tempBarnacleGenerator";
	
	public static StatementGenerator getInstance(Dialect dialect) {
		if (dialect == Const.Dialect.MYSQL) {
			return new StatementGeneratorMySQL();
		} else if (dialect == Const.Dialect.H2) {
			return new StatementGeneratorH2();
		}
		throw new RuntimeException("Unsupported statement generator requested!");
	}
	
	public abstract String[] deactivateForeignKeyChecks();
	
	public abstract String[] activateForeignKeyChecks();

	public String dropTableIfExists(String tableName) {
		return "DROP TABLE IF EXISTS " + tableName + ";";

		// Oracle-SQL: 'drop {tableName} cascade constraints'
		// deletes all foreign keys
		// that reference the table to be dropped, then
		// drops the table
	}

	public String createTable(String tableName) {
		return "CREATE TABLE " + tableName + " (" + tempColName + " varchar(1));";
	}

	public String addColumn(String tableName, String columnName, String sqlType, String defaultValue, boolean notNull) {
		String sql = "ALTER TABLE " + tableName + " ADD COLUMN (" + columnName + " " + sqlType;
		if (defaultValue != null) {
			sql += " DEFAULT '" + defaultValue + "'";
		}
		if (notNull) {
			sql += " NOT NULL";
		}
		sql += ");";
		return sql;
	}

	public String addPrimaryKey(String tableName, List<String> columnNames) {
		StringBuilder sql = new StringBuilder("ALTER TABLE " + tableName + " ADD PRIMARY KEY (");
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

	public String addUniqueKey(String tableName, String indexName, List<String> columnNames) {
		return "ALTER TABLE " + tableName + " ADD CONSTRAINT " + indexName
				+ " UNIQUE (" + Strings.listing(columnNames, ", ") + ");";
	}

	// worked for mysql until 1.2
	// TODO Check if new method works as well. New one is also applicable for H2.
	// see https://dev.mysql.com/doc/refman/8.0/en/alter-table.html
//	public String addUniqueKey(String tableName, String indexName, List<String> columnNames) {
//		StringBuilder sql = new StringBuilder("ALTER TABLE " + tableName + " ADD UNIQUE KEY " + indexName + " (");
//		boolean sequence = false;
//		for (String columnName : columnNames) {
//			if (sequence) {
//				sql.append(", ");
//			}
//			sql.append(columnName);
//			sequence = true;
//		}
//		sql.append(");");
//		return sql.toString();
//	}

	public String dropTempColumn(String tableName) {
		return "ALTER TABLE " + tableName + " DROP COLUMN " + tempColName + ";";
	}

	public String addForeignKey(String tableName, String foreignKeyName, ForeignKeyWrapper foreignKeyWrapper) {
		StringBuilder sql = new StringBuilder("ALTER TABLE " + tableName + " ADD CONSTRAINT " + foreignKeyName + " FOREIGN KEY (");
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
	
	public abstract String addAutoIncrement(String tableName, String columnName);
	
	/**
	 * Generates sql to configure encoding for table;
	 * 
	 * TODO: Configuring encoding on table level seems to be specific for MySQL. Oracle for example
	 * needs configuration on database level. Needs rework when extending Barnacle for other RDBMS. 
	 * 
	 * @param encoding
	 * @return
	 */
	public abstract String configureEncoding(String tableName, Const.Encoding encoding);

}
