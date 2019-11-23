package io.github.mainstringargs.alpaca.websocket.message.trade;

import com.google.gson.JsonObject;
import io.github.mainstringargs.alpaca.enums.StreamUpdateType;
import io.github.mainstringargs.alpaca.websocket.message.ChannelMessage;
import io.github.mainstringargs.domain.alpaca.websocket.TradeUpdate;
import io.github.mainstringargs.util.gson.GsonUtil;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The Class OrderUpdateMessage.
 */
public class TradeUpdateMessage implements ChannelMessage {

    /** The Trade update. */
    private TradeUpdate tradeUpdate;

    /**
     * Instantiates a new Trade update message.
     *
     * @param tradeUpdateJsonObject the trade update json object
     */
    public TradeUpdateMessage(JsonObject tradeUpdateJsonObject) {
        this.tradeUpdate = GsonUtil.GSON.fromJson(tradeUpdateJsonObject, TradeUpdate.class);
    }

    @Override
    public StreamUpdateType getMessageType() {
        return StreamUpdateType.TRADE_UPDATES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        TradeUpdateMessage that = (TradeUpdateMessage) o;

        return Objects.equals(this.tradeUpdate, that.tradeUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeUpdate);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("tradeUpdate = " + tradeUpdate)
                .toString();
    }
}
