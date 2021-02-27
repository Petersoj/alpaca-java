package net.jacobpeterson.alpaca.enums.order;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * {@link OrderSide} defines enums for various {@link net.jacobpeterson.domain.alpaca.order.Order} sides.
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
     * @param apiName the API name
     */
    OrderSide(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
