package io.github.mainstringargs.polygon.websocket.message;

import com.google.gson.annotations.SerializedName;
import io.github.mainstringargs.abstracts.enums.APIName;
import io.github.mainstringargs.abstracts.websocket.message.StreamMessageType;
import io.github.mainstringargs.util.gson.GsonUtil;

/**
 * The enum Polygon stream message type.
 */
public enum PolygonStreamMessageType implements StreamMessageType, APIName {

    /** Status polygon stream message type. */
    @SerializedName("status")
    STATUS(false),

    /** Trade polygon stream message type. */
    @SerializedName("T")
    TRADE(true),

    /** Quote polygon stream message type. */
    @SerializedName("Q")
    QUOTE(true),

    /** Aggregate per second polygon stream message type. */
    @SerializedName("A")
    AGGREGATE_PER_SECOND(true),

    /** Aggregate per minute polygon stream message type. */
    @SerializedName("AM")
    AGGREGATE_PER_MINUTE(true);

    /** The is api subscribable. */
    private boolean isAPISubscribable;

    /**
     * Instantiates a new Polygon stream message type.
     *
     * @param isAPISubscribable the is api subscribable
     */
    PolygonStreamMessageType(boolean isAPISubscribable) {
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

