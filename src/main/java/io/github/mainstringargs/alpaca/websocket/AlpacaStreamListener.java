package io.github.mainstringargs.alpaca.websocket;

import java.util.Set;
import io.github.mainstringargs.alpaca.enums.MessageType;
import io.github.mainstringargs.alpaca.websocket.message.UpdateMessage;

/**
 * An asynchronous update interface for receiving notifications about Websocket information as the
 * Websocket is constructed.
 */
public interface AlpacaStreamListener {

  public Set<MessageType> getMessageTypes();

  public void streamUpdate(MessageType messageType, UpdateMessage message);
}
