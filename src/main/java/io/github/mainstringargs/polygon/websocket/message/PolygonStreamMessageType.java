package io.github.mainstringargs.polygon.websocket.message;

import com.google.gson.annotations.SerializedName;
import io.github.mainstringargs.abstracts.websocket.message.StreamMessageType;

public enum PolygonStreamMessageType implements StreamMessageType {

    /** Status polygon stream message type. */
    @SerializedName("status")
    STATUS,

    /** Trade polygon stream message type. */
    @SerializedName("T")
    TRADE,

    /** Quote polygon stream message type. */
    @SerializedName("Q")
    QUOTE,

    /** Aggregate per second polygon stream message type. */
    @SerializedName("A")
    AGGREGATE_PER_SECOND,

    /** Aggregate per minute polygon stream message type. */
    @SerializedName("AM")
    AGGREGATE_PER_MINUTE
}

