package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The Enum {@link Timespan}.
 */
public enum Timespan implements APIName {

    /** The Minute {@link Timespan}. */
    MINUTE("minute"),

    /** The Hour {@link Timespan}. */
    HOUR("hour"),

    /** The Day {@link Timespan}. */
    DAY("day"),

    /** The Week {@link Timespan}. */
    WEEK("week"),

    /** The Month {@link Timespan}. */
    MONTH("month"),

    /** The Quarter {@link Timespan}. */
    QUARTER("quarter"),

    /** The Year {@link Timespan}. */
    YEAR("year");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link Timespan}.
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
