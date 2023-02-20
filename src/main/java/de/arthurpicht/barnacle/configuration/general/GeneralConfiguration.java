package de.arthurpicht.barnacle.configuration.general;

public class GeneralConfiguration {

    public static final String LOGGER = "logger";
    public static final String LOG_CONFIG_ON_INIT = "log_config_on_init";

    private final String logger;
    private final boolean logConfigOnInit;

    public GeneralConfiguration(
            String logger,
            boolean logConfigOnInit
    ) {
        this.logger = logger;
        this.logConfigOnInit = logConfigOnInit;
    }

    public String getLogger() {
        return logger;
    }

    public boolean isLogConfigOnInit() {
        return logConfigOnInit;
    }

}
