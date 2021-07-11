package net.jacobpeterson.alpaca.rest.endpoint;

import net.jacobpeterson.alpaca.rest.AlpacaClient;

/**
 * {@link AbstractEndpoint} is an abstract class representing a RestAPI endpoint.
 */
public abstract class AbstractEndpoint {

    protected final AlpacaClient alpacaClient;
    protected final String endpointPathSegment;

    /**
     * Instantiates a new {@link AbstractEndpoint}.
     *
     * @param alpacaClient        the {@link AlpacaClient}
     * @param endpointPathSegment the endpoint path segment relative to {@link AlpacaClient#getBaseURL()}
     */
    public AbstractEndpoint(AlpacaClient alpacaClient, String endpointPathSegment) {
        this.alpacaClient = alpacaClient;
        this.endpointPathSegment = endpointPathSegment;
    }
}
