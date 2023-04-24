package de.arthurpicht.barnacle.configuration.helper;

import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;

public class MandatoryConfigParameterMissing extends BarnacleRuntimeException {

    public MandatoryConfigParameterMissing(String sectionName, String parameterName) {
        super("Mandatory parameter [" + parameterName + "] missing in section [" + sectionName + "] " +
                "of barnacle configuration file.");
    }

}
