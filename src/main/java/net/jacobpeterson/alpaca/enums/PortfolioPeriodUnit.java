package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The enum {@link PortfolioPeriodUnit}.
 */
public enum PortfolioPeriodUnit implements APIName {

    /** Day {@link PortfolioPeriodUnit}. */
    DAY("D"),

    /** Week {@link PortfolioPeriodUnit}. */
    WEEK("W"),

    /** Month {@link PortfolioPeriodUnit}. */
    MONTH("M"),

    /** Year {@link PortfolioPeriodUnit}. */
    YEAR("A");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link PortfolioPeriodUnit}.
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
