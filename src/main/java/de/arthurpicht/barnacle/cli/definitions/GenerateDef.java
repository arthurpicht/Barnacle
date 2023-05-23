package de.arthurpicht.barnacle.cli.definitions;

import de.arthurpicht.barnacle.cli.executor.GenerateExecutor;
import de.arthurpicht.cli.command.CommandSequence;
import de.arthurpicht.cli.command.CommandSequenceBuilder;

public class GenerateDef {

    public static CommandSequence get() {
        return new CommandSequenceBuilder()
                .addCommand("generate")
                .withCommandExecutor(new GenerateExecutor())
                .withDescription("generate VOs, DAOs and db schema")
                .build();
    }

}
