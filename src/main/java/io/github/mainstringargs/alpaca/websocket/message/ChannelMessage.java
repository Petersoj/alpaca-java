package io.github.mainstringargs.alpaca.websocket.message;

import io.github.mainstringargs.alpaca.enums.StreamUpdateType;

/**
 * The Interface ChannelMessage.
 */
public interface ChannelMessage {

    /**
     * Gets the message type.
     *
     * @return the message type
     */
    StreamUpdateType getMessageType();
}
