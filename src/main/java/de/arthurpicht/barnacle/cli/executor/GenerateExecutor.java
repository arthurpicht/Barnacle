package de.arthurpicht.barnacle.cli.executor;

import de.arthurpicht.barnacle.cli.definitions.GlobalOptionsDef;
import de.arthurpicht.cli.CliCall;
import de.arthurpicht.cli.CommandExecutor;
import de.arthurpicht.cli.CommandExecutorException;
import de.arthurpicht.console.Console;

public class GenerateExecutor implements CommandExecutor {
    @Override
    public void execute(CliCall cliCall) throws CommandExecutorException {
        Console.println("generate called!");
        if (cliCall.getOptionParserResultGlobal().hasOption(GlobalOptionsDef.CONFIGURATION_FILE)) {
            Console.println("configuration file: " + cliCall.getOptionParserResultGlobal().getOption(GlobalOptionsDef.CONFIGURATION_FILE));
        } else {
            Console.println("No configuration file specified.");
        }
    }
}
