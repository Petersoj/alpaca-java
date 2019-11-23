package io.github.mainstringargs.alpaca.enums;

public enum StreamUpdateType {

    /** The trade updates. */
    TRADE_UPDATES("trade_updates"),

    /** The account updates. */
    ACCOUNT_UPDATES("account_updates");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new Stream update type.
     *
     * @param apiName the api name
     */
    StreamUpdateType(String apiName) {
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
