package net.jacobpeterson.abstracts.websocket.client;

import net.jacobpeterson.abstracts.websocket.exception.WebsocketException;
import net.jacobpeterson.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.abstracts.websocket.message.StreamMessage;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;

/**
 * {@link WebsocketClient} represents a client for a Websocket.
 */
public interface WebsocketClient {

    /**
     * Adds a {@link StreamListener}. Note that this method is blocking.
     *
     * @param streamListener the {@link StreamListener}
     *
     * @throws WebsocketException thrown for {@link WebsocketException}s
     */
    void addListener(StreamListener<?, ?> streamListener) throws WebsocketException;

    /**
     * Removes a {@link StreamListener}. Note that this method is blocking.
     *
     * @param streamListener the {@link StreamListener}
     *
     * @throws WebsocketException thrown for {@link WebsocketException}s
     */
    void removeListener(StreamListener<?, ?> streamListener) throws WebsocketException;

    /**
     * Connects the Websocket. Note that this method is blocking.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    void connect() throws Exception;

    /**
     * Disconnects the Websocket. Note that this method is blocking.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    void disconnect() throws Exception;

    /**
     * Sends the authentication message. Note that this method is blocking.
     */
    void sendAuthenticationMessage();

    /**
     * Handles resubscribing. Note that this method is blocking.
     */
    void handleResubscribing();

    /**
     * Handles a Websocket message.
     *
     * @param message the message
     */
    void handleWebsocketMessage(String message);

    /**
     * Sends a {@link StreamMessage} to {@link StreamListener}s.
     *
     * @param streamMessageType the {@link StreamMessageType}
     * @param streamMessage     the {@link StreamMessage}
     */
    void sendStreamMessageToListeners(StreamMessageType streamMessageType, StreamMessage streamMessage);

    /**
     * Returns true if the Websocket is connected.
     *
     * @return true if the Websocket is connected
     */
    boolean isConnected();

    /**
     * Returns true if the Websocket is authenticated.
     *
     * @return true if the Websocket is authenticated
     */
    boolean isAuthenticated();
}
