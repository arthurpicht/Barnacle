package de.arthurpicht.barnacle.exceptions;

/**
 * Arthur Picht, parcs IT-Consulting GmbH, 21.02.18.
 */
public class BarnacleRuntimeException extends RuntimeException {

    public BarnacleRuntimeException() {
    }

    public BarnacleRuntimeException(String message) {
        super(message);
    }

    public BarnacleRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BarnacleRuntimeException(Throwable cause) {
        super(cause);
    }

    public BarnacleRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
