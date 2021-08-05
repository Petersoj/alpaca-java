package net.jacobpeterson.alpaca.websocket;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * {@link AlpacaWebsocketInterface} defines an interface for Alpaca websockets.
 *
 * @param <L> the {@link AlpacaWebsocketMessageListener} type parameter
 */
public interface AlpacaWebsocketInterface<L extends AlpacaWebsocketMessageListener<?, ?>> {

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
     * Gets a {@link Boolean} {@link Future} that completes when an authentication message that is received after a new
     * websocket connection indicates successful authentication.
     * <br>
     * Note that if this {@link AlpacaWebsocketInterface} is already authorized, the returned {@link Future} will likely
     * never complete.
     *
     * @return a {@link Boolean} {@link Future}
     */
    Future<Boolean> getAuthorizationFuture();

    /**
     * Waits for {@link #getAuthorizationFuture()} to complete and returns its value. After 10 seconds of waiting, this
     * will timeout then return <code>false</code>.
     *
     * @return a boolean
     */
    default boolean waitForAuthorization() {
        try {
            return getAuthorizationFuture().get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {}
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

    /**
     * Sets the {@link AlpacaWebsocketMessageListener} for this {@link AlpacaWebsocketInterface}.
     *
     * @param listener the {@link AlpacaWebsocketMessageListener}
     */
    void setListener(L listener);
}
