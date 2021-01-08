package net.jacobpeterson.alpaca.websocket.broker.listener;

import net.jacobpeterson.alpaca.websocket.broker.message.AlpacaStreamMessageType;
import net.jacobpeterson.domain.alpaca.streaming.AlpacaStreamMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link AlpacaStreamListenerAdapter} is an adapter for {@link AlpacaStreamListener}.
 */
public class AlpacaStreamListenerAdapter implements AlpacaStreamListener {

    private final HashSet<AlpacaStreamMessageType> streamUpdateTypes;

    /**
     * Instantiates a new {@link AlpacaStreamListenerAdapter}.
     *
     * @param streamUpdateTypes the {@link AlpacaStreamMessageType}s
     */
    public AlpacaStreamListenerAdapter(AlpacaStreamMessageType... streamUpdateTypes) {
        this.streamUpdateTypes = new HashSet<>();

        if (streamUpdateTypes != null && streamUpdateTypes.length > 0) {
            this.streamUpdateTypes.addAll(Arrays.asList(streamUpdateTypes));
        }
    }

    @Override
    public Set<AlpacaStreamMessageType> getStreamMessageTypes() {
        return streamUpdateTypes;
    }

    @Override
    public void onStreamUpdate(AlpacaStreamMessageType streamMessageType, AlpacaStreamMessage streamMessage) { }
}
