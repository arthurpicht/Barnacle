package de.arthurpicht.barnacle.configuration.general;

public class GeneralConfigurationBuilder {

    private String logger;
    private boolean logConfigOnInit;

    public GeneralConfigurationBuilder() {
        this.logger = "BARNACLE";
        this.logConfigOnInit = false;
    }

    public GeneralConfigurationBuilder withLogger(String logger) {
        this.logger = logger;
        return this;
    }

    public GeneralConfigurationBuilder withLogConfigOnInit(boolean logConfigOnInit) {
        this.logConfigOnInit = logConfigOnInit;
        return this;
    }

    public GeneralConfiguration build() {
        return new GeneralConfiguration(
                this.logger,
                this.logConfigOnInit
        );
    }

}
