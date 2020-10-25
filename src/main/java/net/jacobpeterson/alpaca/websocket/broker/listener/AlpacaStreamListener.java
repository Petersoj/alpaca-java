package net.jacobpeterson.alpaca.websocket.broker.listener;

import net.jacobpeterson.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.alpaca.websocket.broker.message.AlpacaStreamMessageType;
import net.jacobpeterson.domain.alpaca.streaming.AlpacaStreamMessage;

import java.util.Set;

/**
 * An asynchronous update interface for receiving websocket messages for
 * {@link net.jacobpeterson.alpaca.websocket.broker.client.AlpacaWebsocketClient}.
 */
public interface AlpacaStreamListener extends StreamListener<AlpacaStreamMessageType, AlpacaStreamMessage> {

    /**
     * Gets the stream message types for this listener. Null or empty to listen to all other stream messages.
     *
     * @return the stream message types
     */
    Set<AlpacaStreamMessageType> getStreamMessageTypes();
}
