package io.github.mainstringargs.polygon.enums;

/**
 * The enum Ticker sort.
 */
public enum TickerSort {

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

    /**
     * From API name.
     *
     * @param apiName the api name
     *
     * @return the channel type
     */
    public static TickerSort fromAPIName(String apiName) {
        String apiNameString = apiName.trim();

        for (TickerSort cType : TickerSort.values()) {
            if (apiNameString.equals(cType.apiName)) {
                return cType;
            }
        }

        return null;
    }

    /**
     * Gets the API name.
     *
     * @return the API name
     */
    public String getAPIName() {
        return apiName;
    }
}
