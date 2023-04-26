package de.arthurpicht.barnacle.exceptions;

public class BarnacleIllegalStateException extends BarnacleRuntimeException {

    public BarnacleIllegalStateException() {
    }

    public BarnacleIllegalStateException(String message) {
        super(message);
    }

    public BarnacleIllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BarnacleIllegalStateException(Throwable cause) {
        super(cause);
    }

    public BarnacleIllegalStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
