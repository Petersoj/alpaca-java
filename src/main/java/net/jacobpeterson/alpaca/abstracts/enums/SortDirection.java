package net.jacobpeterson.alpaca.abstracts.enums;

/**
 * {@link SortDirection} defines enums for directional sorting.
 */
public enum SortDirection implements APIName {

    /** The Ascending {@link SortDirection}. */
    ASCENDING("asc"),

    /** The Descending {@link SortDirection}. */
    DESCENDING("desc");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link SortDirection}.
     *
     * @param apiName the api name
     */
    SortDirection(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
