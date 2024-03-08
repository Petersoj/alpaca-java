package net.jacobpeterson.alpaca.websocket.marketdata.streams.crypto;

import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.CryptoMarketDataMessageType;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataWebsocketInterface;

import java.util.Set;

/**
 * {@link CryptoMarketDataWebsocketInterface} is a {@link MarketDataWebsocketInterface} for
 * {@link CryptoMarketDataWebsocket}.
 */
public interface CryptoMarketDataWebsocketInterface extends MarketDataWebsocketInterface {

    /**
     * Sets the {@link CryptoMarketDataListener}.
     *
     * @param listener the {@link CryptoMarketDataListener}
     */
    void setListener(CryptoMarketDataListener listener);

    /**
     * Subscribes the given <code>symbols</code> to {@link CryptoMarketDataMessageType#TRADES}. This will remove all
     * previous {@link CryptoMarketDataMessageType#QUOTES} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setTradeSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link CryptoMarketDataMessageType#TRADES}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getTradeSubscriptions();

    /**
     * Subscribes the given <code>symbols</code> to {@link CryptoMarketDataMessageType#QUOTES}. This will remove all
     * previous {@link CryptoMarketDataMessageType#QUOTES} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setQuoteSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link CryptoMarketDataMessageType#QUOTES}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getQuoteSubscriptions();

    /**
     * Subscribes the given <code>symbols</code> to {@link CryptoMarketDataMessageType#MINUTE_BARS}. This will remove
     * all previous {@link CryptoMarketDataMessageType#QUOTES} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setMinuteBarSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link CryptoMarketDataMessageType#MINUTE_BARS}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getMinuteBarSubscriptions();

    /**
     * Subscribes the given <code>symbols</code> to {@link CryptoMarketDataMessageType#DAILY_BARS}. This will remove all
     * previous {@link CryptoMarketDataMessageType#QUOTES} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setDailyBarSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link CryptoMarketDataMessageType#DAILY_BARS}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getDailyBarSubscriptions();

    /**
     * Subscribes the given <code>symbols</code> to {@link CryptoMarketDataMessageType#UPDATED_BARS}. This will remove
     * all previous {@link CryptoMarketDataMessageType#QUOTES} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setUpdatedBarSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link CryptoMarketDataMessageType#UPDATED_BARS}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getUpdatedBarSubscriptions();

    /**
     * Subscribes the given <code>symbols</code> to {@link CryptoMarketDataMessageType#ORDER_BOOKS}. This will remove
     * all previous {@link CryptoMarketDataMessageType#QUOTES} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setOrderBookSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link CryptoMarketDataMessageType#ORDER_BOOKS}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getOrderBookSubscriptions();
}
