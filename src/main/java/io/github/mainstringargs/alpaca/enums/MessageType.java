package io.github.mainstringargs.alpaca.enums;


/**
 * The Enum ObserverType.
 */
public enum MessageType {


    /** The order updates. */
    ORDER_UPDATES("trade_updates"),


    /** The account updates. */
    ACCOUNT_UPDATES("account_updates");


    /** The api name. */
    String apiName;

    /**
     * Instantiates a new observer type.
     *
     * @param apiName the api name
     */
    MessageType(String apiName) {
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
