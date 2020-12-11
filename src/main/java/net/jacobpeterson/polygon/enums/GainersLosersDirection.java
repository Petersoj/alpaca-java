package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The enum {@link GainersLosersDirection}.
 */
public enum GainersLosersDirection implements APIName {

    /** Gainers {@link GainersLosersDirection}. */
    GAINERS("gainers"),

    /** Losers {@link GainersLosersDirection}. */
    LOSERS("losers");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link GainersLosersDirection}.
     *
     * @param apiName the api name
     */
    GainersLosersDirection(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
