package de.arthurpicht.barnacle.generator;

import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;

public class GeneratorException extends BarnacleRuntimeException {

    public GeneratorException() {
    }

    public GeneratorException(String message) {
        super(message);
    }

    public GeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneratorException(Throwable cause) {
        super(cause);
    }

    public GeneratorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
