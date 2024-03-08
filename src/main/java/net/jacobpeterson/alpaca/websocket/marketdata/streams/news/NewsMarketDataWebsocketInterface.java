package net.jacobpeterson.alpaca.websocket.marketdata.streams.news;

import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.news.model.NewsMarketDataMessageType;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataWebsocketInterface;

import java.util.Set;

/**
 * {@link NewsMarketDataWebsocketInterface} is a {@link MarketDataWebsocketInterface} for
 * {@link NewsMarketDataWebsocket}.
 */
public interface NewsMarketDataWebsocketInterface extends MarketDataWebsocketInterface {

    /**
     * Sets the {@link NewsMarketDataListener}.
     *
     * @param listener the {@link NewsMarketDataListener}
     */
    void setListener(NewsMarketDataListener listener);

    /**
     * Subscribes the given <code>symbols</code> to {@link NewsMarketDataMessageType#NEWS}. This will remove all
     * previous {@link NewsMarketDataMessageType#NEWS} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setNewsSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link NewsMarketDataMessageType#NEWS}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getNewsSubscriptions();
}
