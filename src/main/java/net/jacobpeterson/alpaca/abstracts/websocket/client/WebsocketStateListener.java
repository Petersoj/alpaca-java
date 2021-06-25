package net.jacobpeterson.alpaca.abstracts.websocket.client;

/**
 * {@link WebsocketStateListener} listens for various state changes in a {@link WebsocketClient}.
 */
public interface WebsocketStateListener {

    /**
     * Called when the {@link WebsocketClient} is connected.
     */
    void onConnected();

    /**
     * Called when the {@link WebsocketClient} is disconnected.
     */
    void onDisconnected();

    /**
     * Called when a {@link WebsocketClient} error has occurred.
     *
     * @param cause the cause {@link Throwable}
     */
    void onError(Throwable cause);
}
