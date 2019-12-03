package io.github.mainstringargs.polygon.rest;

import io.github.mainstringargs.abstracts.rest.AbstractRequestBuilder;

import java.util.Arrays;

/**
 * The Class PolygonRequestBuilder.
 */
public class PolygonRequestBuilder extends AbstractRequestBuilder {

    /**
     * Instantiates a new Polygon request builder.
     *
     * @param baseUrl    the base url
     * @param apiVersion the api version
     * @param endpoints  the endpoints
     */
    public PolygonRequestBuilder(String baseUrl, String apiVersion, String... endpoints) {
        super(baseUrl);

        if (apiVersion != null) {
            super.appendEndpoint(apiVersion);
        }

        if (endpoints != null) {
            Arrays.stream(endpoints).forEach(super::appendEndpoint);
        }
    }
}
