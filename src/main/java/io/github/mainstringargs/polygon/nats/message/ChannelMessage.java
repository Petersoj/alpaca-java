package io.github.mainstringargs.polygon.nats.message;

import io.github.mainstringargs.polygon.enums.ChannelType;


/**
 * The Interface UpdateMessage.
 */
public interface ChannelMessage {


  /**
   * Gets the ticker.
   *
   * @return the ticker
   */
  public String getTicker();

  /**
   * Gets the channel type.
   *
   * @return the channel type
   */
  public ChannelType getChannelType();
}
