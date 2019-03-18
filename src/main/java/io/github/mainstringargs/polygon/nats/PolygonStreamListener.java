package io.github.mainstringargs.polygon.nats;

import java.util.Map;
import java.util.Set;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.nats.message.ChannelMessage;


/**
 * The listener interface for receiving polygonStream events. The class that is interested in
 * processing a polygonStream event implements this interface, and the object created with that
 * class is registered with a component using the component's <code>addPolygonStreamListener<code>
 * method. When the polygonStream event occurs, that object's appropriate method is invoked.
 *
 * @see PolygonStreamEvent
 */
public interface PolygonStreamListener {



  /**
   * Gets the stock channel types.
   *
   * @return the stock channel types
   */
  public Map<String, Set<ChannelType>> getStockChannelTypes();



  /**
   * Stream update.
   *
   * @param ticker the ticker
   * @param channelType the channel type
   * @param message the message
   */
  public void streamUpdate(String ticker, ChannelType channelType, ChannelMessage message);
}
