package io.github.mainstringargs.polygon.websocket.client;

import io.github.mainstringargs.abstracts.websocket.client.AbstractWebsocketClientEndpoint;
import io.github.mainstringargs.abstracts.websocket.client.WebsocketClient;

import java.net.URI;

/**
 * The type Polygon websocket client endpoint.
 */
public class PolygonWebsocketClientEndpoint extends AbstractWebsocketClientEndpoint {

    /**
     * Instantiates a new Polygon websocket client endpoint.
     *
     * @param websocketClient the websocket client
     * @param endpointURI     the endpoint uri
     */
    public PolygonWebsocketClientEndpoint(WebsocketClient websocketClient, URI endpointURI) {
        super(websocketClient, endpointURI, "PolygonWebsocketThread");
    }
}
