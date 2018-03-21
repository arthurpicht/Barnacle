package de.arthurpicht.barnacle.generator.java;

/**
 * Abstract base class for logger generator classes. Implements
 * basic functionality to generate statements in conjunction with
 * logger usage. 
 * 
 * @author Arthur Picht, 2007 - 2018
 *
 */
public abstract class LoggerGenerator {
	
	public enum LoggerTypes {SLF4J};

	protected ClassGenerator parentClassGenerator;
	
	/**
	 * Do not call constructor manually! Use getInstance method instead!
	 * 
	 * @param classGenerator
	 */
	public LoggerGenerator(ClassGenerator classGenerator) {
		this.parentClassGenerator = classGenerator;
	}
	
	/**
	 * Generates specific LoggerGenerator class by logger type.
	 * 
	 * @param classGenerator
	 * @param loggerType
	 * @return
	 */
	public static LoggerGenerator getInstance(ClassGenerator classGenerator, LoggerTypes loggerType) {
		if (loggerType == LoggerTypes.SLF4J) {
			LoggerGenerator loggerGenerator = new SLF4JGenerator(classGenerator);
			loggerGenerator.addToImport();
			return new SLF4JGenerator(classGenerator);
		}
		throw new RuntimeException("Impossible logger instance requested!");
	}

	/**
	 * Generates import statement for logger class.
	 *
	 */
	public abstract void addToImport();
	
	/**
	 * Generates initialization statement for logger.
	 * 
	 * @param sourceCache
	 */
	public abstract void generateInitialization(SourceCache sourceCache);
	
	/**
	 * Generates log statement on debug level for passed log string.
	 * 
	 * @param logString
	 */
	public abstract String generateDebugLogStatementByString(String logString);
	
	/**
	 * Generates log statement on debug level for passed variable name.
	 * 
	 * @param varName
	 */
	public abstract String generateDebugLogStatementByVarName(String varName);

}
