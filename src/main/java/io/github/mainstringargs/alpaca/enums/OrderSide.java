package io.github.mainstringargs.alpaca.enums;

/**
 * The Enum OrderSide.
 */
public enum OrderSide {

    /** The buy. */
    BUY("buy"),

    /** The sell. */
    SELL("sell");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new order side.
     *
     * @param apiName the api name
     */
    OrderSide(String apiName) {
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
