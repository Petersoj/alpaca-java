package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The enum Condition mappings type.
 */
public enum ConditionMappingsType implements APIName {

    /** Trades condition mapping type. */
    TRADES("trades"),

    /** Quotes condition mapping type. */
    QUOTES("quotes");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new Condition mappings type.
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
