package net.jacobpeterson.alpaca.enums.order;

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

    private final String apiName;

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
