package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The enum {@link FinancialSort}.
 */
public enum FinancialSort implements APIName {

    /** Report period ascending {@link FinancialSort}. */
    REPORT_PERIOD_ASCENDING("reportPeriod"),

    /** Report period descending {@link FinancialSort} */
    REPORT_PERIOD_DESCENDING("-reportPeriod"),

    /** Calendar date ascending {@link FinancialSort}. */
    CALENDAR_DATE_ASCENDING("calendarDate"),

    /** Calendar date descending {@link FinancialSort}. */
    CALENDAR_DATE_DESCENDING("-calendarDate");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link FinancialSort}.
     *
     * @param apiName the api name
     */
    FinancialSort(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
