package net.jacobpeterson.alpaca.refactor.websocket.streaming;

import net.jacobpeterson.alpaca.model.endpoint.streaming.StreamingMessage;
import net.jacobpeterson.alpaca.model.endpoint.streaming.enums.StreamingMessageType;

/**
 * {@link StreamingListener} defines a listener interface for {@link StreamingWebsocket}
 */
public interface StreamingListener {

    /**
     * Called when a {@link StreamingMessage} is received.
     *
     * @param streamingMessageType the {@link StreamingMessageType}
     * @param streamingMessage     the {@link StreamingMessage}
     */
    void onMessage(StreamingMessageType streamingMessageType, StreamingMessage streamingMessage);
}
