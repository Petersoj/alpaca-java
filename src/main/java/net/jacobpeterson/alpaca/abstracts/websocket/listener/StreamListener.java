package net.jacobpeterson.alpaca.abstracts.websocket.listener;

import net.jacobpeterson.alpaca.abstracts.websocket.message.StreamMessage;
import net.jacobpeterson.alpaca.abstracts.websocket.message.StreamMessageType;

/**
 * {@link StreamListener} is an interface for listening to stream updates.
 *
 * @param <T> the {@link StreamMessageType} type parameter
 * @param <M> the {@link StreamMessage} type parameter
 */
public interface StreamListener<T extends StreamMessageType, M extends StreamMessage> {

    /**
     * Called on a stream update.
     *
     * @param streamMessageType the {@link StreamMessageType}
     * @param streamMessage     the {@link StreamMessage}
     */
    void onStreamUpdate(T streamMessageType, M streamMessage);
}
