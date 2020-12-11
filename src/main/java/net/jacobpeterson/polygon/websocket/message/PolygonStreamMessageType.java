package net.jacobpeterson.polygon.websocket.message;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.abstracts.enums.APIName;
import net.jacobpeterson.abstracts.websocket.message.StreamMessageType;
import net.jacobpeterson.util.gson.GsonUtil;

/**
 * The enum {@link PolygonStreamMessageType}.
 */
public enum PolygonStreamMessageType implements StreamMessageType, APIName {

    /** Status {@link PolygonStreamMessageType}. */
    @SerializedName("status")
    STATUS(false),

    /** Trade {@link PolygonStreamMessageType}. */
    @SerializedName("T")
    TRADE(true),

    /** Quote {@link PolygonStreamMessageType}. */
    @SerializedName("Q")
    QUOTE(true),

    /** Aggregate per second {@link PolygonStreamMessageType}. */
    @SerializedName("A")
    AGGREGATE_PER_SECOND(true),

    /** Aggregate per minute {@link PolygonStreamMessageType}. */
    @SerializedName("AM")
    AGGREGATE_PER_MINUTE(true);

    /** Is API subscribable. */
    private final boolean isAPISubscribable;

    /**
     * Instantiates a new {@link PolygonStreamMessageType}.
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
     * Is API subscribable boolean.
     *
     * @return the boolean
     */
    public boolean isAPISubscribable() {
        return isAPISubscribable;
    }
}

