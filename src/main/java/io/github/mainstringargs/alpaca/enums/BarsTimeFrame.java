package io.github.mainstringargs.alpaca.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The Enum BarsTimeFrame.
 */
public enum BarsTimeFrame implements APIName {

    /** The minute. */
    MINUTE("minute"),

    /** The one min. */
    ONE_MIN("1Min"),

    /** The five min. */
    FIVE_MIN("5Min"),

    /** The fifteen min. */
    FIFTEEN_MIN("15Min"),

    /** The day. */
    DAY("day"),

    /** The one day. */
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
