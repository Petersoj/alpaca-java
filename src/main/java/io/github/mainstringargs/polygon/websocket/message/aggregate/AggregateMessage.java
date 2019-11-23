package io.github.mainstringargs.polygon.websocket.message.aggregate;

import com.google.gson.JsonObject;
import io.github.mainstringargs.domain.polygon.websocket.StockAggregate;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.websocket.message.ChannelMessage;
import io.github.mainstringargs.util.gson.GsonUtil;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The Class AggregateMessage.
 */
public abstract class AggregateMessage implements ChannelMessage {

    /** The channel type. */
    private ChannelType channelType;

    /** The stock aggregate. */
    private StockAggregate stockAggregate;

    /**
     * Instantiates a new aggregate message.
     *
     * @param channelType              the channel type
     * @param stockAggregateJsonObject the stock aggregate json object
     */
    public AggregateMessage(ChannelType channelType, JsonObject stockAggregateJsonObject) {
        this.channelType = channelType;

        stockAggregate = GsonUtil.GSON.fromJson(stockAggregateJsonObject, StockAggregate.class);
    }

    @Override
    public String getTicker() {
        return stockAggregate.getSym();
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        AggregateMessage that = (AggregateMessage) o;

        return Objects.equals(this.channelType, that.channelType) &&
                Objects.equals(this.stockAggregate, that.stockAggregate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelType, stockAggregate);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("channelType = " + channelType)
                .add("stockAggregate = " + stockAggregate)
                .toString();
    }
}
