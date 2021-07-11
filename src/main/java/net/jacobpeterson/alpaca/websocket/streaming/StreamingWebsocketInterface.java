package net.jacobpeterson.alpaca.websocket.streaming;

import net.jacobpeterson.alpaca.model.endpoint.streaming.enums.StreamingMessageType;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocketInterface;

/**
 * {@link StreamingWebsocketInterface} is an {@link AlpacaWebsocketInterface} for a {@link StreamingWebsocket}.
 */
public interface StreamingWebsocketInterface extends AlpacaWebsocketInterface<StreamingListener> {

    /**
     * Sets subscribed {@link StreamingMessageType}s to <code>streamingMessageTypes</code>.
     *
     * @param streamingMessageTypes the {@link StreamingMessageType}s
     */
    void subscriptions(StreamingMessageType... streamingMessageTypes);
}
