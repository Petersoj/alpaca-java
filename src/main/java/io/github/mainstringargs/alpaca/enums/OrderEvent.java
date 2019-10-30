package io.github.mainstringargs.alpaca.enums;

/**
 * The Enum OrderEvent.
 */
public enum OrderEvent {

    /** The new. */
    NEW("new"),

    /** The fill. */
    FILL("fill", "filled"),

    /** The partially filled. */
    PARTIALLY_FILLED("partially_filled", "partial_fill"),

    /** The done for day. */
    DONE_FOR_DAY("done_for_day"),

    /** The canceled. */
    CANCELED("canceled", "cancelled"),

    /** The expired. */
    EXPIRED("expired"),

    /** The order cancel rejected. */
    ORDER_CANCEL_REJECTED("order_cancel_rejected"),

    /** The pending cancel. */
    PENDING_CANCEL("pending_cancel"),

    /** The stopped. */
    STOPPED("stopped"),

    /** The rejected. */
    REJECTED("rejected"),

    /** The suspended. */
    SUSPENDED("suspended"),

    /** The pending new. */
    PENDING_NEW("pending_new"),

    /** The calculated. */
    CALCULATED("calculated");

    /** The api name. */
    private String mainApiName;

    /** The api names. */
    private String[] apiNames;

    /**
     * Instantiates a new order event.
     *
     * @param apiNames the api names
     */
    OrderEvent(String... apiNames) {
        this.mainApiName = apiNames[0];
        this.apiNames = apiNames;
    }

    /**
     * Gets the API name.
     *
     * @param apiNameCheck the api name as a String
     * @return the API name
     */
    public static OrderEvent fromAPIName(String apiNameCheck) {
        for (OrderEvent event : values()) {
            for (String apiName : event.getApiNames()) {
                if (apiName.equals(apiNameCheck.trim())) {
                    return event;
                }
            }
        }

        return null;
    }

    /**
     * Gets the API name.
     *
     * @return the API name
     */
    public String getMainAPIName() {
        return mainApiName;
    }

    /**
     * Gets the api names.
     *
     * @return the api names
     */
    public String[] getApiNames() {
        return apiNames;
    }
}
