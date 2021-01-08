package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * {@link BarsTimeFrame} defines enums for time frames of a {@link net.jacobpeterson.domain.alpaca.marketdata.Bar}.
 */
public enum BarsTimeFrame implements APIName {

    /** 1 minute {@link BarsTimeFrame}. */
    ONE_MIN("1Min"),

    /** 5 minute {@link BarsTimeFrame}. */
    FIVE_MINUTE("5Min"),

    /** 15 minute {@link BarsTimeFrame}. */
    FIFTEEN_MINUTE("15Min"),

    /** 1 day {@link BarsTimeFrame}. */
    ONE_DAY("1D");

    /** The API name. */
    String apiName;

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
