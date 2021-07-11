package net.jacobpeterson.alpaca.websocket;

/**
 * {@link AlpacaWebsocketMessageListener} defines a listener interface for messages from an {@link AlpacaWebsocket}
 * instances.
 *
 * @param <T> the 'message type' type parameter
 * @param <M> the 'message' type parameter
 */
public interface AlpacaWebsocketMessageListener<T, M> {

    /**
     * Called when a message is received.
     *
     * @param messageType the message type
     * @param message     the message
     */
    void onMessage(T messageType, M message);
}
