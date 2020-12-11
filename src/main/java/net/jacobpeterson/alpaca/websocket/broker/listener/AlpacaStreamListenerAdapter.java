package net.jacobpeterson.alpaca.websocket.broker.listener;

import net.jacobpeterson.alpaca.websocket.broker.message.AlpacaStreamMessageType;
import net.jacobpeterson.domain.alpaca.streaming.AlpacaStreamMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class {@link AlpacaStreamListenerAdapter}.
 */
public class AlpacaStreamListenerAdapter implements AlpacaStreamListener {

    /** The message types. */
    private final HashSet<AlpacaStreamMessageType> streamUpdateTypes = new HashSet<>();

    /**
     * Instantiates a new {@link AlpacaStreamListenerAdapter}.
     *
     * @param streamUpdateTypes the stream update types
     */
    public AlpacaStreamListenerAdapter(AlpacaStreamMessageType... streamUpdateTypes) {
        if (streamUpdateTypes != null && streamUpdateTypes.length > 0) {
            this.streamUpdateTypes.addAll(Arrays.asList(streamUpdateTypes));
        }
    }

    @Override
    public Set<AlpacaStreamMessageType> getStreamMessageTypes() {
        return streamUpdateTypes;
    }

    @Override
    public void onStreamUpdate(AlpacaStreamMessageType streamMessageType, AlpacaStreamMessage streamMessage) {
    }
}
