package net.jacobpeterson.alpaca.websocket.updates;

import net.jacobpeterson.alpaca.model.websocket.updates.model.UpdatesMessageType;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocketInterface;

/**
 * {@link UpdatesWebsocketInterface} is an {@link AlpacaWebsocketInterface} for {@link UpdatesWebsocket}.
 */
public interface UpdatesWebsocketInterface extends AlpacaWebsocketInterface {

    /**
     * Sets the {@link UpdatesListener}.
     *
     * @param listener the {@link UpdatesListener}
     */
    void setListener(UpdatesListener listener);

    /**
     * Subscribes to {@link UpdatesMessageType#TRADE_UPDATES}.
     *
     * @param subscribe <code>true</code> to subscribe, <code>false</code> otherwise
     */
    void subscribeToTradeUpdates(boolean subscribe);
}
