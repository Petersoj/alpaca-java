package io.github.mainstringargs.alpaca.enums;

/**
 * The Enum AssetStatus.
 */
public enum AssetStatus {

    /** The active. */
    ACTIVE("active"),

    /** The inactive. */
    INACTIVE("inactive");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new asset status.
     *
     * @param apiName the api name
     */
    AssetStatus(String apiName) {
        this.apiName = apiName;
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
