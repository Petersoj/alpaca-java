package io.github.mainstringargs.alpaca.websocket;

import io.github.mainstringargs.alpaca.enums.StreamUpdateType;
import io.github.mainstringargs.alpaca.websocket.message.ChannelMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class AlpacaStreamListenerAdapter.
 */
public class AlpacaStreamListenerAdapter implements AlpacaStreamListener {

    /** The message types. */
    private HashSet<StreamUpdateType> streamUpdateTypes = new HashSet<>();

    /**
     * Instantiates a new alpaca stream listener adapter.
     *
     * @param streamUpdateTypes the message types
     */
    public AlpacaStreamListenerAdapter(StreamUpdateType... streamUpdateTypes) {
        if (streamUpdateTypes != null && streamUpdateTypes.length > 0) {
            this.streamUpdateTypes.addAll(Arrays.asList(streamUpdateTypes));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see io.github.mainstringargs.alpaca.websocket.AlpacaStreamListener#getMessageTypes()
     */
    @Override
    public Set<StreamUpdateType> getStreamUpdateTypes() {
        return streamUpdateTypes;
    }

    /*
     * (non-Javadoc)
     *
     * @see io.github.mainstringargs.alpaca.websocket.AlpacaStreamListener#streamUpdate(io.github.
     * mainstringargs.alpaca.enums.MessageType, java.lang.Object)
     */
    @Override
    public void streamUpdate(StreamUpdateType streamUpdateType, ChannelMessage message) {
    }
}
