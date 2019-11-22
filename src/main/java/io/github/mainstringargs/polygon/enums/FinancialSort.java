package io.github.mainstringargs.polygon.enums;

/**
 * The enum Financial sort.
 */
public enum FinancialSort {

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

    /**
     * From API name.
     *
     * @param apiName the api name
     *
     * @return the channel type
     */
    public static StockType fromAPIName(String apiName) {
        String apiNameString = apiName.trim();

        for (StockType stockType : StockType.values()) {
            if (apiNameString.equals(stockType.apiName)) {
                return stockType;
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
