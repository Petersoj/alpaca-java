package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The Enum {@link BarsTimeFrame}.
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
     * @param apiName the api name
     */
    BarsTimeFrame(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
