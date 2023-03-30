package de.arthurpicht.barnacle.configuration.helper;

import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;

public class MandatoryConfigParameterMissing extends BarnacleRuntimeException {

    public MandatoryConfigParameterMissing(String parameterName) {
        super("Mandatory parameter missing in generator config: [" + parameterName + "].");
    }

}
