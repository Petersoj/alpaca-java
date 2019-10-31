package io.github.mainstringargs.polygon.websocket.message;

import com.google.gson.JsonObject;
import io.github.mainstringargs.polygon.enums.ChannelType;

/**
 * The Class AggregatePerSecondMessage.
 */
public class AggregatePerSecondMessage extends AggregateMessage {

    /**
     * Instantiates a new aggregate per second message.
     *
     * @param asJsonObject the as json object
     */
    public AggregatePerSecondMessage(JsonObject asJsonObject) {
        super(ChannelType.AGGREGATE_PER_SECOND, asJsonObject);
    }
}
