package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The enum {@link ConditionMappingsType}.
 */
public enum ConditionMappingsType implements APIName {

    /** Trades {@link ConditionMappingsType}. */
    TRADES("trades"),

    /** Quotes {@link ConditionMappingsType}. */
    QUOTES("quotes");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link ConditionMappingsType}.
     *
     * @param apiName the api name
     */
    ConditionMappingsType(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
