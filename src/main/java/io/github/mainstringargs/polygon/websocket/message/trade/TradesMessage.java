package io.github.mainstringargs.polygon.websocket.message.trade;

import com.google.gson.JsonObject;
import io.github.mainstringargs.domain.polygon.websocket.StockTrade;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.websocket.message.ChannelMessage;
import io.github.mainstringargs.util.gson.GsonUtil;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The Class TradesMessage.
 */
public class TradesMessage implements ChannelMessage {

    /** The channel type. */
    private ChannelType channelType;

    /** The stock trade. */
    private StockTrade stockTrade;

    /**
     * Instantiates a new trades message.
     *
     * @param stockTradeJsonObject the stock trade json object
     */
    public TradesMessage(JsonObject stockTradeJsonObject) {
        this.channelType = ChannelType.TRADES;

        stockTrade = GsonUtil.GSON.fromJson(stockTradeJsonObject, StockTrade.class);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        TradesMessage that = (TradesMessage) o;

        return Objects.equals(this.channelType, that.channelType) &&
                Objects.equals(this.stockTrade, that.stockTrade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelType, stockTrade);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("channelType = " + channelType)
                .add("stockTrade = " + stockTrade)
                .toString();
    }
}
