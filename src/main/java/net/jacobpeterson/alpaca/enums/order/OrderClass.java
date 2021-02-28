package net.jacobpeterson.alpaca.enums.order;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * {@link OrderClass} defines enums of various {@link net.jacobpeterson.domain.alpaca.order.Order} classes.
 */
public enum OrderClass implements APIName {

    /** Simple {@link OrderClass}. */
    SIMPLE("simple"),

    /** Bracket {@link OrderClass}. */
    BRACKET("bracket"),

    /** One-Cancels-Other {@link OrderClass}. */
    OCO("oco"),

    /** One-Triggers-Other {@link OrderClass}. */
    OTO("oto");

    private final String apiName;

    /**
     * Instantiates a new {@link OrderClass}.
     *
     * @param apiName the API name
     */
    OrderClass(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}

