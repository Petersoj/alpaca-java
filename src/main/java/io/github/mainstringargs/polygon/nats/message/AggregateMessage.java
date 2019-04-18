package io.github.mainstringargs.polygon.nats.message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.mainstringargs.polygon.domain.StockAggregate;
import io.github.mainstringargs.polygon.enums.ChannelType;

/**
 * The Class AggregateMessage.
 */
public class AggregateMessage implements ChannelMessage {


  /** The ticker. */
  private String ticker;

  /** The channel type. */
  private ChannelType channelType;

  /** The stock aggregate. */
  private StockAggregate stockAggregate;

  /** The start. */
  private LocalDateTime start;

  /** The end. */
  private LocalDateTime end;

  /** The gson. */
  private static Gson gson;
  static {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setLenient();
    gson = gsonBuilder.create();
  }


  /**
   * Instantiates a new aggregate message.
   *
   * @param cType the c type
   * @param ticker the ticker
   * @param jsonObject the json object
   */
  public AggregateMessage(ChannelType cType, String ticker, JsonObject jsonObject) {
    this.ticker = ticker;
    this.channelType = cType;

    JsonObject jsonQuote = jsonObject.getAsJsonObject();

    stockAggregate = gson.fromJson(jsonQuote, StockAggregate.class);

    start = LocalDateTime.ofInstant(Instant.ofEpochMilli(stockAggregate.getS()),
        ZoneId.systemDefault());

    end = LocalDateTime.ofInstant(Instant.ofEpochMilli(stockAggregate.getE()),
        ZoneId.systemDefault());
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.polygon.nats.message.ChannelMessage#getTicker()
   */
  @Override
  public String getTicker() {
    return ticker;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.polygon.nats.message.ChannelMessage#getChannelType()
   */
  @Override
  public ChannelType getChannelType() {
    return channelType;
  }


  /**
   * Gets the stock aggregate.
   *
   * @return the stock aggregate
   */
  public StockAggregate getStockAggregate() {
    return stockAggregate;
  }

  /**
   * Gets the start.
   *
   * @return the start
   */
  public LocalDateTime getStart() {
    return start;
  }

  /**
   * Gets the end.
   *
   * @return the end
   */
  public LocalDateTime getEnd() {
    return end;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((channelType == null) ? 0 : channelType.hashCode());
    result = prime * result + ((end == null) ? 0 : end.hashCode());
    result = prime * result + ((start == null) ? 0 : start.hashCode());
    result = prime * result + ((stockAggregate == null) ? 0 : stockAggregate.hashCode());
    result = prime * result + ((ticker == null) ? 0 : ticker.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AggregateMessage other = (AggregateMessage) obj;
    if (channelType != other.channelType)
      return false;
    if (end == null) {
      if (other.end != null)
        return false;
    } else if (!end.equals(other.end))
      return false;
    if (start == null) {
      if (other.start != null)
        return false;
    } else if (!start.equals(other.start))
      return false;
    if (stockAggregate == null) {
      if (other.stockAggregate != null)
        return false;
    } else if (!stockAggregate.equals(other.stockAggregate))
      return false;
    if (ticker == null) {
      if (other.ticker != null)
        return false;
    } else if (!ticker.equals(other.ticker))
      return false;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "AggregateMessage [ticker=" + ticker + ", channelType=" + channelType
        + ", stockAggregate=" + stockAggregate + ", start=" + start + ", end=" + end + "]";
  }



}
