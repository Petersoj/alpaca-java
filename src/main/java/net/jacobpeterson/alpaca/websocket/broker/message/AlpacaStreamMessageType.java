package net.jacobpeterson.alpaca.websocket.broker.message;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.abstracts.enums.APIName;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;
import net.jacobpeterson.util.gson.GsonUtil;

/**
 * The enum {@link AlpacaStreamMessageType}.
 */
public enum AlpacaStreamMessageType implements StreamMessageType, APIName {

    /** Listening {@link AlpacaStreamMessageType}. */
    @SerializedName("listening")
    LISTENING(false),

    /** Authorization {@link AlpacaStreamMessageType}. */
    @SerializedName("authorization")
    AUTHORIZATION(false),

    /** Trade updates {@link AlpacaStreamMessageType}. */
    @SerializedName("trade_updates")
    TRADE_UPDATES(true),

    /** Account updates {@link AlpacaStreamMessageType}. */
    @SerializedName("account_updates")
    ACCOUNT_UPDATES(true);

    /** Is API subscribable. */
    private final boolean isAPISubscribable;

    /**
     * Instantiates a new {@link AlpacaStreamMessageType}.
     *
     * @param isAPISubscribable the is api subscribable
     */
    AlpacaStreamMessageType(boolean isAPISubscribable) {
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
