package net.jacobpeterson.alpaca.websocket.streaming;

import net.jacobpeterson.alpaca.model.endpoint.streaming.enums.StreamingMessageType;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocketInterface;

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
     * <br>
     * Note that this will call {@link StreamingWebsocket#connect()} if the {@link StreamingWebsocket} is not connected
     * already.
     *
     * @param streamingListener the {@link StreamingListener}
     */
    void addListener(StreamingListener streamingListener);

    /**
     * Remove a {@link StreamingListener}.
     * <br>
     * Note that this will call {@link StreamingWebsocket#disconnect()} if this is the last listener being removed.
     *
     * @param streamingListener the {@link StreamingListener}
     */
    void removeListener(StreamingListener streamingListener);
}
