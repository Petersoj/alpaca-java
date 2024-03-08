package net.jacobpeterson.alpaca.websocket.updates;

import net.jacobpeterson.alpaca.model.websocket.updates.model.tradeupdate.TradeUpdateMessage;

/**
 * {@link UpdatesListener} defines a listener interface for {@link UpdatesWebsocketInterface} messages.
 */
@FunctionalInterface
public interface UpdatesListener {

    /**
     * Called when a {@link TradeUpdateMessage} is received.
     *
     * @param tradeUpdate the {@link TradeUpdateMessage}
     */
    void onTradeUpdate(TradeUpdateMessage tradeUpdate);
}
