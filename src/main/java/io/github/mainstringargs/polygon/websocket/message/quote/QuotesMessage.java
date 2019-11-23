package io.github.mainstringargs.polygon.websocket.message.quote;

import com.google.gson.JsonObject;
import io.github.mainstringargs.domain.polygon.websocket.StockQuote;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.websocket.message.ChannelMessage;
import io.github.mainstringargs.util.gson.GsonUtil;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The Class QuotesMessage.
 */
public class QuotesMessage implements ChannelMessage {

    /** The channel type. */
    private ChannelType channelType;

    /** The stock quote. */
    private StockQuote stockQuote;

    /**
     * Instantiates a new quotes message.
     *
     * @param stockQuoteJsonObject the stock quote json object
     */
    public QuotesMessage(JsonObject stockQuoteJsonObject) {
        this.channelType = ChannelType.QUOTES;

        stockQuote = GsonUtil.GSON.fromJson(stockQuoteJsonObject, StockQuote.class);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        QuotesMessage that = (QuotesMessage) o;

        return Objects.equals(this.channelType, that.channelType) &&
                Objects.equals(this.stockQuote, that.stockQuote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelType, stockQuote);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("channelType = " + channelType)
                .add("stockQuote = " + stockQuote)
                .toString();
    }
}
