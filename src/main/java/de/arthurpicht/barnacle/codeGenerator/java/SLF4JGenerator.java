package de.arthurpicht.barnacle.codeGenerator.java;

import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SLF4JGenerator extends LoggerGenerator {

    protected SLF4JGenerator(ClassGenerator classGenerator) {
        super(classGenerator);
    }

    public void addToImport() {
        ImportGenerator importGenerator = this.classGenerator.importGenerator;
        importGenerator.addImport(Logger.class);
        importGenerator.addImport(LoggerFactory.class);
    }

    public void generateInitialization(SourceCache sourceCache) {
        GeneratorConfiguration generatorConfiguration = GeneratorContext.getInstance().getGeneratorConfiguration();
        String loggerName = generatorConfiguration.hasDaoLoggerName() ?
                "\"" + generatorConfiguration.getDaoLoggerName() + "\"" :
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
