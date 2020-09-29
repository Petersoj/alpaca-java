package net.jacobpeterson.abstracts.websocket.listener;

import net.jacobpeterson.abstracts.websocket.message.StreamMessage;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;

/**
 * The type Stream listener.
 *
 * @param <T> the stream message type type parameter
 * @param <M> the stream message type parameter
 */
public interface StreamListener<T extends StreamMessageType, M extends StreamMessage> {

    /**
     * On stream update.
     *
     * @param streamMessageType the stream message type
     * @param streamMessage     the stream message
     */
    void onStreamUpdate(T streamMessageType, M streamMessage);
}
