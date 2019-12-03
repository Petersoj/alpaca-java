package io.github.mainstringargs.alpaca.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The Enum OrderTimeInForce.
 */
public enum OrderTimeInForce implements APIName {

    /** The day. */
    DAY("day"),

    /** The gtc. */
    GTC("gtc"),

    /** The opg. */
    OPG("opg"),

    /** Cls order time in force. */
    CLS("cls"),

    /** Ioc order time in force. */
    IOC("ioc"),

    /** Fok order time in force. */
    FOK("fok");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new order time in force.
     *
     * @param apiName the api name
     */
    OrderTimeInForce(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
