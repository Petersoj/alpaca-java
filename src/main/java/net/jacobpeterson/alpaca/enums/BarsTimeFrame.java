package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The Enum BarsTimeFrame.
 */
public enum BarsTimeFrame implements APIName {

    /** One min bars time frame. */
    ONE_MIN("1Min"),

    /** Five minute bars time frame. */
    FIVE_MINUTE("5Min"),

    /** Fifteen minute bars time frame. */
    FIFTEEN_MINUTE("15Min"),

    /** One day bars time frame. */
    ONE_DAY("1D");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new bars time frame.
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
