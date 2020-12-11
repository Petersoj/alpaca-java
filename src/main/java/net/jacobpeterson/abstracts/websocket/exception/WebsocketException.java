package net.jacobpeterson.abstracts.websocket.exception;

import java.io.IOException;

/**
 * The type {@link WebsocketException}.
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
     * @param exception the Exception
     */
    public WebsocketException(Exception exception) {
        super(exception.getMessage(), exception.getCause());
    }
}
