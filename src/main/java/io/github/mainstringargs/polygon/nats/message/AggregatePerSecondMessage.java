package io.github.mainstringargs.polygon.nats.message;

import com.google.gson.JsonObject;
import io.github.mainstringargs.polygon.enums.ChannelType;

/**
 * The Class AggregatePerSecondMessage.
 */
public class AggregatePerSecondMessage extends AggregateMessage {

    /**
     * Instantiates a new aggregate per second message.
     *
     * @param cType        the c type
     * @param tickerString the ticker string
     * @param asJsonObject the as json object
     */
    public AggregatePerSecondMessage(ChannelType cType, String tickerString,
                                     JsonObject asJsonObject) {
        super(cType, tickerString, asJsonObject);
    }
}
