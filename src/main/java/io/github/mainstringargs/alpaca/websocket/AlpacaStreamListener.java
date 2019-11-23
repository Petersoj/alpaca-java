package io.github.mainstringargs.alpaca.websocket;

import io.github.mainstringargs.alpaca.enums.StreamUpdateType;
import io.github.mainstringargs.alpaca.websocket.message.ChannelMessage;

import java.util.Set;

/**
 * An asynchronous update interface for receiving notifications about Websocket information as the Websocket is
 * constructed.
 *
 * @see ChannelMessage
 */
public interface AlpacaStreamListener {

    /**
     * Gets the message types. Null or empty to listen to all other stream messages.
     *
     * @return the message types
     */
    Set<StreamUpdateType> getStreamUpdateTypes();

    /**
     * Stream update.
     *
     * @param streamUpdateType the message type
     * @param message     the message
     */
    void streamUpdate(StreamUpdateType streamUpdateType, ChannelMessage message);
}
