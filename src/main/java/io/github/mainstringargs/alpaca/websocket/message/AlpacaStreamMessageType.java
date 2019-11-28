package io.github.mainstringargs.alpaca.websocket.message;

import com.google.gson.annotations.SerializedName;
import io.github.mainstringargs.abstracts.websocket.message.StreamMessageType;

public enum AlpacaStreamMessageType implements StreamMessageType {

    /** Listening alpaca stream message type. */
    @SerializedName("listening")
    LISTENING,

    /** Authorization alpaca stream message type. */
    @SerializedName("authorization")
    AUTHORIZATION,

    /** Trade updates alpaca stream message type. */
    @SerializedName("trade_updates")
    TRADE_UPDATES,

    /** Account updates alpaca stream message type. */
    @SerializedName("account_updates")
    ACCOUNT_UPDATES
}
