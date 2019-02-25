package io.github.mainstringargs.alpaca.websocket.message;

import io.github.mainstringargs.alpaca.enums.MessageType;

/**
 * The Interface UpdateMessage.
 */
public interface UpdateMessage {

  /**
   * Gets the message type.
   *
   * @return the message type
   */
  public MessageType getMessageType();
}
