package io.github.mainstringargs.polygon.enums;

/**
 * The Enum Timespan.
 */
public enum Timespan {

    /** The Minute. */
    Minute("minute"),
    /** The Hour. */
    Hour("hour"),
    /** The Day. */
    Day("day"),
    /** The Week. */
    Week("week"),
    /** The Month. */
    Month("month"),
    /** The Quarter. */
    Quarter("quarter"),
    /** The Year. */
    Year("year");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new order type.
     *
     * @param apiName the api name
     */
    Timespan(String apiName) {
        this.apiName = apiName;
    }

    /**
     * From API name.
     *
     * @param apiName the api name
     *
     * @return the channel type
     */
    public static Timespan fromAPIName(String apiName) {
        String apiNameString = apiName.trim();

        for (Timespan cType : Timespan.values()) {
            if (apiNameString.equals(cType.apiName)) {
                return cType;
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
