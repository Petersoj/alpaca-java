package net.jacobpeterson.alpaca.websocket.marketdata.client;

import net.jacobpeterson.alpaca.refactor.AlpacaAPI;
import net.jacobpeterson.alpaca.abstracts.websocket.client.AbstractWebsocketClientEndpoint;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.net.URI;

/**
 * {@link MarketDataWebsocketClientEndpoint} is used for handling a Websocket directly for {@link AlpacaAPI} market
 * data.
 */
@ClientEndpoint(subprotocols = "STRING")
public class MarketDataWebsocketClientEndpoint extends AbstractWebsocketClientEndpoint<MarketDataWebsocketClient> {

    /**
     * Instantiates a new {@link MarketDataWebsocketClientEndpoint}.
     *
     * @param websocketClient the {@link MarketDataWebsocketClient}
     * @param endpointURI     the endpoint {@link URI}
     */
    public MarketDataWebsocketClientEndpoint(MarketDataWebsocketClient websocketClient, URI endpointURI) {
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
