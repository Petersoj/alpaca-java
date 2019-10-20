package io.github.mainstringargs.polygon.nats.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.mainstringargs.polygon.domain.other.StockTrade;
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

    /** The ticker. */
    private String ticker;

    /** The channel type. */
    private ChannelType channelType;

    /** The stock trade. */
    private StockTrade stockTrade;

    /** The timestamp. */
    private LocalDateTime timestamp;

    /**
     * Instantiates a new trades message.
     *
     * @param cType      the c type
     * @param ticker     the ticker
     * @param jsonObject the json object
     */
    public TradesMessage(ChannelType cType, String ticker, JsonObject jsonObject) {
        this.ticker = ticker;
        this.channelType = cType;

        JsonObject jsonQuote = jsonObject.getAsJsonObject();


        stockTrade = gson.fromJson(jsonQuote, StockTrade.class);

        long time = stockTrade.getT();

        if (time > 1560447226296000000L) {
            time = stockTrade.getT() / 1000000L;
        }

        timestamp =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
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
        result = prime * result + ((ticker == null) ? 0 : ticker.hashCode());
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
        if (ticker == null) {
            if (other.ticker != null)
                return false;
        } else if (!ticker.equals(other.ticker))
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
        return "TradesMessage [ticker=" + ticker + ", channelType=" + channelType + ", stockTrade="
                + stockTrade + ", timestamp=" + timestamp + "]";
    }
}
