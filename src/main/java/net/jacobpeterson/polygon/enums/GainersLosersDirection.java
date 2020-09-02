package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

public enum GainersLosersDirection implements APIName {

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

    @Override
    public String getAPIName() {
        return apiName;
    }
}
