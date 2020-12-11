package net.jacobpeterson.alpaca.websocket.marketdata.message;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.abstracts.enums.APIName;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;
import net.jacobpeterson.util.gson.GsonUtil;

/**
 * The enum {@link MarketDataStreamMessageType}.
 */
public enum MarketDataStreamMessageType implements StreamMessageType, APIName {

    /** Listening {@link MarketDataStreamMessageType}. */
    @SerializedName("listening")
    LISTENING(false),

    /** Authorization {@link MarketDataStreamMessageType}. */
    @SerializedName("authorization")
    AUTHORIZATION(false),

    /** Trades {@link MarketDataStreamMessageType}. */
    @SerializedName("T")
    TRADES(true),

    /** Quotes {@link MarketDataStreamMessageType}. */
    @SerializedName("Q")
    QUOTES(true),

    /** Aggregate {@link MarketDataStreamMessageType}. */
    @SerializedName("AM")
    AGGREGATE_MINUTE(true);

    /** The is api subscribable. */
    private final boolean isAPISubscribable;

    /**
     * Instantiates a new {@link MarketDataStreamMessageType}.
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
