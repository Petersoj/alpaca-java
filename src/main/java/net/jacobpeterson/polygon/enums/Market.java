package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The Enum {@link Market}.
 */
public enum Market implements APIName {

    /** Stocks {@link Market}. */
    STOCKS("stocks"),

    /** Indices {@link Market}. */
    INDICES("indices"),

    /** Crypto {@link Market} */
    CRYPTO("crypto"),

    /** Forex {@link Market}. */
    FX("fx"),

    /** Bonds {@link Market}. */
    BONDS("bonds"),

    /** Mutual Funds {@link Market}. */
    MF("mf"),

    /** Money Market Funds {@link Market}. */
    MMF("mmf");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link Market}.
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
