package io.github.mainstringargs.alpaca.websocket.marketdata.listener;

import io.github.mainstringargs.abstracts.websocket.listener.StreamListener;
import io.github.mainstringargs.alpaca.websocket.marketdata.message.MarketDataStreamMessageType;
import io.github.mainstringargs.domain.alpaca.marketdata.streaming.MarketDataStreamMessage;

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
