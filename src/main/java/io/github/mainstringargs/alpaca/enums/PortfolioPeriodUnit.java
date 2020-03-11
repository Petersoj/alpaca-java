package io.github.mainstringargs.alpaca.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The enum Portfolio period unit.
 */
public enum PortfolioPeriodUnit implements APIName {

    /** Day portfolio period unit. */
    DAY("D"),

    /** Week portfolio period unit. */
    WEEK("W"),

    /** Month portfolio period unit. */
    MONTH("M"),

    /** Year portfolio period unit. */
    YEAR("A");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new Portfolio period unit.
     *
     * @param apiName the api name
     */
    PortfolioPeriodUnit(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
