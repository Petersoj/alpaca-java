package io.github.mainstringargs.alpaca.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The enum Account status.
 *
 * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/orders/">Account Status</a>
 */
public enum OrderClass implements APIName {

    /** Simple order class. */
    SIMPLE("simple"),

    /** Bracket order class. */
    BRACKET("bracket"),

    /** One-Cancels-Other order class. */
    OCO("oco"),

    /** One-Triggers-Other order class. */
    OTO("oto");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new order class.
     *
     * @param apiName the api name
     */
    OrderClass(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}

