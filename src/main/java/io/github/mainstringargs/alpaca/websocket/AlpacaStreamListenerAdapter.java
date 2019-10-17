package io.github.mainstringargs.alpaca.websocket;

import io.github.mainstringargs.alpaca.enums.MessageType;
import io.github.mainstringargs.alpaca.websocket.message.UpdateMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class AlpacaStreamListenerAdapter.
 */
public class AlpacaStreamListenerAdapter implements AlpacaStreamListener {

    /** The message types. */
    private HashSet<MessageType> messageTypes = new HashSet<>();

    /**
     * Instantiates a new alpaca stream listener adapter.
     *
     * @param messageTypes the message types
     */
    public AlpacaStreamListenerAdapter(MessageType... messageTypes) {
        if (messageTypes != null && messageTypes.length > 0) {
            this.messageTypes.addAll(Arrays.asList(messageTypes));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see io.github.mainstringargs.alpaca.websocket.AlpacaStreamListener#getMessageTypes()
     */
    @Override
    public Set<MessageType> getMessageTypes() {
        return messageTypes;
    }

    /*
     * (non-Javadoc)
     *
     * @see io.github.mainstringargs.alpaca.websocket.AlpacaStreamListener#streamUpdate(io.github.
     * mainstringargs.alpaca.enums.MessageType, java.lang.Object)
     */
    @Override
    public void streamUpdate(MessageType messageType, UpdateMessage message) {
    }
}
