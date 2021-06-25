package net.jacobpeterson.alpaca.websocket.marketdata.listener;

import net.jacobpeterson.alpaca.domain.marketdata.realtime.MarketDataMessage;
import net.jacobpeterson.alpaca.websocket.marketdata.message.MarketDataMessageType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link MarketDataListenerAdapter} is an adapter for {@link MarketDataListener}.
 */
public class MarketDataListenerAdapter implements MarketDataListener {

    private final Map<String, Set<MarketDataMessageType>> dataStreams = new HashMap<>();

    /**
     * Instantiates a new {@link MarketDataListenerAdapter}.
     *
     * @param symbol           the symbol
     * @param dataMessageTypes the {@link MarketDataMessageType}s
     */
    public MarketDataListenerAdapter(String symbol, MarketDataMessageType... dataMessageTypes) {
        if (dataMessageTypes != null && dataMessageTypes.length > 0) {
            dataStreams.put(symbol, new HashSet<>(Arrays.asList(dataMessageTypes)));
        } else {
            dataStreams.put(symbol, new HashSet<>(Arrays.asList(MarketDataMessageType.values())));
        }
    }

    /**
     * Instantiates a new {@link MarketDataListenerAdapter}.
     *
     * @param symbols          the symbols
     * @param dataMessageTypes the {@link MarketDataMessageType}s
     */
    public MarketDataListenerAdapter(Set<String> symbols, MarketDataMessageType... dataMessageTypes) {
        for (String symbol : symbols) {
            if (dataMessageTypes != null && dataMessageTypes.length > 0) {
                dataStreams.put(symbol, new HashSet<>(Arrays.asList(dataMessageTypes)));
            } else {
                dataStreams.put(symbol, new HashSet<>(Arrays.asList(MarketDataMessageType.values())));
            }
        }
    }

    /**
     * Instantiates a new {@link MarketDataListenerAdapter}.
     *
     * @param dataStreams the {@link MarketDataMessageType} {@link Set} of symbols {@link Map}
     */
    public MarketDataListenerAdapter(Map<String, Set<MarketDataMessageType>> dataStreams) {
        this.dataStreams.putAll(dataStreams);
    }

    @Override
    public Map<String, Set<MarketDataMessageType>> getDataStreams() {
        return dataStreams;
    }

    @Override
    public void onStreamUpdate(MarketDataMessageType streamMessageType, MarketDataMessage streamMessage) { }
}
