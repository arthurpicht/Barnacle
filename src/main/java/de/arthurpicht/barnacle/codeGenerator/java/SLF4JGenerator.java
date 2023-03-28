package de.arthurpicht.barnacle.codeGenerator.java;

import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SLF4JGenerator extends LoggerGenerator {

    private final GeneratorConfiguration generatorConfiguration;

    protected SLF4JGenerator(ClassGenerator classGenerator, GeneratorConfiguration generatorConfiguration) {
        super(classGenerator);
        this.generatorConfiguration = generatorConfiguration;
    }

    public void addToImport() {
        ImportGenerator importGenerator = this.classGenerator.importGenerator;
        importGenerator.addImport(Logger.class);
        importGenerator.addImport(LoggerFactory.class);
    }

    public void generateInitialization(SourceCache sourceCache) {
        String loggerName = this.generatorConfiguration.hasDaoLoggerName() ?
                "\"" + this.generatorConfiguration.getDaoLoggerName() + "\"" :
                this.classGenerator.getSimpleClassName() + ".class";
        sourceCache.addLine("private static final " + Logger.class.getSimpleName() + " logger = "
                + LoggerFactory.class.getSimpleName() + ".getLogger(" + loggerName + ");");
        sourceCache.addLine();
    }

    @Override
    public String generateDebugLogStatementByExpression(String expression) {
        return "logger.debug(" + expression + ");";
    }

    @Override
    public String generateDebugLogStatementByString(String logString) {
        return "logger.debug(\"" + logString + "\");";
    }

    @Override
    public String generateDebugLogStatementByVarName(String varName) {
        return "logger.debug(" + varName + ");";
    }

}
