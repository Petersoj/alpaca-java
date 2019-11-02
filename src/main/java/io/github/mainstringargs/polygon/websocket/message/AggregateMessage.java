package io.github.mainstringargs.polygon.websocket.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.mainstringargs.domain.polygon.websocket.StockAggregate;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.util.time.TimeUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * The Class AggregateMessage.
 */
public abstract class AggregateMessage implements ChannelMessage {

    /** The gson. */
    private static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        gson = gsonBuilder.create();
    }

    /** The channel type. */
    private ChannelType channelType;

    /** The stock aggregate. */
    private StockAggregate stockAggregate;

    /** The start. */
    private LocalDateTime start;

    /** The end. */
    private LocalDateTime end;

    /**
     * Instantiates a new aggregate message.
     *
     * @param channelType the channel type
     * @param jsonObject  the json object
     */
    public AggregateMessage(ChannelType channelType, JsonObject jsonObject) {
        this.channelType = channelType;

        JsonObject jsonQuote = jsonObject.getAsJsonObject();

        stockAggregate = gson.fromJson(jsonQuote, StockAggregate.class);

        start = LocalDateTime.ofInstant(Instant.ofEpochMilli(TimeUtil.convertToMilli(stockAggregate.getS())),
                ZoneId.systemDefault());

        end = LocalDateTime.ofInstant(Instant.ofEpochMilli(TimeUtil.convertToMilli(stockAggregate.getE())),
                ZoneId.systemDefault());
    }

    /*
     * (non-Javadoc)
     *
     * @see io.github.mainstringargs.polygon.nats.message.ChannelMessage#getTicker()
     */
    @Override
    public String getTicker() {
        return stockAggregate.getSym();
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
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AggregateMessage [channelType=" + channelType
                + ", stockAggregate=" + stockAggregate + ", start=" + start + ", end=" + end + "]";
    }
}
