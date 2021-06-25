package net.jacobpeterson.alpaca.enums.asset;

import net.jacobpeterson.alpaca.abstracts.enums.APIName;

/**
 * {@link AssetStatus} defines enums of various asset statuses.
 */
public enum AssetStatus implements APIName {

    /** The active {@link AssetStatus}. */
    ACTIVE("active"),

    /** The inactive {@link AssetStatus}. */
    INACTIVE("inactive");

    private final String apiName;

    /**
     * Instantiates a new {@link AssetStatus}.
     *
     * @param apiName the API name
     */
    AssetStatus(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
