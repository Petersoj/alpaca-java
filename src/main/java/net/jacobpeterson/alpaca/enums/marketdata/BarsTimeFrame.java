package net.jacobpeterson.alpaca.enums.marketdata;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The {@link BarsTimeFrame} enum defines time frame enums for the Bars endpoint.
 */
public enum BarsTimeFrame implements APIName {

    /** 1 Minute {@link BarsTimeFrame}. */
    MINUTE("1Min"),

    /** 1 Hour {@link BarsTimeFrame}. */
    HOUR("1Hour"),

    /** 1 Day {@link BarsTimeFrame}. */
    DAY("1Day");

    private final String apiName;

    /**
     * Instantiates a new {@link BarsTimeFrame}.
     *
     * @param apiName the API name
     */
    BarsTimeFrame(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
