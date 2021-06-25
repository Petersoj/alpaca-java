package net.jacobpeterson.alpaca.websocket.broker.message;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.alpaca.abstracts.enums.APIName;
import net.jacobpeterson.alpaca.abstracts.websocket.message.StreamMessageType;
import net.jacobpeterson.alpaca.util.gson.GsonUtil;

/**
 * {@link AlpacaStreamMessageType} defines enums for various message types for {@link
 * net.jacobpeterson.alpaca.websocket.broker.listener.AlpacaStreamListener}.
 */
public enum AlpacaStreamMessageType implements StreamMessageType, APIName {

    /** The listening status {@link AlpacaStreamMessageType}. */
    @SerializedName("listening")
    LISTENING(false),

    /** The authorization status {@link AlpacaStreamMessageType}. */
    @SerializedName("authorization")
    AUTHORIZATION(false),

    /** The trade updates {@link AlpacaStreamMessageType}. */
    @SerializedName("trade_updates")
    TRADE_UPDATES(true),

    /** The account updates {@link AlpacaStreamMessageType}. */
    @SerializedName("account_updates")
    ACCOUNT_UPDATES(true);

    private final boolean isAPISubscribable;

    /**
     * Instantiates a new {@link AlpacaStreamMessageType}.
     *
     * @param isAPISubscribable true if API is subscribable
     */
    AlpacaStreamMessageType(boolean isAPISubscribable) {
        this.isAPISubscribable = isAPISubscribable;
    }

    @Override
    public String getAPIName() {
        return GsonUtil.GSON.toJsonTree(this).getAsString();
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
