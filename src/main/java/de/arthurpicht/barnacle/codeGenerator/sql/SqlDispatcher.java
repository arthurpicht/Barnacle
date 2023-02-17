package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.exceptions.DBConnectionException;
import de.arthurpicht.barnacle.codeGenerator.CodeGeneratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Depending on conf dispatches sql statement to script 
 * and/or jdbc.
 *
 * TODO
 *
 * @author Picht
 *
 */
public class SqlDispatcher {

	private static Logger logger = LoggerFactory.getLogger("BARNACLE");

	public static void dispatch(String sqlString) throws DBConnectionException, CodeGeneratorException {

		logger.debug(sqlString);

		GeneratorConfiguration generatorConfiguration = GeneratorContext.getInstance().getGeneratorConfiguration();

		if (generatorConfiguration.isCreateScript()) {
			SqlScriptWriter sqlScriptWriter = SqlScriptWriter.getInstance();
			sqlScriptWriter.println(sqlString);
		}

		if (generatorConfiguration.isExecuteOnDb()) {
			SqlDbExecuter sqlDbExecuter = SqlDbExecuter.getInstance();
			sqlDbExecuter.executeSql(sqlString);
		}
	}

	public static void dispatch(String[] sqlStrings) throws DBConnectionException, CodeGeneratorException {
		for (String string : sqlStrings) {
			dispatch(string);
		}
	}

	public static void close() throws CodeGeneratorException, DBConnectionException {

		GeneratorConfiguration generatorConfiguration = GeneratorContext.getInstance().getGeneratorConfiguration();

		if (generatorConfiguration.isCreateScript()) {
			SqlScriptWriter sqlScriptWriter = SqlScriptWriter.getInstance();
			sqlScriptWriter.close();
		}

		if (generatorConfiguration.isExecuteOnDb()) {
			SqlDbExecuter sqlDbExecuter = SqlDbExecuter.getInstance();
			sqlDbExecuter.close();
		}


	}

}
