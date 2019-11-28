package io.github.mainstringargs.alpaca.websocket.client;

import io.github.mainstringargs.abstracts.websocket.client.AbstractWebsocketClientEndpoint;
import io.github.mainstringargs.abstracts.websocket.client.WebsocketClient;

import java.net.URI;

/**
 * The type Alpaca websocket client endpoint.
 */
public class AlpacaWebsocketClientEndpoint extends AbstractWebsocketClientEndpoint {

    /**
     * Instantiates a new Alpaca websocket client endpoint.
     *
     * @param websocketClient the websocket client
     * @param endpointURI     the endpoint uri
     */
    public AlpacaWebsocketClientEndpoint(WebsocketClient websocketClient, URI endpointURI) {
        super(websocketClient, endpointURI, "AlpacaWebsocketThread");
    }
}
