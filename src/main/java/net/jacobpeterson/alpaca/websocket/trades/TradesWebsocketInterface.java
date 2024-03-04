package net.jacobpeterson.alpaca.websocket.trades;

import net.jacobpeterson.alpaca.model.websocket.trades.model.TradesStreamMessageType;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocketInterface;

import java.util.Collection;

/**
 * {@link TradesWebsocketInterface} is an {@link AlpacaWebsocketInterface} for a {@link TradesWebsocket}.
 */
public interface TradesWebsocketInterface extends AlpacaWebsocketInterface<TradesListener> {

    /**
     * Sets the {@link TradesStreamMessageType}s for this stream.
     *
     * @param messageTypes the {@link TradesStreamMessageType}s
     */
    void subscribe(TradesStreamMessageType... messageTypes);

    /**
     * Gets all the currently subscribed {@link TradesStreamMessageType}s.
     *
     * @return a {@link Collection} of {@link TradesStreamMessageType}
     */
    Collection<TradesStreamMessageType> subscriptions();
}
