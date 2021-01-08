package net.jacobpeterson.alpaca.websocket.marketdata.listener;

import net.jacobpeterson.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.alpaca.websocket.marketdata.message.MarketDataStreamMessageType;
import net.jacobpeterson.domain.alpaca.marketdata.streaming.MarketDataStreamMessage;

import java.util.Map;
import java.util.Set;

/**
 * An asynchronous update interface for receiving websocket messages for
 * {@link net.jacobpeterson.alpaca.websocket.marketdata.client.MarketDataWebsocketClient}
 */
public interface MarketDataStreamListener extends StreamListener<MarketDataStreamMessageType, MarketDataStreamMessage> {

    /**
     * Gets the {@link MarketDataStreamMessageType} of tickers {@link Map} for this {@link MarketDataStreamListener}.
     * Null or empty to listen to all stream messages.
     *
     * @return the {@link MarketDataStreamMessageType} of tickers {@link Map}
     */
    Map<String, Set<MarketDataStreamMessageType>> getDataStreams();
}
