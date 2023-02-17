package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.Const.Dialect;
import de.arthurpicht.barnacle.Const.Encoding;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.barnacle.codeGenerator.CodeGeneratorException;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SqlGenerator {
	
	private static final StatementGenerator statementGenerator;
	
	static {
		Dialect dialect = GeneratorContext.getInstance().getGeneratorConfiguration().getDialect();
		statementGenerator = StatementGenerator.getInstance(dialect);
	}
	
	/**
	 * SQL generation stage 1, consists of dropping all tables corresponding to
	 * current entities, creating new tables, columns and unique keys.
	 * 
	 * @param entities
	 * @throws CodeGeneratorException
	 */
	public static void generateStage1(Set<Entity> entities, Encoding encoding) throws CodeGeneratorException {

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
	 * @throws CodeGeneratorException
	 */
	private static void preDrop() throws CodeGeneratorException {
		try {
			String[] sqlStatements = statementGenerator.deactivateForeignKeyChecks();
			SqlDispatcher.dispatch(sqlStatements);
		} catch (DBConnectionException e) {
			throw new CodeGeneratorException(e);
		}
	}
	
	/**
	 * Create and commits sql statements that will be executed after
	 * dropping all the tables.
	 * 
	 * @throws CodeGeneratorException
	 */
	private static void postDrop() throws CodeGeneratorException {
		try {
			String[] sqlStatements = statementGenerator.activateForeignKeyChecks();
			SqlDispatcher.dispatch(sqlStatements);
		} catch (DBConnectionException e) {
			throw new CodeGeneratorException(e);
		}
	}
	
	/**
	 * Create and commits sql statements in oder to drop table 
	 * corresponding to passed entity.
	 * 
	 * @param entity
	 * @throws CodeGeneratorException
	 */
	private static void dropStage1(Entity entity) throws CodeGeneratorException {
		try {
			String sqlStatement = statementGenerator.dropTableIfExists(entity.getTableName());
			SqlDispatcher.dispatch(sqlStatement);
		} catch (DBConnectionException e) {
			throw new CodeGeneratorException(e);
		}
	}
	
	/**
	 * Create and commits sql statements in oder to create
	 * new table, solumns, primary keys and unique keys 
	 * corresponding to passed entity.
	 * 
	 * @param entity
	 * @throws CodeGeneratorException
	 */
	private static void generateStage1(Entity entity, Encoding encoding) throws CodeGeneratorException {
		
		try {
			
			// create table
			String sqlStatement = statementGenerator.createTable(entity.getTableName());
			SqlDispatcher.dispatch(sqlStatement);
			
			// create columns
			List<Attribute> attributeList = entity.getAttributes();
			for (Attribute attribute : attributeList) {

				boolean isNotNull = attribute.isNotNull() || attribute.isPrimaryKey();
				sqlStatement = statementGenerator.addColumn(
						entity.getTableName(),
						attribute.getColumnName(),
						attribute.getSqlDataType(),
						attribute.getDefaultValue(),
						isNotNull);
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
			throw new CodeGeneratorException(e);
//		} catch (UnknownTypeException e) {
//			throw new GeneratorException(e);
		}
	}
	
	/**
	 * SQL generation stage 2, consists of generating foreign keys. 
	 * 
	 * @param entity
	 * @throws CodeGeneratorException
	 */
	public static void generateStage2(Entity entity) throws CodeGeneratorException {
		
		try {
			
			// foreign keys
			Set<String> foreignKeyNames = entity.getAllForeignKeyNames();
			for (String foreignKeyName : foreignKeyNames) {
				ForeignKeyWrapper foreignKeyWrapper = entity.getOrCreateForeignKeyByName(foreignKeyName);
				
				String sqlStatement = statementGenerator.addForeignKey(entity.getTableName(), foreignKeyName, foreignKeyWrapper);
				SqlDispatcher.dispatch(sqlStatement);
			}
		} catch (DBConnectionException e) {
			throw new CodeGeneratorException(e);
		}
		
	}
	
	public static void close() throws CodeGeneratorException {
		// close resources
		try {
			SqlDispatcher.close();
		} catch (DBConnectionException e) {
			throw new CodeGeneratorException(e);
		}

	}
	

}
