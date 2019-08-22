package de.arthurpicht.barnacle.generator.sql;

import de.arthurpicht.barnacle.BarnacleInitializer;
import de.arthurpicht.barnacle.mapping.ForeignKeyWrapper;

import java.util.List;


public abstract class StatementGenerator {
	
	/**
	 * Instance factory. Creates derived instance based on database. 
	 * 
	 * @param database
	 * @return
	 */
	public static StatementGenerator getInstance(Databases database) {
		if (database == Databases.MYSQL) {
			return new StatementGeneratorMySQL();
		}
		throw new RuntimeException("Unsupported statement generator requested!");
	}
	
	/**
	 * Generates sql-statements that will be executed before 
	 * dropping tables in stage 1.
	 * 
	 * @return
	 */
	public abstract String[] preDrop();
	
	/**
	 * Generates sql-statements that will be executed after
	 * dropping table in stage 1.
	 *
	 * @return
	 */
	public abstract String[] postDrop();	
	
	/**
	 * Generates sql to drop table by tablename if exists.
	 * 
	 * @param tablename
	 * @return
	 */
	public abstract String dropTableIfExists(String tablename);
	
	/**
	 * Generates sql to create a table with a temporary column
	 * named 'tempBarnacleGenerator'.
	 * 
	 * @param tablename
	 * @return
	 */
	public abstract String createTable(String tablename);
	
	/**
	 * Generates sql to add column to table.
	 *  
	 * @param columnName
	 * @param fieldType
	 * @param notNull
	 * @return
	 */
	public abstract String addColumn(String tablename, String columnName, String sqlType, String defaultValue, boolean notNull);
	
	/**
	 * Generates sql to add primary key.
	 * 
	 * @param tablename
	 * @param columnNames
	 * @return
	 */
	public abstract String addPrimaryKey(String tablename, List<String> columnNames);
	
	/**
	 * Generates sql to add unique key. 
	 * 
	 * @param tablename
	 * @param indexName
	 * @param columnNames
	 * @return
	 */
	public abstract String addUniqueKey(String tablename, String indexName, List<String> columnNames);
	
	/**
	 * Drops the temporary column which was created on table creation time.
	 * 
	 * @param tablename
	 * @return
	 */
	public abstract String dropTempColumn(String tablename);
	
	/**
	 * Generates sql to add foreign key.
	 * 
	 * @param tablename
	 * @param foreignKeyWrapper
	 * @return
	 */
	public abstract String addForeignKey(String tablename, String foreignKeyName, ForeignKeyWrapper foreignKeyWrapper);
	
	/**
	 * Generates sql to add auto increment functionality to given field. 
	 * 
	 * @param tablename
	 * @param clumnName
	 * @return
	 */
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
