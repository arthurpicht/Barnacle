package de.arthurpicht.barnacle.generator.java;

import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SLF4JGenerator extends LoggerGenerator {

    /**
     * Do not call constructor manually! Use getInstance method instead!
     *
     * @param classGenerator
     */
    public SLF4JGenerator(ClassGenerator classGenerator) {
        super(classGenerator);
    }

    /**
     * Generates import statement for logger class.
     *
     */
    public void addToImport() {
        ImportGenerator importGenerator = this.parentClassGenerator.importGenerator;
        importGenerator.addImport(Logger.class);
        importGenerator.addImport(LoggerFactory.class);
    }

    /**
     * Generates initialization statement for logger.
     *
     * @param sourceCache
     */
    public void generateInitialization(SourceCache sourceCache) {
        GeneratorConfiguration generatorConfiguration = GeneratorContext.getInstance().getGeneratorConfiguration();
        if (generatorConfiguration.hasDaoLoggerName()) {
            String daoLoggerName = generatorConfiguration.getDaoLoggerName();
            sourceCache.addLine("private static " + Logger.class.getSimpleName() + " logger = "
                    + LoggerFactory.class.getSimpleName() + ".getLogger(\"" + daoLoggerName + "\");");
        } else {
            String simpleClassName = this.parentClassGenerator.getSimpleClassName();
            sourceCache.addLine("private static " + Logger.class.getSimpleName() + " logger = "
                    + LoggerFactory.class.getSimpleName() + ".getLogger(" + simpleClassName + ".class);");
        }

        sourceCache.addLine();
    }

    /**
     * Generates log statement on debug level for passed log string.
     *
     * @param logString
     */
    public String generateDebugLogStatementByString(String logString) {
        return "logger.debug(\"" + logString + "\");";
    }

    /**
     * Generates log statement on debug level for passed variable name.
     *
     * @param varName
     */
    public String generateDebugLogStatementByVarName(String varName) {
        return "logger.debug(" + varName + ");";
    }
}
