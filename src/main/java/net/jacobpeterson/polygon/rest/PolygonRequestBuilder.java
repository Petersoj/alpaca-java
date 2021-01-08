package net.jacobpeterson.polygon.rest;

import net.jacobpeterson.abstracts.rest.AbstractRequestBuilder;

import java.util.Arrays;

/**
 * {@link PolygonRequestBuilder} is a builder for URL Strings for {@link net.jacobpeterson.polygon.PolygonAPI}.
 */
public class PolygonRequestBuilder extends AbstractRequestBuilder {

    /**
     * Instantiates a new {@link PolygonRequestBuilder}.
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
