package net.jacobpeterson.alpaca.rest;

import net.jacobpeterson.abstracts.rest.AbstractRequestBuilder;

import java.util.Arrays;

/**
 * The Class AlpacaRequestBuilder.
 */
public class AlpacaRequestBuilder extends AbstractRequestBuilder {

    /**
     * Instantiates a new alpaca request builder.
     *
     * @param baseUrl    the base url
     * @param apiVersion the api version
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
