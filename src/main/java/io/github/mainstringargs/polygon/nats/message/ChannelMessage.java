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
    String getTicker();

    /**
     * Gets the channel type.
     *
     * @return the channel type
     */
    ChannelType getChannelType();
}
