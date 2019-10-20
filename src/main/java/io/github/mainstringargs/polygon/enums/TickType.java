package io.github.mainstringargs.polygon.enums;

/**
 * The enum Tick type.
 */
public enum TickType {

    /** Trades tick type. */
    TRADES("trades"),

    /** Quotes tick type. */
    QUOTES("quotes");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new order type.
     *
     * @param apiName the api name
     */
    TickType(String apiName) {
        this.apiName = apiName;
    }

    /**
     * From API name.
     *
     * @param apiName the api name
     * @return the channel type
     */
    public static TickType fromAPIName(String apiName) {
        String apiNameString = apiName.trim();

        for (TickType cType : TickType.values()) {
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
