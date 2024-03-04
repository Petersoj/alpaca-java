package net.jacobpeterson.alpaca.websocket;

import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * {@link AlpacaWebsocketStateListener} is an interface to listen for various state changes in an {@link WebSocket}
 * instance.
 */
public interface AlpacaWebsocketStateListener {

    /**
     * Called when the {@link WebSocket} is connected.
     *
     * @param response the HTTP websocket upgrade {@link Response}
     */
    void onOpen(Response response);

    /**
     * Called when the {@link WebSocket} is disconnected.
     *
     * @param code   the code
     * @param reason the reason
     */
    void onClosed(int code, String reason);

    /**
     * Called when a {@link WebSocket} error has occurred.
     *
     * @param cause the cause {@link Throwable}
     */
    void onFailure(Throwable cause);
}
