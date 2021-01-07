package net.jacobpeterson.alpaca.websocket.broker.client;

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
 * {@link AlpacaWebsocketClientEndpoint} is used for handling a Websocket directly for {@link
 * net.jacobpeterson.alpaca.AlpacaAPI}.
 */
@ClientEndpoint(subprotocols = "BINARY")
public class AlpacaWebsocketClientEndpoint extends AbstractWebsocketClientEndpoint {

    /**
     * Instantiates a new {@link AlpacaWebsocketClientEndpoint}.
     *
     * @param websocketClient the {@link WebsocketClient}
     * @param endpointURI     the endpoint {@link URI}
     */
    public AlpacaWebsocketClientEndpoint(WebsocketClient websocketClient, URI endpointURI) {
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
