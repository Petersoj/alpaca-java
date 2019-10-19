package io.github.mainstringargs.polygon.enums;

/**
 * The enum Gainers losers.
 */
public enum GainersLosers {

    /** The gainers. */
    GAINERS("gainers"),

    /** The losers. */
    LOSERS("losers");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new Gainers losers.
     *
     * @param apiName the api name
     */
    GainersLosers(String apiName) {
        this.apiName = apiName;
    }

    /**
     * From API name.
     *
     * @param apiName the api name
     * @return the channel type
     */
    public static GainersLosers fromAPIName(String apiName) {
        String apiNameString = apiName.trim();

        for (GainersLosers cType : GainersLosers.values()) {
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
