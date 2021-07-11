package net.jacobpeterson.alpaca.websocket;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * {@link AlpacaWebsocketInterface} defines an interface for Alpaca websockets.
 */
public interface AlpacaWebsocketInterface {

    /**
     * Connects this Websocket.
     */
    void connect();

    /**
     * Disconnects this Websocket.
     */
    void disconnect();

    /**
     * Returns true if this websocket is connected.
     *
     * @return a boolean
     */
    boolean isConnected();

    /**
     * Returns true if this websocket is authenticated.
     *
     * @return a boolean
     */
    boolean isAuthenticated();

    /**
     * Gets a {@link Boolean} {@link Future} that completes when the next authentication message that is received
     * indicates a successful websocket authorization or not.
     *
     * @return a {@link Boolean} {@link Future}
     */
    Future<Boolean> getAuthorizationFuture();

    /**
     * Waits for {@link #getAuthorizationFuture()} to complete and returns its value.
     *
     * @return a boolean
     */
    default boolean waitForAuthorization() {
        try {
            return getAuthorizationFuture().get();
        } catch (InterruptedException | ExecutionException ignored) {}
        return false;
    }

    /**
     * True if {@link #isConnected()} and {@link #isAuthenticated()}.
     *
     * @return a boolean
     */
    default boolean isValid() {
        return isConnected() && isAuthenticated();
    }
}
