package io.github.mainstringargs.alpaca.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The enum Account status.
 *
 * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/orders/">Account Status</a>
 */
public enum OrderClass implements APIName {

    /** The buy. */
    SIMPLE("simple"),

    /** The sell. */
    BRACKET("bracket");

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

