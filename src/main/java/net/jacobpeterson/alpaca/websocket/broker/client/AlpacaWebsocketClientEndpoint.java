package net.jacobpeterson.alpaca.websocket.broker.client;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.abstracts.websocket.client.AbstractWebsocketClientEndpoint;

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
 * {@link AlpacaWebsocketClientEndpoint} is used for handling a Websocket directly for {@link AlpacaAPI}.
 */
@ClientEndpoint(subprotocols = "BINARY")
public class AlpacaWebsocketClientEndpoint extends AbstractWebsocketClientEndpoint<AlpacaWebsocketClient> {

    /**
     * Instantiates a new {@link AlpacaWebsocketClientEndpoint}.
     *
     * @param websocketClient the {@link AlpacaWebsocketClient}
     * @param endpointURI     the endpoint {@link URI}
     */
    public AlpacaWebsocketClientEndpoint(AlpacaWebsocketClient websocketClient, URI endpointURI) {
        super(websocketClient, endpointURI, "AlpacaWebsocketThread");
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
    public void onMessageAnnotated(byte[] message) {
        super.onMessage(new String(message, StandardCharsets.UTF_8));
    }

    @OnError
    public void onErrorAnnotated(Throwable throwable) {
        super.onError(throwable);
    }
}
