package io.github.mainstringargs.alpaca.websocket.listener;

import io.github.mainstringargs.abstracts.websocket.listener.StreamListener;
import io.github.mainstringargs.alpaca.websocket.message.AlpacaStreamMessageType;
import io.github.mainstringargs.domain.alpaca.websocket.AlpacaStreamMessage;

import java.util.Set;

/**
 * An asynchronous update interface for receiving notifications about Websocket information as the Websocket is
 * constructed.
 */
public interface AlpacaStreamListener extends StreamListener<AlpacaStreamMessageType, AlpacaStreamMessage> {

    /**
     * Gets stream message types. Null or empty to listen to all other stream messages.
     *
     * @return the stream message types
     */
    Set<AlpacaStreamMessageType> getStreamMessageTypes();
}
