package de.arthurpicht.barnacle.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import de.arthurpicht.barnacle.BarnacleInitializer;
import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.exceptions.GeneratorException;

public class SqlScriptWriter {
	
	private static SqlScriptWriter sqlScriptWriter = null;
	private PrintWriter printWriter;
	
	private SqlScriptWriter() throws GeneratorException {
		GeneratorConfiguration generatorConfiguration = GeneratorContext.getInstance().getGeneratorConfiguration();
		String sqlScriptFile = generatorConfiguration.getScriptFile();
		try {
			this.printWriter = new PrintWriter(new FileWriter(new File(sqlScriptFile)));
		} catch (IOException e) {
			throw new GeneratorException("Error when creating SQL script file '" + sqlScriptFile + "'" , e);
		} 
	}
	
	public static SqlScriptWriter getInstance() throws GeneratorException {
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

