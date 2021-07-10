package net.jacobpeterson.alpaca.refactor.websocket.streaming;

import net.jacobpeterson.alpaca.model.endpoint.streaming.enums.StreamingMessageType;
import net.jacobpeterson.alpaca.refactor.websocket.AlpacaWebsocketInterface;

/**
 * {@link StreamingWebsocketInterface} is an {@link AlpacaWebsocketInterface} for a {@link StreamingWebsocket}.
 */
public interface StreamingWebsocketInterface extends AlpacaWebsocketInterface {

    /**
     * Sets subscribed {@link StreamingMessageType}s to <code>streamingMessageTypes</code>.
     *
     * @param streamingMessageTypes the {@link StreamingMessageType}s
     */
    void subscriptions(StreamingMessageType... streamingMessageTypes);

    /**
     * Adds a {@link StreamingListener}.
     *
     * @param streamingListener the {@link StreamingListener}
     */
    void addListener(StreamingListener streamingListener);

    /**
     * Remove a {@link StreamingListener}.
     *
     * @param streamingListener the {@link StreamingListener}
     */
    void removeListener(StreamingListener streamingListener);
}
