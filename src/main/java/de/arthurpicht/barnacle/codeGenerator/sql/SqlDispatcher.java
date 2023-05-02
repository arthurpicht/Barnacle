package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.codeGenerator.CodeGeneratorException;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlDispatcher {

	private static final Logger logger = LoggerFactory.getLogger(SqlDispatcher.class);

	private final boolean createSqlScript;
	private final boolean executeOnDb;

	private final SqlScriptWriter sqlScriptWriter;
	private final SqlDbExecutor sqlDbExecutor;

	public SqlDispatcher(GeneratorConfiguration generatorConfiguration) {
		this.createSqlScript = generatorConfiguration.isCreateScript();
		this.executeOnDb = generatorConfiguration.isExecuteOnDb();
		this.sqlScriptWriter = this.createSqlScript ?
				new SqlScriptWriter(generatorConfiguration) : null;
		this.sqlDbExecutor = this.executeOnDb ?
				new SqlDbExecutor(generatorConfiguration) : null;
	}

	public void dispatch(String sqlString) {
		logger.debug(sqlString);
		if (this.createSqlScript) this.sqlScriptWriter.println(sqlString);
		if (this.executeOnDb) this.sqlDbExecutor.executeSql(sqlString);
	}

	public void dispatch(String[] sqlStrings) {
		for (String string : sqlStrings) {
			dispatch(string);
		}
	}

	public void close() throws CodeGeneratorException, DBConnectionException {
		if (this.createSqlScript) this.sqlScriptWriter.close();
		if (this.executeOnDb) this.sqlDbExecutor.close();
	}

}
