package de.arthurpicht.barnacle.model;

import de.arthurpicht.barnacle.generator.GeneratorException;

public class ERMBuilderException extends GeneratorException {

    public ERMBuilderException() {
    }

    public ERMBuilderException(String message) {
        super(message);
    }

    public ERMBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ERMBuilderException(Throwable cause) {
        super(cause);
    }

    public ERMBuilderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
