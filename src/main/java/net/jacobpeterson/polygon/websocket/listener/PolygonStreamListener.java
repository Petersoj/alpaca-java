package net.jacobpeterson.polygon.websocket.listener;

import net.jacobpeterson.abstracts.websocket.listener.StreamListener;
import net.jacobpeterson.domain.polygon.websocket.PolygonStreamMessage;
import net.jacobpeterson.polygon.websocket.message.PolygonStreamMessageType;

import java.util.Map;
import java.util.Set;

/**
 * The listener interface for receiving polygonStream events. The class that is interested in processing a
 * ChannelMessage event implements this interface, and the object created with that class is registered with a component
 * using the component's <code>addPolygonStreamListener</code> method. When the ChannelMessage event occurs, that
 * object's appropriate method is invoked.
 */
public interface PolygonStreamListener extends StreamListener<PolygonStreamMessageType, PolygonStreamMessage> {

    /**
     * Gets the stock channels for this listener. Null or empty to listen to all other stream messages.
     *
     * @return the stock channels
     */
    Map<String, Set<PolygonStreamMessageType>> getStockChannels();
}
