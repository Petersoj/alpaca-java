package io.github.mainstringargs.polygon.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The Enum StockType.
 */
public enum StockType implements APIName {

    /** The etfs. */
    ETFS("etp"),

    /** The common stocks. */
    COMMON_STOCKS("cs");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new stock type.
     *
     * @param apiName the api name
     */
    StockType(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
