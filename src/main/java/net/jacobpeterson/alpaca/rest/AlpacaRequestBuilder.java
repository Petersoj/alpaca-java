package net.jacobpeterson.alpaca.rest;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.abstracts.rest.AbstractRequestBuilder;

import java.util.Arrays;

/**
 * {@link AlpacaRequestBuilder} is a builder for URL Strings for {@link AlpacaAPI}.
 */
public class AlpacaRequestBuilder extends AbstractRequestBuilder {

    /**
     * Instantiates a new {@link AlpacaRequestBuilder}.
     *
     * @param baseUrl    the base URL
     * @param apiVersion the API version
     * @param endpoints  the endpoints
     */
    public AlpacaRequestBuilder(String baseUrl, String apiVersion, String... endpoints) {
        super(baseUrl);

        if (apiVersion != null) {
            super.appendEndpoint(apiVersion);
        }

        if (endpoints != null) {
            Arrays.stream(endpoints).forEach(super::appendEndpoint);
        }
    }
}
