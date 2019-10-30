package io.github.mainstringargs.polygon.websocket.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.mainstringargs.polygon.domain.StockTrade;
import io.github.mainstringargs.polygon.enums.ChannelType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * The Class TradesMessage.
 */
public class TradesMessage implements ChannelMessage {

    /** The gson. */
    private static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        gson = gsonBuilder.create();
    }

    /** The channel type. */
    private ChannelType channelType;

    /** The stock trade. */
    private StockTrade stockTrade;

    /** The timestamp. */
    private LocalDateTime timestamp;

    /**
     * Instantiates a new trades message.
     *
     * @param jsonObject the json object
     */
    public TradesMessage(JsonObject jsonObject) {
        this.channelType = ChannelType.TRADES;

        JsonObject jsonQuote = jsonObject.getAsJsonObject();

        stockTrade = gson.fromJson(jsonQuote, StockTrade.class);

        timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(
                TimeUtil.convertToMilli(stockTrade.getT())), ZoneId.systemDefault());
    }

    /*
     * (non-Javadoc)
     *
     * @see io.github.mainstringargs.polygon.nats.message.ChannelMessage#getTicker()
     */
    @Override
    public String getTicker() {
        return stockTrade.getSym();
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
     * Gets the stock trade.
     *
     * @return the stock trade
     */
    public StockTrade getStockTrade() {
        return stockTrade;
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
        result = prime * result + ((stockTrade == null) ? 0 : stockTrade.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
        TradesMessage other = (TradesMessage) obj;
        if (channelType != other.channelType)
            return false;
        if (stockTrade == null) {
            if (other.stockTrade != null)
                return false;
        } else if (!stockTrade.equals(other.stockTrade))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
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
        return "TradesMessage [channelType=" + channelType + ", stockTrade="
                + stockTrade + ", timestamp=" + timestamp + "]";
    }
}
