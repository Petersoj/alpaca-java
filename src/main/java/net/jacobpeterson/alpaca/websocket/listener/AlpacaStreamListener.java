package net.jacobpeterson.alpaca.websocket.listener;

import net.jacobpeterson.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.alpaca.websocket.message.AlpacaStreamMessageType;
import net.jacobpeterson.domain.alpaca.websocket.AlpacaStreamMessage;

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
