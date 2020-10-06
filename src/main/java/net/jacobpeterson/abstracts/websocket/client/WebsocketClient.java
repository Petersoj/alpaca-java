package net.jacobpeterson.abstracts.websocket.client;

import net.jacobpeterson.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.abstracts.websocket.message.StreamMessage;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;

/**
 * The type Websocket client.
 */
public interface WebsocketClient {

    /**
     * Add listener.
     *
     * @param streamListener the stream listener
     */
    void addListener(StreamListener streamListener);

    /**
     * Remove listener.
     *
     * @param streamListener the stream listener
     */
    void removeListener(StreamListener streamListener);

    /**
     * Connect.
     */
    void connect();

    /**
     * Disconnect.
     */
    void disconnect();

    /**
     * Send authentication message.
     */
    void sendAuthenticationMessage();

    /**
     * Handle resubscribing.
     */
    void handleResubscribing();

    /**
     * Handle websocket message.
     *
     * @param message the message
     */
    void handleWebsocketMessage(String message);

    /**
     * Send stream message to listeners.
     *
     * @param streamMessageType the stream message type
     * @param streamMessage     the stream message
     */
    void sendStreamMessageToListeners(StreamMessageType streamMessageType, StreamMessage streamMessage);

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    boolean isConnected();

    /**
     * Is authenticated boolean.
     *
     * @return the boolean
     */
    boolean isAuthenticated();

}
