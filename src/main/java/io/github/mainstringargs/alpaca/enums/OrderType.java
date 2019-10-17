package io.github.mainstringargs.alpaca.enums;

/**
 * The Enum OrderType.
 */
public enum OrderType {

    /** The market. */
    MARKET("market"),

    /** The limit. */
    LIMIT("limit"),

    /** The stop. */
    STOP("stop"),

    /** The stop limit. */
    STOP_LIMIT("stop_limit");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new order type.
     *
     * @param apiName the api name
     */
    OrderType(String apiName) {
        this.apiName = apiName;
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
