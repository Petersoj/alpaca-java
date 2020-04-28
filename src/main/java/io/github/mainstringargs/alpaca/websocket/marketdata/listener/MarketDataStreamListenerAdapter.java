package io.github.mainstringargs.alpaca.websocket.marketdata.listener;

import io.github.mainstringargs.alpaca.websocket.marketdata.message.MarketDataStreamMessageType;
import io.github.mainstringargs.domain.alpaca.marketdata.streaming.MarketDataStreamMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The type Market data stream listener adapter.
 */
public class MarketDataStreamListenerAdapter implements MarketDataStreamListener {

    /** The Data streams. */
    private final Map<String, Set<MarketDataStreamMessageType>> dataStreams = new HashMap<>();

    /**
     * Instantiates a new Market data stream listener adapter.
     *
     * @param ticker           the ticker
     * @param dataMessageTypes the data message types
     */
    public MarketDataStreamListenerAdapter(String ticker, MarketDataStreamMessageType... dataMessageTypes) {
        if (dataMessageTypes != null && dataMessageTypes.length > 0) {
            dataStreams.put(ticker, new HashSet<>(Arrays.asList(dataMessageTypes)));
        } else {
            dataStreams.put(ticker, new HashSet<>(Arrays.asList(MarketDataStreamMessageType.values())));
        }
    }

    /**
     * Instantiates a new Market data stream listener adapter.
     *
     * @param tickers          the tickers
     * @param dataMessageTypes the data message types
     */
    public MarketDataStreamListenerAdapter(Set<String> tickers, MarketDataStreamMessageType... dataMessageTypes) {
        for (String ticker : tickers) {
            if (dataMessageTypes != null && dataMessageTypes.length > 0) {
                dataStreams.put(ticker, new HashSet<>(Arrays.asList(dataMessageTypes)));
            } else {
                dataStreams.put(ticker, new HashSet<>(Arrays.asList(MarketDataStreamMessageType.values())));
            }
        }
    }

    /**
     * Instantiates a new Market data stream listener adapter.
     *
     * @param dataStreams the data streams
     */
    public MarketDataStreamListenerAdapter(Map<String, Set<MarketDataStreamMessageType>> dataStreams) {
        for (Map.Entry<String, Set<MarketDataStreamMessageType>> entry : dataStreams.entrySet()) {
            dataStreams.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
    }

    @Override
    public Map<String, Set<MarketDataStreamMessageType>> getDataStreams() {
        return dataStreams;
    }

    @Override
    public void onStreamUpdate(MarketDataStreamMessageType streamMessageType, MarketDataStreamMessage streamMessage) {
    }
}
