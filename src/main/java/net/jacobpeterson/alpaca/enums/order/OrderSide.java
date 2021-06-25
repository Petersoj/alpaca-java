package net.jacobpeterson.alpaca.enums.order;

import net.jacobpeterson.alpaca.abstracts.enums.APIName;

/**
 * {@link OrderSide} defines enums for various {@link net.jacobpeterson.alpaca.domain.order.Order} sides.
 */
public enum OrderSide implements APIName {

    /** The buy {@link OrderSide}. */
    BUY("buy"),

    /** The sell {@link OrderSide}. */
    SELL("sell");

    private final String apiName;

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
