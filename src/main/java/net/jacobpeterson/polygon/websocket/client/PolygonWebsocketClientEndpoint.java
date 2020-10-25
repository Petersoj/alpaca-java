package net.jacobpeterson.polygon.websocket.client;

import net.jacobpeterson.abstracts.websocket.client.AbstractWebsocketClientEndpoint;
import net.jacobpeterson.abstracts.websocket.client.WebsocketClient;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.net.URI;

/**
 * The type Polygon websocket client endpoint.
 */
@ClientEndpoint(subprotocols = "TEXT")
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

    @OnOpen
    public void onOpenAnnotated(Session userSession) {
        super.onOpen(userSession);
    }

    @OnClose
    public void onCloseAnnotated(Session userSession, CloseReason reason) {
        super.onClose(userSession, reason);
    }

    @OnMessage
    public void onMessageAnnotated(String message) {
        super.onMessage(message);
    }

    @OnError
    public void onError(Throwable throwable) {
        super.onError(throwable);
    }
}
