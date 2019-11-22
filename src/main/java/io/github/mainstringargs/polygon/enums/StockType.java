package io.github.mainstringargs.polygon.enums;

/**
 * The Enum StockType.
 */
public enum StockType {

    /** The etfs. */
    ETFS("etp"),

    /** The common stocks. */
    COMMON_STOCKS("cs");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new stock type.
     *
     * @param apiName the api name
     */
    StockType(String apiName) {
        this.apiName = apiName;
    }

    /**
     * From API name.
     *
     * @param apiName the api name
     *
     * @return the channel type
     */
    public static StockType fromAPIName(String apiName) {
        String apiNameString = apiName.trim();

        for (StockType stockType : StockType.values()) {
            if (apiNameString.equals(stockType.apiName)) {
                return stockType;
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
