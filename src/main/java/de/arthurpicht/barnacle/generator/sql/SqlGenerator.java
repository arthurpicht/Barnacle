package de.arthurpicht.barnacle.generator.sql;

import de.arthurpicht.barnacle.BarnacleInitializer;
import de.arthurpicht.barnacle.BarnacleInitializer.Encoding;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.barnacle.exceptions.GeneratorException;
import de.arthurpicht.barnacle.generator.SqlDispatcher;
import de.arthurpicht.barnacle.mapping.Attribute;
import de.arthurpicht.barnacle.mapping.Entity;
import de.arthurpicht.barnacle.mapping.ForeignKeyWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SqlGenerator {
	
	private static StatementGenerator statementGenerator;
	
	static { 
		statementGenerator = StatementGenerator.getInstance(BarnacleInitializer.getDatabase());
	}
	
	/**
	 * SQL generation stage 1, consists of dropping all tables corresponding to
	 * current entities, creating new tables, columns and unique keys.
	 * 
	 * @param entities
	 * @throws GeneratorException
	 */
	public static void generateStage1(Set<Entity> entities, Encoding encoding) throws GeneratorException {		

		// pre drop, e.g. turning off foreign key checks
		preDrop();
		
		// drop all tables
		// Do this first, not to leave foreign key references from left tables
		// to dropped tables. If so, a new table with same name can not be created, 
		// although 'foreign key checks' is turned off.
		for (Entity entity : entities) {
			dropStage1(entity);
		}

		// post drop, e.g. turning on again foreign key checks
		postDrop();
		
		// create table, columns, and unique keys, set encoding
		for (Entity entity : entities) {
			generateStage1(entity, encoding);			
		}
		
	}
	
	/**
	 * Create and commits sql statements that will be executed before
	 * dropping all the tables.
	 * 
	 * @throws GeneratorException
	 */
	private static void preDrop() throws GeneratorException {
		try {
			String[] sqlStatements = statementGenerator.preDrop();
			SqlDispatcher.dispatch(sqlStatements);
		} catch (DBConnectionException e) {
			throw new GeneratorException(e);
		}
	}
	
	/**
	 * Create and commits sql statements that will be executed after
	 * dropping all the tables.
	 * 
	 * @throws GeneratorException
	 */
	private static void postDrop() throws GeneratorException {
		try {
			String[] sqlStatements = statementGenerator.postDrop();
			SqlDispatcher.dispatch(sqlStatements);
		} catch (DBConnectionException e) {
			throw new GeneratorException(e);
		}
	}
	
	/**
	 * Create and commits sql statements in oder to drop table 
	 * corresponding to passed entity.
	 * 
	 * @param entity
	 * @throws GeneratorException
	 */
	private static void dropStage1(Entity entity) throws GeneratorException {
		try {
			String sqlStatement = statementGenerator.dropTableIfExists(entity.getTableName());
			SqlDispatcher.dispatch(sqlStatement);
		} catch (DBConnectionException e) {
			throw new GeneratorException(e);
		}
	}
	
	/**
	 * Create and commits sql statements in oder to create
	 * new table, solumns, primary keys and unique keys 
	 * corresponding to passed entity.
	 * 
	 * @param entity
	 * @throws GeneratorException
	 */
	private static void generateStage1(Entity entity, Encoding encoding) throws GeneratorException {
		
		try {
			
			// create table
			String sqlStatement = statementGenerator.createTable(entity.getTableName());
			SqlDispatcher.dispatch(sqlStatement);
			
			// create columns
			List<Attribute> attributeList = entity.getAttributes();
			for (Attribute attribute : attributeList) {
				
				sqlStatement = statementGenerator.addColumn(entity.getTableName(), attribute.getColumnName(), attribute.getSqlDataType(), attribute.getDefaultValue(), attribute.isNotNull());
				SqlDispatcher.dispatch(sqlStatement);
			}
			
			// remove temporary column
			sqlStatement = statementGenerator.dropTempColumn(entity.getTableName());
			SqlDispatcher.dispatch(sqlStatement);
			
			// primary keys
			attributeList = entity.getPkAttributes();
			List<String> pkColumnNames = new ArrayList<String>();
			for (Attribute attribute : attributeList) {
				pkColumnNames.add(attribute.getColumnName());
			}
					
			sqlStatement = statementGenerator.addPrimaryKey(entity.getTableName(), pkColumnNames);
			SqlDispatcher.dispatch(sqlStatement);
			
			// unique keys
			Set<String> indexNames = entity.getAllUniqueIndicesNames();
			for (String indexName : indexNames) {
				attributeList = entity.getAttributesByUniqueIndexName(indexName);
				List<String> uniqueColumnNames = new ArrayList<String>();
				for (Attribute attribute : attributeList) {
					uniqueColumnNames.add(attribute.getColumnName());
				}
				
				sqlStatement = statementGenerator.addUniqueKey(entity.getTableName(), indexName, uniqueColumnNames);
				SqlDispatcher.dispatch(sqlStatement);
			}
			
			// auto increment
			Attribute attribute = entity.getAutoIncrementAttribute();
			if (attribute != null) {
				sqlStatement = statementGenerator.addAutoIncrement(entity.getTableName(), attribute.getColumnName(), attribute.getSqlDataType(), attribute.getDefaultValue(), attribute.isNotNull());
				SqlDispatcher.dispatch(sqlStatement);				
			}
			
			// configure encoding
			sqlStatement = statementGenerator.configureEncoding(entity.getTableName(), encoding);
			if (sqlStatement!=null && !sqlStatement.equals("")) {
				SqlDispatcher.dispatch(sqlStatement);
			}
			
		} catch (DBConnectionException e) {
			throw new GeneratorException(e);
//		} catch (UnknownTypeException e) {
//			throw new GeneratorException(e);
		}
	}
	
	/**
	 * SQL generation stage 2, consists of generating foreign keys. 
	 * 
	 * @param entity
	 * @throws GeneratorException
	 */
	public static void generateStage2(Entity entity) throws GeneratorException {
		
		try {
			
			// foreign keys
			Set<String> foreignKeyNames = entity.getAllForeignKeyNames();
			for (String foreignKeyName : foreignKeyNames) {
				ForeignKeyWrapper foreignKeyWrapper = entity.getOrCreateForeignKeyByName(foreignKeyName);
				
				String sqlStatement = statementGenerator.addForeignKey(entity.getTableName(), foreignKeyName, foreignKeyWrapper);
				SqlDispatcher.dispatch(sqlStatement);
			}
		} catch (DBConnectionException e) {
			throw new GeneratorException(e);
		}
		
	}
	
	public static void close() throws GeneratorException {
		// close resources
		try {
			SqlDispatcher.close();
		} catch (DBConnectionException e) {
			throw new GeneratorException(e);
		}

	}
	

}
