package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.codeGenerator.CodeGeneratorException;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SqlScriptWriter {
	
	private final PrintWriter printWriter;
	
	public SqlScriptWriter(GeneratorConfiguration generatorConfiguration) throws CodeGeneratorException {
		String sqlScriptFile = generatorConfiguration.getScriptFile();
		try {
			this.printWriter = new PrintWriter(new FileWriter(sqlScriptFile));
		} catch (IOException e) {
			throw new CodeGeneratorException("Error when creating SQL script file [" + sqlScriptFile + "]." , e);
		} 
	}
	
	public void println(String sqlString) {
		this.printWriter.println(sqlString);
	}
	
	public void close() {
		this.printWriter.close();
	}

}

