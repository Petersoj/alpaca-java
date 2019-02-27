package io.github.mainstringargs.alpaca.websocket;

import java.util.Set;
import io.github.mainstringargs.alpaca.enums.MessageType;
import io.github.mainstringargs.alpaca.websocket.message.UpdateMessage;

/**
 * An asynchronous update interface for receiving notifications about Websocket information as the
 * Websocket is constructed.
 *
 * @see AlpacaStreamEvent
 */
public interface AlpacaStreamListener {

  /**
   * Gets the message types.
   *
   * @return the message types
   */
  public Set<MessageType> getMessageTypes();

  /**
   * Stream update.
   *
   * @param messageType the message type
   * @param message the message
   */
  public void streamUpdate(MessageType messageType, UpdateMessage message);
}
