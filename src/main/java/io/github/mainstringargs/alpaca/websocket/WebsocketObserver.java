package io.github.mainstringargs.alpaca.websocket;

import java.util.Set;
import io.github.mainstringargs.alpaca.enums.MessageType;

/**
 * An asynchronous update interface for receiving notifications about Websocket information as the
 * Websocket is constructed.
 */
public interface WebsocketObserver {

  public Set<MessageType> getMessageTypes();

  public void streamUpdate(MessageType messageType, Object message);
}
