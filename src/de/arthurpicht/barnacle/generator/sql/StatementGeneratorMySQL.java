package de.arthurpicht.barnacle.generator.sql;

import java.util.List;

import de.arthurpicht.barnacle.BarnacleInitializer.Encoding;
import de.arthurpicht.barnacle.mapping.Attribute;
import de.arthurpicht.barnacle.mapping.ForeignKeyWrapper;



public class StatementGeneratorMySQL extends StatementGenerator {
	
	private static final String TEMP_COL="tempBarnacleGenerator";
	
	/**
	 * Do not call this constructor manually! Use getInstance
	 * method of superclass instead!
	 *
	 */	
	public StatementGeneratorMySQL() {		
	}
	
	@Override
	public String[] preDrop() {		
		String[] s = {"SET foreign_key_checks=0;"};
		return s;
	}
	
	@Override
	public String[] postDrop() {
		String[] s = {"SET foreign_key_checks=1;"};
		return s;		
	}
	
	@Override
	public String dropTableIfExists(String tablename) {
		
//		String[] statements = new String[3];
//		statements[0] = "SET foreign_key_checks=0;";
		return "DROP TABLE IF EXISTS " + tablename + ";";
//		statements[2] = "SET foreign_key_checks=1;";
		
//		return statements;
		
		// Oracle-SQL: 'drop {tablename} cascade constraints' 
		// deletes all foreign keys
		// that reference the table to be dropped, than
		// drops the table
	}

	@Override
	public String createTable(String tablename) {
		return "CREATE TABLE " + tablename + " (" + TEMP_COL + " varchar(1));";
	}

	@Override
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

	@Override
	public String addPrimaryKey(String tablename, List<String> columnNames) {
		String sql = "ALTER TABLE " + tablename + " ADD PRIMARY KEY (";
		boolean sequence = false;
		for (String columnName : columnNames) {
			if (sequence) {
				sql += ", ";
			}
			sql += columnName;
			sequence = true;
		}
		sql += ");";
		return sql;
	}

	@Override
	public String addUniqueKey(String tablename, String indexName, List<String> columnNames) {
		String sql = "ALTER TABLE " + tablename + " ADD UNIQUE KEY " + indexName + " (";
		boolean sequence = false;
		for (String columnName : columnNames) {
			if (sequence) {
				sql += ", ";
			}
			sql += columnName;
			sequence = true;
		}
		sql += ");";
		return sql;
	}
	
	@Override
	public String addAutoIncrement(String tablename, String columnName, String sqlType, String defaultValue, boolean notNull) {
		String sql = "ALTER TABLE " + tablename + " MODIFY COLUMN " + columnName + " " + sqlType;
		if (defaultValue != null) {
			sql += " DEFAULT '" + defaultValue + "'";
		}
		if (notNull) {
			sql += " NOT NULL";
		}
		sql += " AUTO_INCREMENT;";		
		
		return sql;
	}

	@Override
	public String dropTempColumn(String tablename) {
		String sql = "ALTER TABLE " + tablename + " DROP COLUMN " + TEMP_COL + ";";
		return sql;
	}

	@Override
	public String addForeignKey(String tablename, String foreignKeyName, ForeignKeyWrapper foreignKeyWrapper) {		
		String sql = "ALTER TABLE " + tablename + " ADD CONSTRAINT " + foreignKeyName + " FOREIGN KEY (";
		boolean sequence = false;
		for (Attribute attribute : foreignKeyWrapper.getKeyFieldAttributes()) {
			if (sequence) {
				sql += ", ";
			}
			sql += attribute.getColumnName();
			sequence = true;
		}
		sql += ") REFERENCES " + foreignKeyWrapper.getTargetEntity().getTableName() + " (";
		
		sequence = false;
		for (Attribute attribute : foreignKeyWrapper.getTargetFieldAttributes()) {
			if (sequence) {
				sql += ", ";
			}
			sql += attribute.getColumnName();
			sequence = true;
		}
		sql += ")";
		
		if (foreignKeyWrapper.isOnDeleteCascade()) {
			sql += " ON DELETE CASCADE";
		}
		
		if (foreignKeyWrapper.isOnUpdateCascade()) {
			sql += " ON UPDATE CASCADE";
		}
		
		sql+= ";";
		
		return sql;
	}

	@Override
	public String configureEncoding(String tablename, Encoding encoding) {
		String sql = "";
		if (encoding.equals(Encoding.ISO)) {
			sql = "ALTER TABLE " + tablename + " CHARACTER SET LATIN1;";
		} else if (encoding.equals(Encoding.UTF)) {
			sql = "ALTER TABLE " + tablename + " CHARACTER SET UTF8;";
		}
		
		return sql;
	}
	
	

	
	

}
