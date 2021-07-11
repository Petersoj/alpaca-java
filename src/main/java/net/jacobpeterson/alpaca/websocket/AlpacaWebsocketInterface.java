package net.jacobpeterson.alpaca.websocket;

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
     * True if {@link #isConnected()} and {@link #isAuthenticated()}.
     *
     * @return a boolean
     */
    default boolean isValid() {
        return isConnected() && isAuthenticated();
    }
}
