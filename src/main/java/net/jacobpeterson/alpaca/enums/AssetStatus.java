package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The Enum {@link AssetStatus}.
 */
public enum AssetStatus implements APIName {

    /** The active {@link AssetStatus}. */
    ACTIVE("active"),

    /** The inactive {@link AssetStatus}. */
    INACTIVE("inactive");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link AssetStatus}.
     *
     * @param apiName the api name
     */
    AssetStatus(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
