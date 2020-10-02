package net.jacobpeterson.alpaca.websocket.marketdata.client;

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
import java.nio.charset.StandardCharsets;

/**
 * The type Market data websocket client endpoint.
 */
@ClientEndpoint(subprotocols = "STRING")
public class MarketDataWebsocketClientEndpoint extends AbstractWebsocketClientEndpoint {

    /**
     * Instantiates a new Market data websocket client endpoint.
     *
     * @param websocketClient the websocket client
     * @param endpointURI     the endpoint uri
     */
    public MarketDataWebsocketClientEndpoint(WebsocketClient websocketClient, URI endpointURI) {
        super(websocketClient, endpointURI, "MarketDataWebsocketThread");
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
        System.out.println(throwable.getLocalizedMessage());
    }
}
