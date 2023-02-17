package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.codeGenerator.CodeGeneratorException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SqlScriptWriter {
	
	private static SqlScriptWriter sqlScriptWriter = null;
	private PrintWriter printWriter;
	
	private SqlScriptWriter() throws CodeGeneratorException {
		GeneratorConfiguration generatorConfiguration = GeneratorContext.getInstance().getGeneratorConfiguration();
		String sqlScriptFile = generatorConfiguration.getScriptFile();
		try {
			this.printWriter = new PrintWriter(new FileWriter(new File(sqlScriptFile)));
		} catch (IOException e) {
			throw new CodeGeneratorException("Error when creating SQL script file '" + sqlScriptFile + "'" , e);
		} 
	}
	
	public static SqlScriptWriter getInstance() throws CodeGeneratorException {
		if (sqlScriptWriter == null) {
			sqlScriptWriter = new SqlScriptWriter();
		}
		return sqlScriptWriter;
	}
	
	public void println(String sqlString) {
		this.printWriter.println(sqlString);
	}
	
	public void close() {
		this.printWriter.close();
	}

}

