package de.arthurpicht.barnacle.generator;

import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;

public class GeneratorPreconditionException extends BarnacleRuntimeException {

    public GeneratorPreconditionException() {
    }

    public GeneratorPreconditionException(String message) {
        super(message);
    }

    public GeneratorPreconditionException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneratorPreconditionException(Throwable cause) {
        super(cause);
    }

    public GeneratorPreconditionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
