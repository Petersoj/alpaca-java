package io.github.mainstringargs.alpaca.websocket.message;

import com.google.gson.annotations.SerializedName;
import io.github.mainstringargs.abstracts.enums.APIName;
import io.github.mainstringargs.abstracts.websocket.message.StreamMessageType;
import io.github.mainstringargs.util.gson.GsonUtil;

/**
 * The enum Alpaca stream message type.
 */
public enum AlpacaStreamMessageType implements StreamMessageType, APIName {

    /** Listening alpaca stream message type. */
    @SerializedName("listening")
    LISTENING(false),

    /** Authorization alpaca stream message type. */
    @SerializedName("authorization")
    AUTHORIZATION(false),

    /** Trade updates alpaca stream message type. */
    @SerializedName("trade_updates")
    TRADE_UPDATES(true),

    /** Account updates alpaca stream message type. */
    @SerializedName("account_updates")
    ACCOUNT_UPDATES(true);

    /** The is api subscribable. */
    private boolean isAPISubscribable;

    /**
     * Instantiates a new Alpaca stream message type.
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
     * Is api subscribable boolean.
     *
     * @return the boolean
     */
    public boolean isAPISubscribable() {
        return isAPISubscribable;
    }
}
