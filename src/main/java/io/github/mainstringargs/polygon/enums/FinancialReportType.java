package io.github.mainstringargs.polygon.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The enum Financial report type.
 */
public enum FinancialReportType implements APIName {

    /** Y financial report type. */
    Y("Y"),

    /** YA financial report type. */
    YA("YA"),

    /** Q financial report type. */
    Q("Q"),

    /** Qa financial report type. */
    QA("QA"),

    /** T financial report type. */
    T("T"),

    /** Ta financial report type. */
    TA("TA");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new Financial report type.
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
