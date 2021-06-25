package net.jacobpeterson.alpaca.websocket.broker.listener;

import net.jacobpeterson.alpaca.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.alpaca.domain.streaming.AlpacaStreamMessage;
import net.jacobpeterson.alpaca.websocket.broker.message.AlpacaStreamMessageType;

import java.util.Set;

/**
 * An asynchronous update interface for receiving websocket messages for
 * {@link net.jacobpeterson.alpaca.websocket.broker.client.AlpacaWebsocketClient}.
 */
public interface AlpacaStreamListener extends StreamListener<AlpacaStreamMessageType, AlpacaStreamMessage> {

    /**
     * Gets the {@link AlpacaStreamMessageType}s for this {@link AlpacaStreamListener}. Null or empty to listen to all
     * other stream messages.
     *
     * @return the {@link AlpacaStreamMessageType}s
     */
    Set<AlpacaStreamMessageType> getStreamMessageTypes();
}
