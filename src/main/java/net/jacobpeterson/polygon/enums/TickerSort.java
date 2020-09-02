package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The enum Ticker sort.
 */
public enum TickerSort implements APIName {

    /** The ticker ascending. */
    TICKER_ASCENDING("ticker"),

    /** The ticker descending. */
    TICKER_DESCENDING("-ticker"),

    /** The type ascending. */
    TYPE_ASCENDING("type"),

    /** The type descending. */
    TYPE_DESCENDING("-type");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new order type.
     *
     * @param apiName the api name
     */
    TickerSort(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
