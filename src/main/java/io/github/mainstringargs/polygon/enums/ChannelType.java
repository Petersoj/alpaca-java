package io.github.mainstringargs.polygon.enums;

/**
 * The Enum ChannelType.
 */
public enum ChannelType {

    /** The trades. */
    TRADES("T"),

    /** The quotes. */
    QUOTES("Q"),

    /** The aggregate per second. */
    AGGREGATE_PER_SECOND("A"),

    /** The aggregate per minute. */
    AGGREGATE_PER_MINUTE("AM");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new channel type.
     *
     * @param apiName the api name
     */
    ChannelType(String apiName) {
        this.apiName = apiName;
    }

    /**
     * From API name.
     *
     * @param apiName the api name
     * @return the channel type
     */
    public static ChannelType fromAPIName(String apiName) {
        String apiNameString = apiName.trim();

        for (ChannelType cType : ChannelType.values()) {
            if (apiNameString.equals(cType.apiName)) {
                return cType;
            }
        }

        return null;
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
