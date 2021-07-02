package net.jacobpeterson.alpaca.properties.enums;

/**
 * {@link EndpointAPIType} defines the types of API endpoint to use with Alpaca.
 */
public enum EndpointAPIType {

    /** The live {@link EndpointAPIType}. */
    LIVE("https://api.alpaca.markets", "live"),

    /** The paper {@link EndpointAPIType}. */
    PAPER("https://paper-api.alpaca.markets", "paper");

    private final String url;
    private final String propertyName;

    /**
     * Instantiates a new {@link EndpointAPIType}.
     *
     * @param url          the URL
     * @param propertyName the property name
     */
    EndpointAPIType(String url, String propertyName) {
        this.url = url;
        this.propertyName = propertyName;
    }

    /**
     * Gets an {@link EndpointAPIType} from its {@link EndpointAPIType#getPropertyName()}.
     *
     * @param propertyName the property name
     *
     * @return the {@link EndpointAPIType}
     */
    public static EndpointAPIType fromPropertyName(String propertyName) {
        for (EndpointAPIType endpointAPIType : values()) {
            if (endpointAPIType.getPropertyName().equals(propertyName)) {
                return endpointAPIType;
            }
        }

        return null;
    }

    /**
     * Gets URL.
     *
     * @return the URL
     */
    public String getURL() {
        return url;
    }

    /**
     * Gets property name.
     *
     * @return the property name
     */
    public String getPropertyName() {
        return propertyName;
    }
}
