package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The enum {@link FinancialReportType}.
 */
public enum FinancialReportType implements APIName {

    /** Y {@link FinancialReportType}. */
    Y("Y"),

    /** YA {@link FinancialReportType}. */
    YA("YA"),

    /** Q {@link FinancialReportType}. */
    Q("Q"),

    /** QA {@link FinancialReportType}. */
    QA("QA"),

    /** T {@link FinancialReportType}. */
    T("T"),

    /** TA {@link FinancialReportType}. */
    TA("TA");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link FinancialReportType}.
     *
     * @param apiName the api name
     */
    FinancialReportType(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
