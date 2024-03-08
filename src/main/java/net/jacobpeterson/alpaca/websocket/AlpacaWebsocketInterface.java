package net.jacobpeterson.alpaca.websocket;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
     * Returns <code>true</code> if this websocket is connected, <code>false</code> otherwise.
     *
     * @return a boolean
     */
    boolean isConnected();

    /**
     * Returns <code>true</code> if this websocket is authenticated, <code>false</code> otherwise.
     *
     * @return a boolean
     */
    boolean isAuthenticated();

    /**
     * Returns <code>true</code> if {@link #isConnected()} and {@link #isAuthenticated()}, <code>false</code>
     * otherwise.
     *
     * @return a boolean
     */
    default boolean isValid() {
        return isConnected() && isAuthenticated();
    }

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
     * Waits for {@link #getAuthorizationFuture()} to complete and returns its value, except when <code>timeout</code>
     * time has elapsed, then this will return <code>false</code>.
     *
     * @param timeout the timeout time
     * @param unit    the timeout {@link TimeUnit}
     *
     * @return a boolean
     */
    default boolean waitForAuthorization(long timeout, TimeUnit unit) {
        try {
            return getAuthorizationFuture().get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {}
        return false;
    }

    /**
     * Sets the {@link AlpacaWebsocketStateListener}.
     *
     * @param alpacaWebsocketStateListener the {@link AlpacaWebsocketStateListener}
     */
    void setAlpacaWebsocketStateListener(AlpacaWebsocketStateListener alpacaWebsocketStateListener);

    /**
     * Returns <code>true</code> if this websocket automatically reconnects, <code>false</code> otherwise.
     *
     * @return a boolean
     */
    boolean doesAutomaticallyReconnect();

    /**
     * Sets whether to automatically reconnect and reauthenticate on a websocket failure. <code>true</code> by default.
     *
     * @param automaticallyReconnect <code>true</code> to automatically reconnect, <code>false</code> otherwise
     */
    void setAutomaticallyReconnect(boolean automaticallyReconnect);
}
