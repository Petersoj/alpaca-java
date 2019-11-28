package io.github.mainstringargs.abstracts.websocket.listener;

import io.github.mainstringargs.abstracts.websocket.message.StreamMessage;
import io.github.mainstringargs.abstracts.websocket.message.StreamMessageType;

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
