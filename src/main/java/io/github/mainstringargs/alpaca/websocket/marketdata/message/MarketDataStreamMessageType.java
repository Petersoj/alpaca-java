package io.github.mainstringargs.alpaca.websocket.marketdata.message;

import com.google.gson.annotations.SerializedName;
import io.github.mainstringargs.abstracts.enums.APIName;
import io.github.mainstringargs.abstracts.websocket.message.StreamMessageType;
import io.github.mainstringargs.util.gson.GsonUtil;

/**
 * The enum Market data stream message type.
 */
public enum MarketDataStreamMessageType implements StreamMessageType, APIName {

    /** Listening market data stream message type. */
    @SerializedName("listening")
    LISTENING(false),

    /** Authorization market data stream message type. */
    @SerializedName("authorization")
    AUTHORIZATION(false),

    /** Trades market data stream message type. */
    @SerializedName("T")
    TRADES(true),

    /** Quotes market data stream message type. */
    @SerializedName("Q")
    QUOTES(true),

    /** Aggregate minute market data stream message type. */
    @SerializedName("AM")
    AGGREGATE_MINUTE(true);

    /** The is api subscribable. */
    private final boolean isAPISubscribable;

    /**
     * Instantiates a new Market data stream message type.
     *
     * @param isAPISubscribable the is api subscribable
     */
    MarketDataStreamMessageType(boolean isAPISubscribable) {
        this.isAPISubscribable = isAPISubscribable;
    }

    @Override
    public String getAPIName() {
        return GsonUtil.GSON.toJsonTree(this).getAsString();
    }

    /**
     * Is the API subscribable.
     *
     * @return the boolean
     */
    public boolean isAPISubscribable() {
        return isAPISubscribable;
    }
}
