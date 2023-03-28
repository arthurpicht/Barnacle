package de.arthurpicht.barnacle.codeGenerator.java;

import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;

/**
 * @author Arthur Picht, 2007 - 2018, 2023
 */
public abstract class LoggerGenerator {
	
	public enum LoggerType {SLF4J}

	protected final ClassGenerator classGenerator;
	
	protected LoggerGenerator(ClassGenerator classGenerator) {
		this.classGenerator = classGenerator;
	}
	
	public static LoggerGenerator getInstance(ClassGenerator classGenerator, LoggerType loggerType, GeneratorConfiguration generatorConfiguration) {
		if (loggerType == LoggerType.SLF4J) {
			LoggerGenerator loggerGenerator = new SLF4JGenerator(classGenerator, generatorConfiguration);
			loggerGenerator.addToImport();
			return loggerGenerator;
		}
		throw new BarnacleRuntimeException("Impossible logger instance requested!");
	}

	public abstract void addToImport();
	
	public abstract void generateInitialization(SourceCache sourceCache);
	
	public abstract String generateDebugLogStatementByString(String logString);
	
	public abstract String generateDebugLogStatementByVarName(String varName);

	public abstract String generateDebugLogStatementByExpression(String expression);

}
