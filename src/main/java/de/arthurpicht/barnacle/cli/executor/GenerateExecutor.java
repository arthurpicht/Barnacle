package de.arthurpicht.barnacle.cli.executor;

import de.arthurpicht.cli.CliCall;
import de.arthurpicht.cli.CommandExecutor;
import de.arthurpicht.cli.CommandExecutorException;
import de.arthurpicht.console.Console;

public class GenerateExecutor implements CommandExecutor {
    @Override
    public void execute(CliCall cliCall) throws CommandExecutorException {
        Console.println("generate called!");
    }
}
