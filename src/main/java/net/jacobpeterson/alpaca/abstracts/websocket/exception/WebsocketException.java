package net.jacobpeterson.alpaca.abstracts.websocket.exception;

import java.io.IOException;

/**
 * {@link WebsocketException} is used for Websocket-related exceptions.
 */
public class WebsocketException extends IOException {

    /**
     * Instantiates a new {@link WebsocketException}.
     *
     * @param message the message
     */
    public WebsocketException(String message) {
        super(message);
    }

    /**
     * Instantiates a new {@link WebsocketException}.
     *
     * @param message the message
     * @param cause   the cause
     */
    public WebsocketException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new {@link WebsocketException}.
     *
     * @param exception the {@link Exception}
     */
    public WebsocketException(Exception exception) {
        super(exception.getMessage(), exception.getCause());
    }
}
