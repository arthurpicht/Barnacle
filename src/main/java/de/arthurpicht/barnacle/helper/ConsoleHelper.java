package de.arthurpicht.barnacle.helper;

import de.arthurpicht.console.message.Level;
import de.arthurpicht.console.message.Message;
import de.arthurpicht.console.message.MessageBuilder;

public class ConsoleHelper {

    public static Message verbose(String text) {
        return new MessageBuilder()
                .addText(text)
                .asLevel(Level.VERBOSE)
                .build();
    }

    public static Message veryVerbose(String text) {
        return new MessageBuilder()
                .addText(text)
                .asLevel(Level.VERY_VERBOSE)
                .build();
    }

    public static Message error(String text) {
        return new MessageBuilder()
                .addText(text)
                .toErrorStream()
                .build();
    }

}
