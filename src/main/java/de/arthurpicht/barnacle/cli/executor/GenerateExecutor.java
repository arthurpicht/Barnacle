package de.arthurpicht.barnacle.cli.executor;

import de.arthurpicht.barnacle.cli.definitions.GlobalOptionsDef;
import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;
import de.arthurpicht.barnacle.generator.BarnacleGenerator;
import de.arthurpicht.cli.CliCall;
import de.arthurpicht.cli.CommandExecutor;
import de.arthurpicht.cli.CommandExecutorException;
import de.arthurpicht.console.Console;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GenerateExecutor implements CommandExecutor {

    @Override
    public void execute(CliCall cliCall) throws CommandExecutorException {
        Console.verbose("Barnacle code generation started.");
        try {
            if (cliCall.getOptionParserResultGlobal().hasOption(GlobalOptionsDef.CONFIGURATION_FILE)) {
                String configFileString = cliCall.getOptionParserResultGlobal().getValue(GlobalOptionsDef.CONFIGURATION_FILE);
                Path configFilePath = Paths.get(configFileString);
                BarnacleGenerator.process(configFilePath);
            } else {
                BarnacleGenerator.process();
            }
        } catch (BarnacleRuntimeException e) {
            throw new CommandExecutorException(e.getMessage(), e);
        }
    }

}
