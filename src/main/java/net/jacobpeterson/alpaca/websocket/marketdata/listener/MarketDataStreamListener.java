package net.jacobpeterson.alpaca.websocket.marketdata.listener;

import net.jacobpeterson.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.alpaca.websocket.marketdata.message.MarketDataStreamMessageType;
import net.jacobpeterson.domain.alpaca.marketdata.streaming.MarketDataStreamMessage;

import java.util.Map;
import java.util.Set;

/**
 * An asynchronous update interface for receiving notifications about Websocket information as the Websocket is
 * constructed.
 */
public interface MarketDataStreamListener extends StreamListener<MarketDataStreamMessageType, MarketDataStreamMessage> {

    /**
     * Gets data streams. Null or empty to listen to all stream messages.
     *
     * @return the stream message types
     */
    Map<String, Set<MarketDataStreamMessageType>> getDataStreams();
}
