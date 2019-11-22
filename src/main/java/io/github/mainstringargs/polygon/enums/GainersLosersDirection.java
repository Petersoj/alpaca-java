package io.github.mainstringargs.polygon.enums;

public enum GainersLosersDirection {

    GAINERS("gainers"),

    LOSERS("losers");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new Gainers losers direction.
     *
     * @param apiName the api name
     */
    GainersLosersDirection(String apiName) {
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
