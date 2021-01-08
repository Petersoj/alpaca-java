package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * {@link OrderStatus} defines enums for various {@link net.jacobpeterson.domain.alpaca.order.Order} statuses.
 */
public enum OrderStatus implements APIName {

    /** Open {@link OrderStatus}. */
    OPEN("open"),

    /** Closed {@link OrderStatus}. */
    CLOSED("closed"),

    /** All {@link OrderStatus}es. */
    ALL("all");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link OrderStatus}.
     *
     * @param apiName the API name
     */
    OrderStatus(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
