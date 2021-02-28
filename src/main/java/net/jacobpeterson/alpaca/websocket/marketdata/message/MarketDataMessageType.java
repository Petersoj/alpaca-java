package net.jacobpeterson.alpaca.websocket.marketdata.message;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.abstracts.enums.APIName;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;

/**
 * {@link MarketDataMessageType} defines enums for various market data stream message types.
 */
public enum MarketDataMessageType implements StreamMessageType, APIName {

    /** The success {@link MarketDataMessageType}. */
    @SerializedName("success")
    SUCCESS("success", false),

    /** The error {@link MarketDataMessageType}. */
    @SerializedName("error")
    ERROR("error", false),

    /** The subscription {@link MarketDataMessageType}. */
    @SerializedName("subscription")
    SUBSCRIPTION("subscription", false),

    /** The trade {@link MarketDataMessageType}. */
    @SerializedName("t")
    TRADE("t", true),

    /** The quote {@link MarketDataMessageType}. */
    @SerializedName("q")
    QUOTE("q", true),

    /** The bar {@link MarketDataMessageType}. */
    @SerializedName("b")
    BAR("b", true);

    private final String apiName;
    private final boolean isAPISubscribable;

    /**
     * Instantiates a new {@link MarketDataMessageType}.
     *
     * @param apiName           the API name
     * @param isAPISubscribable true if API is subscribable
     */
    MarketDataMessageType(String apiName, boolean isAPISubscribable) {
        this.apiName = apiName;
        this.isAPISubscribable = isAPISubscribable;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }

    /**
     * Returns true if API is subscribable.
     *
     * @return true if API is subscribable
     */
    public boolean isAPISubscribable() {
        return isAPISubscribable;
    }
}
