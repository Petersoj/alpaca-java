package net.jacobpeterson.alpaca.websocket.trades;

import net.jacobpeterson.alpaca.model.websocket.trades.model.TradesStreamMessage;
import net.jacobpeterson.alpaca.model.websocket.trades.model.TradesStreamMessageType;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocketMessageListener;

/**
 * {@link TradesListener} defines a listener interface for {@link TradesWebsocket} messages.
 */
public interface TradesListener extends AlpacaWebsocketMessageListener<TradesStreamMessageType, TradesStreamMessage> {
}
