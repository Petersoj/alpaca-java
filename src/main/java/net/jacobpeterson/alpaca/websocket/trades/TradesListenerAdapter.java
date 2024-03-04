package net.jacobpeterson.alpaca.websocket.trades;

import net.jacobpeterson.alpaca.model.websocket.trades.model.TradesStreamMessage;
import net.jacobpeterson.alpaca.model.websocket.trades.model.TradesStreamMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link TradesListenerAdapter} is an adapter class for {@link TradesListener}.
 */
public class TradesListenerAdapter implements TradesListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradesListenerAdapter.class);

    @Override
    public void onMessage(TradesStreamMessageType messageType, TradesStreamMessage message) {
        LOGGER.info("{} message received: {}", messageType, message);
    }
}
