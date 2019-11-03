package io.github.mainstringargs.polygon.websocket;

import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.websocket.message.ChannelMessage;

import java.util.Map;
import java.util.Set;

/**
 * The listener interface for receiving polygonStream events. The class that is interested in processing a
 * ChannelMessage event implements this interface, and the object created with that class is registered with a component
 * using the component's <code>addPolygonStreamListener</code> method. When the ChannelMessage event occurs, that
 * object's appropriate method is invoked.
 *
 * @see ChannelMessage
 */
public interface PolygonStreamListener {

    /**
     * Gets the stock channel types. Null or empty to listen to all other stream messages.
     *
     * @return the stock channel types
     */
    Map<String, Set<ChannelType>> getStockChannelTypes();

    /**
     * Stream update.
     *
     * @param ticker      the ticker
     * @param channelType the channel type
     * @param message     the message
     */
    void streamUpdate(String ticker, ChannelType channelType, ChannelMessage message);
}
