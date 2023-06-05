package de.arthurpicht.barnacle;

import de.arthurpicht.barnacle.cli.definitions.GenerateDef;
import de.arthurpicht.barnacle.cli.definitions.GlobalOptionsDef;
import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;
import de.arthurpicht.barnacle.helper.ExceptionHelper;
import de.arthurpicht.cli.*;
import de.arthurpicht.cli.command.Commands;
import de.arthurpicht.cli.command.InfoDefaultCommand;
import de.arthurpicht.cli.common.UnrecognizedArgumentException;
import de.arthurpicht.console.Console;
import de.arthurpicht.console.config.ConsoleConfigurationBuilder;
import de.arthurpicht.console.message.Level;
import de.arthurpicht.console.message.format.Format;
import de.arthurpicht.utils.core.strings.Strings;

public class Barnacle {

    private static Cli createCli() {
        Commands commands = new Commands();
        commands.setDefaultCommand(new InfoDefaultCommand());
        commands.add(GenerateDef.get());

        CliDescription cliDescription = new CliDescriptionBuilder()
                .withDescription("barnacle\nhttps://github.com/arthurpicht/Barnacle")
                .withVersionByTag(Const.VERSION_TAG, Const.VERSION_DATE)
                .build("barnacle");

        return new CliBuilder()
                .withGlobalOptions(GlobalOptionsDef.get())
                .withCommands(commands)
                .withAutoHelp()
                .build(cliDescription);
    }

    public static void main(String[] args) {

        Cli cli = createCli();
        CliCall cliCall = null;
        try {
            cliCall = cli.parse(args);
        } catch (UnrecognizedArgumentException e) {
            System.out.println(e.getExecutableName() + " call syntax error. " + e.getMessage());
            System.out.println(e.getCallString());
            System.out.println(e.getCallPointerString());
            System.exit(1);
        }

        boolean showStacktrace = cliCall.getOptionParserResultGlobal().hasOption(GlobalOptionsDef.STACKTRACE);
        boolean verbose = cliCall.getOptionParserResultGlobal().hasOption(GlobalOptionsDef.VERBOSE);
        boolean debug = cliCall.getOptionParserResultGlobal().hasOption(GlobalOptionsDef.DEBUG);

        ConsoleConfigurationBuilder consoleConfigurationBuilder = new ConsoleConfigurationBuilder();
        if (isNoColor(cliCall)) consoleConfigurationBuilder.withSuppressedColors();
        if (verbose) consoleConfigurationBuilder.asLevel(Level.VERBOSE);
        if (debug) consoleConfigurationBuilder.asLevel(Level.VERY_VERBOSE);
        Console.configure(consoleConfigurationBuilder.build());

        try {
            cli.execute(cliCall);
        } catch (CommandExecutorException | BarnacleRuntimeException e) {
            if (e.getCause() != null) {
                errorOut(e.getCause(), showStacktrace);
                System.exit(10);
            }
            if (Strings.isSpecified(e.getMessage())) {
                errorOut(e, showStacktrace);
                System.exit(1);
            }
            System.exit(1);
        } catch (RuntimeException e) {
            errorOut(e, showStacktrace);
            System.exit(11);
        }
    }

    private static void errorOut(Throwable e, boolean showStacktrace) {
        Console.println("ERROR. OPERATION ABORTED.", Format.RED_TEXT());
        Console.println(e.getMessage());
        if (showStacktrace) Console.println(ExceptionHelper.getStackTrace(e));
    }

    private static boolean isNoColor(CliCall cliCall) {
        if (cliCall.getOptionParserResultGlobal().hasOption(GlobalOptionsDef.NO_COLOR)) return true;
        try {
            if (System.getenv("NO_COLOR") != null) return true;
        } catch (SecurityException e) {
            // ignored
        }
        return false;
    }

}
