package io.github.mainstringargs.polygon.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The enum Financial sort.
 */
public enum FinancialSort implements APIName {

    /** Report period ascending financial sort. */
    REPORT_PERIOD_ASCENDING("reportPeriod"),

    /** Report period descending financial sort */
    REPORT_PERIOD_DESCENDING("-reportPeriod"),

    /** Calendar date ascending financial sort. */
    CALENDAR_DATE_ASCENDING("calendarDate"),

    /** Calendar date descending financial sort. */
    CALENDAR_DATE_DESCENDING("-calendarDate");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new Financial sort.
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
