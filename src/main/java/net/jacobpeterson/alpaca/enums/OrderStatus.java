package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The Enum {@link OrderStatus}.
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
