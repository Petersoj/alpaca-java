package io.github.mainstringargs.polygon.enums;

/**
 * The Enum SymbolsMarket.
 */
public enum Market {

    /** The STOCKS. */
    STOCKS("STOCKS"),

    /** The CRYPTO. */
    CRYPTO("CRYPTO"),

    /** The MF. */
    MF("MF"),

    /** The MMF. */
    MMF("MMF"),

    /** The INDICES. */
    INDICES("INDICES"),

    /** The FX. */
    FX("FX");

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

    /**
     * From API name.
     *
     * @param apiName the api name
     * @return the channel type
     */
    public static Market fromAPIName(String apiName) {
        String apiNameString = apiName.trim();

        for (Market cType : Market.values()) {
            if (apiNameString.equals(cType.apiName)) {
                return cType;
            }
        }

        return null;
    }

    /**
     * Gets the API name.
     *
     * @return the API name
     */
    public String getAPIName() {
        return apiName;
    }
}
