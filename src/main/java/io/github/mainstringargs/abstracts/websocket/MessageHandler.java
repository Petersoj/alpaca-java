package io.github.mainstringargs.abstracts.websocket;

/**
 * Message handler.
 */
public interface MessageHandler<T> {

    /**
     * Handle message.
     *
     * @param message the message
     */
    void handleMessage(T message);
}
