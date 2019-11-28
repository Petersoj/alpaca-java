package io.github.mainstringargs.polygon.websocket.listener;

import io.github.mainstringargs.domain.polygon.websocket.PolygonStreamMessage;
import io.github.mainstringargs.polygon.websocket.message.PolygonStreamMessageType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The Class PolygonStreamListenerAdapter.
 */
public class PolygonStreamListenerAdapter implements PolygonStreamListener {

    /** The stock channel types. */
    private Map<String, Set<PolygonStreamMessageType>> stockChannels = new HashMap<>();

    /**
     * Instantiates a new polygon stream listener adapter.
     *
     * @param ticker       the ticker
     * @param channelTypes the channel types
     */
    public PolygonStreamListenerAdapter(String ticker, PolygonStreamMessageType... channelTypes) {
        if (channelTypes != null && channelTypes.length > 0) {
            this.stockChannels.put(ticker, new HashSet<>(Arrays.asList(channelTypes)));
        } else {
            this.stockChannels.put(ticker, new HashSet<>(Arrays.asList(PolygonStreamMessageType.values())));
        }
    }

    /**
     * Instantiates a new polygon stream listener adapter.
     *
     * @param tickers      the tickers
     * @param channelTypes the channel types
     */
    public PolygonStreamListenerAdapter(Set<String> tickers, PolygonStreamMessageType... channelTypes) {
        for (String ticker : tickers) {
            if (channelTypes != null && channelTypes.length > 0) {
                this.stockChannels.put(ticker, new HashSet<>(Arrays.asList(channelTypes)));
            } else {
                this.stockChannels.put(ticker, new HashSet<>(Arrays.asList(PolygonStreamMessageType.values())));
            }
        }
    }

    /**
     * Instantiates a new polygon stream listener adapter.
     *
     * @param stockChannels the stock channel types
     */
    public PolygonStreamListenerAdapter(Map<String, Set<PolygonStreamMessageType>> stockChannels) {
        for (Entry<String, Set<PolygonStreamMessageType>> entry : stockChannels.entrySet()) {
            this.stockChannels.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
    }

    @Override
    public Map<String, Set<PolygonStreamMessageType>> getStockChannels() {
        return stockChannels;
    }

    @Override
    public void onStreamUpdate(PolygonStreamMessageType streamMessageType, PolygonStreamMessage streamMessage) {
    }
}
