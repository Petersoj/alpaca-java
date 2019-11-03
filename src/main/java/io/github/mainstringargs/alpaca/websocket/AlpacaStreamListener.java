package io.github.mainstringargs.alpaca.websocket;

import io.github.mainstringargs.alpaca.enums.MessageType;
import io.github.mainstringargs.alpaca.websocket.message.UpdateMessage;

import java.util.Set;

/**
 * An asynchronous update interface for receiving notifications about Websocket information as the Websocket is
 * constructed.
 *
 * @see UpdateMessage
 */
public interface AlpacaStreamListener {

    /**
     * Gets the message types. Null or empty to listen to all other stream messages.
     *
     * @return the message types
     */
    Set<MessageType> getMessageTypes();

    /**
     * Stream update.
     *
     * @param messageType the message type
     * @param message     the message
     */
    void streamUpdate(MessageType messageType, UpdateMessage message);
}
