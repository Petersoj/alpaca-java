package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The Enum {@link OrderSide}.
 */
public enum OrderSide implements APIName {

    /** The buy {@link OrderSide}. */
    BUY("buy"),

    /** The sell {@link OrderSide}. */
    SELL("sell");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link OrderSide}.
     *
     * @param apiName the api name
     */
    OrderSide(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
