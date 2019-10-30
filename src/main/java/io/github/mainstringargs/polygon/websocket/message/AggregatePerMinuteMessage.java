package io.github.mainstringargs.polygon.websocket.message;

import com.google.gson.JsonObject;
import io.github.mainstringargs.polygon.enums.ChannelType;

/**
 * The Class AggregatePerMinuteMessage.
 */
public class AggregatePerMinuteMessage extends AggregateMessage {

    /**
     * Instantiates a new aggregate per minute message.
     *
     * @param asJsonObject the as json object
     */
    public AggregatePerMinuteMessage(JsonObject asJsonObject) {
        super(ChannelType.AGGREGATE_PER_MINUTE, asJsonObject);
    }
}
