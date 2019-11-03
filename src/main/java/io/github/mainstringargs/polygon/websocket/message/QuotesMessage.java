package io.github.mainstringargs.polygon.websocket.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.mainstringargs.domain.polygon.websocket.StockQuote;
import io.github.mainstringargs.polygon.enums.ChannelType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * The Class QuotesMessage.
 */
public class QuotesMessage implements ChannelMessage {

    /** The gson. */
    private static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        gson = gsonBuilder.create();
    }

    /** The channel type. */
    private ChannelType channelType;

    /** The stock quote. */
    private StockQuote stockQuote;

    /** The timestamp. */
    private LocalDateTime timestamp;

    /**
     * Instantiates a new quotes message.
     *
     * @param jsonObject the json object
     */
    public QuotesMessage(JsonObject jsonObject) {
        this.channelType = ChannelType.QUOTES;

        JsonObject jsonQuote = jsonObject.getAsJsonObject();

        stockQuote = gson.fromJson(jsonQuote, StockQuote.class);

        long time = stockQuote.getT();

        timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    /*
     * (non-Javadoc)
     *
     * @see io.github.mainstringargs.polygon.nats.message.ChannelMessage#getTicker()
     */
    @Override
    public String getTicker() {
        return stockQuote.getSym();
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
     * Gets the stock quote.
     *
     * @return the stock quote
     */
    public StockQuote getStockQuote() {
        return stockQuote;
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
        result = prime * result + ((stockQuote == null) ? 0 : stockQuote.hashCode());
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
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        QuotesMessage other = (QuotesMessage) obj;
        if (channelType != other.channelType) { return false; }
        if (stockQuote == null) {
            if (other.stockQuote != null) { return false; }
        } else if (!stockQuote.equals(other.stockQuote)) { return false; }
        if (timestamp == null) {
            return other.timestamp == null;
        } else return timestamp.equals(other.timestamp);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QuotesMessage [channelType=" + channelType + ", stockQuote="
                + stockQuote + ", timestamp=" + timestamp + "]";
    }
}
