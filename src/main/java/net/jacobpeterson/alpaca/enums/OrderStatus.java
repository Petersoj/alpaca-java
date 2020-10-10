package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The Enum OrderStatus.
 */
public enum OrderStatus implements APIName {

    /** The open. */
    OPEN("open"),

    /** The closed. */
    CLOSED("closed"),

    /** The all. */
    ALL("all");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new order status.
     *
     * @param apiName the api name
     */
    OrderStatus(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
