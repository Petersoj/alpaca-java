package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The enum {@link TickerSort}.
 */
public enum TickerSort implements APIName {

    /** The ticker ascending {@link TickerSort}. */
    TICKER_ASCENDING("ticker"),

    /** The ticker descending {@link TickerSort}. */
    TICKER_DESCENDING("-ticker"),

    /** The type ascending {@link TickerSort}. */
    TYPE_ASCENDING("type"),

    /** The type descending {@link TickerSort}. */
    TYPE_DESCENDING("-type");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link TickerSort}.
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
