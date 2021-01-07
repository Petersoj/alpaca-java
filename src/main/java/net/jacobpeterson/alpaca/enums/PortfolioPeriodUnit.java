package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * {@link PortfolioPeriodUnit} defines enums for various
 * {@link net.jacobpeterson.domain.alpaca.portfoliohistory.PortfolioHistory}
 * period units.
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
     * @param apiName the API name
     */
    PortfolioPeriodUnit(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
