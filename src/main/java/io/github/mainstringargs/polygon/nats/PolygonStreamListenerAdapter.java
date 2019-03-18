package io.github.mainstringargs.polygon.nats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.nats.message.ChannelMessage;


/**
 * The Class PolygonStreamListenerAdapter.
 */
public class PolygonStreamListenerAdapter implements PolygonStreamListener {


  /** The stock channel types. */
  private Map<String, Set<ChannelType>> stockChannelTypes = new HashMap<>();


  /**
   * Instantiates a new polygon stream listener adapter.
   *
   * @param ticker the ticker
   * @param channelTypes the channel types
   */
  public PolygonStreamListenerAdapter(String ticker, ChannelType... channelTypes) {
    if (channelTypes != null && channelTypes.length > 0) {
      stockChannelTypes.put(ticker, new HashSet<ChannelType>(Arrays.asList(channelTypes)));
    } else {
      stockChannelTypes.put(ticker, new HashSet<ChannelType>(Arrays.asList(ChannelType.values())));
    }
  }

  /**
   * Instantiates a new polygon stream listener adapter.
   *
   * @param tickers the tickers
   * @param channelTypes the channel types
   */
  public PolygonStreamListenerAdapter(Set<String> tickers, ChannelType... channelTypes) {

    for (String ticker : tickers) {
      if (channelTypes != null && channelTypes.length > 0) {
        stockChannelTypes.put(ticker, new HashSet<ChannelType>(Arrays.asList(channelTypes)));
      } else {
        stockChannelTypes.put(ticker,
            new HashSet<ChannelType>(Arrays.asList(ChannelType.values())));
      }
    }
  }

  /**
   * Instantiates a new polygon stream listener adapter.
   *
   * @param stockChannelTypes the stock channel types
   */
  public PolygonStreamListenerAdapter(Map<String, Set<ChannelType>> stockChannelTypes) {

    for (Entry<String, Set<ChannelType>> entry : stockChannelTypes.entrySet()) {
      stockChannelTypes.put(entry.getKey(), new HashSet<ChannelType>(entry.getValue()));
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.polygon.nats.PolygonStreamListener#getStockChannelTypes()
   */
  @Override
  public Map<String, Set<ChannelType>> getStockChannelTypes() {
    return stockChannelTypes;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.polygon.nats.PolygonStreamListener#streamUpdate(java.lang.String,
   * io.github.mainstringargs.polygon.enums.ChannelType,
   * io.github.mainstringargs.polygon.nats.message.ChannelMessage)
   */
  @Override
  public void streamUpdate(String ticker, ChannelType channelType, ChannelMessage message) {


  }



}
