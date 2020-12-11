package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The Enum {@link Direction}.
 */
public enum Direction implements APIName {

    /** The Ascending {@link Direction}. */
    ASCENDING("asc"),

    /** The Descending {@link Direction}. */
    DESCENDING("desc");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link Direction}.
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
