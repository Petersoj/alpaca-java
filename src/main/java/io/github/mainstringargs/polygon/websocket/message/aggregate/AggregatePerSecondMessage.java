package io.github.mainstringargs.polygon.websocket.message.aggregate;

import com.google.gson.JsonObject;
import io.github.mainstringargs.polygon.enums.ChannelType;

/**
 * The Class AggregatePerSecondMessage.
 */
public class AggregatePerSecondMessage extends AggregateMessage {

    /**
     * Instantiates a new aggregate per second message.
     *
     * @param aggregatePerSecondJsonObject the aggregate per second json object
     */
    public AggregatePerSecondMessage(JsonObject aggregatePerSecondJsonObject) {
        super(ChannelType.AGGREGATE_PER_SECOND, aggregatePerSecondJsonObject);
    }
}
