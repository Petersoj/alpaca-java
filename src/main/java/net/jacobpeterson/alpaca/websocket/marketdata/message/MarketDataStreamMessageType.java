package net.jacobpeterson.alpaca.websocket.marketdata.message;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.abstracts.enums.APIName;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;
import net.jacobpeterson.util.gson.GsonUtil;

/**
 * {@link MarketDataStreamMessageType} defines enums for various message types for {@link
 * net.jacobpeterson.alpaca.websocket.marketdata.listener.MarketDataStreamListener}.
 */
public enum MarketDataStreamMessageType implements StreamMessageType, APIName {

    /** The listening status {@link MarketDataStreamMessageType}. */
    @SerializedName("listening")
    LISTENING(false),

    /** The authorization status {@link MarketDataStreamMessageType}. */
    @SerializedName("authorization")
    AUTHORIZATION(false),

    /** The trades {@link MarketDataStreamMessageType}. */
    @SerializedName("T")
    TRADES(true),

    /** The quotes {@link MarketDataStreamMessageType}. */
    @SerializedName("Q")
    QUOTES(true),

    /** The aggregate minute {@link MarketDataStreamMessageType}. */
    @SerializedName("AM")
    AGGREGATE_MINUTE(true);

    private final boolean isAPISubscribable;

    /**
     * Instantiates a new {@link MarketDataStreamMessageType}.
     *
     * @param isAPISubscribable true if API subscribable
     */
    MarketDataStreamMessageType(boolean isAPISubscribable) {
        this.isAPISubscribable = isAPISubscribable;
    }

    @Override
    public String getAPIName() {
        return GsonUtil.GSON.toJsonTree(this).getAsString();
    }

    /**
     * Returns true if API subscribable.
     *
     * @return true if API subscribable
     */
    public boolean isAPISubscribable() {
        return isAPISubscribable;
    }
}
