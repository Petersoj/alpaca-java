package io.github.mainstringargs.polygon.enums;

/**
 * The enum Financial report type.
 */
public enum FinancialReportType {

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
