package de.arthurpicht.barnacle.cli.definitions;

import de.arthurpicht.cli.option.ManOption;
import de.arthurpicht.cli.option.OptionBuilder;
import de.arthurpicht.cli.option.Options;
import de.arthurpicht.cli.option.VersionOption;

public class GlobalOptionsDef {

    public static final String STACKTRACE = "stacktrace";
    public static final String VERBOSE = "verbose";
    public static final String DEBUG = "debug";
    public static final String NO_COLOR = "no-color";

    public static Options get() {

        return new Options()
                .add(new VersionOption())
                .add(new ManOption())
                .add(new OptionBuilder()
                        .withShortName('s')
                        .withLongName("stacktrace")
                        .withDescription("show stacktrace when running on error")
                        .build(STACKTRACE))
                .add(new OptionBuilder()
                        .withLongName("verbose")
                        .withDescription("verbose output")
                        .build(VERBOSE))
                .add(new OptionBuilder()
                        .withLongName("debug")
                        .withDescription("output for debugging meta application")
                        .build(DEBUG))
                .add(new OptionBuilder()
                        .withShortName('c')
                        .withLongName("no-color")
                        .withDescription("omit colors on console output")
                        .build(NO_COLOR));
    }

}