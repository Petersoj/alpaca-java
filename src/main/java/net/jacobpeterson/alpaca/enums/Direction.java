package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The Enum Direction.
 */
public enum Direction implements APIName {

    /** The asc. */
    ASCENDING("asc"),

    /** The desc. */
    DESCENDING("desc");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new direction.
     *
     * @param apiName the api name
     */
    Direction(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
