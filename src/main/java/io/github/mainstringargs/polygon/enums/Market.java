package io.github.mainstringargs.polygon.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The Enum SymbolsMarket.
 */
public enum Market implements APIName {

    /** Stocks market. */
    STOCKS("stocks"),

    /** Indices market. */
    INDICES("indices"),

    /** Crypto market */
    CRYPTO("crypto"),

    /** Fx market. */
    FX("fx"),

    /** Bonds market. */
    BONDS("bonds"),

    /** Mf market. */
    MF("mf"),

    /** Mmf market. */
    MMF("mmf");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new order type.
     *
     * @param apiName the api name
     */
    Market(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
