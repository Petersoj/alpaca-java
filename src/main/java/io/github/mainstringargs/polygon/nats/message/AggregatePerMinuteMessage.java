package io.github.mainstringargs.polygon.nats.message;

import com.google.gson.JsonObject;
import io.github.mainstringargs.polygon.enums.ChannelType;

/**
 * The Class AggregatePerMinuteMessage.
 */
public class AggregatePerMinuteMessage extends AggregateMessage {

    /**
     * Instantiates a new aggregate per minute message.
     *
     * @param cType        the c type
     * @param tickerString the ticker string
     * @param asJsonObject the as json object
     */
    public AggregatePerMinuteMessage(ChannelType cType, String tickerString,
                                     JsonObject asJsonObject) {
        super(cType, tickerString, asJsonObject);
    }
}
