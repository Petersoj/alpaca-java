package io.github.mainstringargs.alpaca.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The Enum AssetStatus.
 */
public enum AssetStatus implements APIName {

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

    @Override
    public String getAPIName() {
        return apiName;
    }
}
