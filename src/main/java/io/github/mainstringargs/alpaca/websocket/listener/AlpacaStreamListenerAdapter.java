package io.github.mainstringargs.alpaca.websocket.listener;

import io.github.mainstringargs.alpaca.websocket.message.AlpacaStreamMessageType;
import io.github.mainstringargs.domain.alpaca.websocket.AlpacaStreamMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class AlpacaStreamListenerAdapter.
 */
public class AlpacaStreamListenerAdapter implements AlpacaStreamListener {

    /** The message types. */
    private HashSet<AlpacaStreamMessageType> streamUpdateTypes = new HashSet<>();

    /**
     * Instantiates a new Alpaca stream listener adapter.
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
