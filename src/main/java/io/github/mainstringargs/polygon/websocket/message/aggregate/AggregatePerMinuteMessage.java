package io.github.mainstringargs.polygon.websocket.message.aggregate;

import com.google.gson.JsonObject;
import io.github.mainstringargs.polygon.enums.ChannelType;

/**
 * The Class AggregatePerMinuteMessage.
 */
public class AggregatePerMinuteMessage extends AggregateMessage {

    /**
     * Instantiates a new aggregate per minute message.
     *
     * @param aggregatePerMinuteJsonObject the aggregate per minute json object
     */
    public AggregatePerMinuteMessage(JsonObject aggregatePerMinuteJsonObject) {
        super(ChannelType.AGGREGATE_PER_MINUTE, aggregatePerMinuteJsonObject);
    }
}
