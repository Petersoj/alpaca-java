package net.jacobpeterson.alpaca.websocket.marketdata.client;

import net.jacobpeterson.abstracts.websocket.client.AbstractWebsocketClientEndpoint;
import net.jacobpeterson.abstracts.websocket.client.WebsocketClient;

import javax.websocket.*;
import java.net.URI;

/**
 * {@link MarketDataWebsocketClientEndpoint} is used for handling a Websocket directly for {@link
 * net.jacobpeterson.alpaca.AlpacaAPI} market data.
 */
@ClientEndpoint(subprotocols = "STRING")
public class MarketDataWebsocketClientEndpoint extends AbstractWebsocketClientEndpoint {

    /**
     * Instantiates a new {@link MarketDataWebsocketClientEndpoint}.
     *
     * @param websocketClient the {@link WebsocketClient}
     * @param endpointURI     the endpoint {@link URI}
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
        super.onError(throwable);
    }
}
