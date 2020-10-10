package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The Enum Timespan.
 */
public enum Timespan implements APIName {

    /** The Minute. */
    MINUTE("minute"),

    /** The Hour. */
    HOUR("hour"),

    /** The Day. */
    DAY("day"),

    /** The Week. */
    WEEK("week"),

    /** The Month. */
    MONTH("month"),

    /** The Quarter. */
    QUARTER("quarter"),

    /** The Year. */
    YEAR("year");

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

    @Override
    public String getAPIName() {
        return apiName;
    }
}
