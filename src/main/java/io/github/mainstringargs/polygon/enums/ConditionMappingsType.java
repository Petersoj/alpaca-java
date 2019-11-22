package io.github.mainstringargs.polygon.enums;

/**
 * The enum Condition mappings type.
 */
public enum ConditionMappingsType {

    /** Trades condition mapping type. */
    TRADES("trades"),

    /** Quotes condition mapping type. */
    QUOTES("quotes");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new Condition mappings type.
     *
     * @param apiName the api name
     */
    ConditionMappingsType(String apiName) {
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
