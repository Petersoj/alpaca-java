package net.jacobpeterson.alpaca.websocket.marketdata.streams.stock;

import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.StockMarketDataMessageType;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataWebsocketInterface;

import java.util.Set;

/**
 * {@link StockMarketDataWebsocketInterface} is a {@link MarketDataWebsocketInterface} for
 * {@link StockMarketDataWebsocket}.
 */
public interface StockMarketDataWebsocketInterface extends MarketDataWebsocketInterface {

    /**
     * Sets the {@link StockMarketDataListener}.
     *
     * @param listener the {@link StockMarketDataListener}
     */
    void setListener(StockMarketDataListener listener);

    /**
     * Subscribes the given <code>symbols</code> to {@link StockMarketDataMessageType#TRADES}. This will remove all
     * previous {@link StockMarketDataMessageType#TRADES} subscriptions.
     * <br>
     * Note that this will automatically apply the same <code>symbols</code> subscription list to the
     * {@link StockMarketDataMessageType#TRADE_CORRECTIONS} and {@link StockMarketDataMessageType#TRADE_CANCEL_ERRORS}
     * types.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setTradeSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link StockMarketDataMessageType#TRADES}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getTradeSubscriptions();

    /**
     * Subscribes the given <code>symbols</code> to {@link StockMarketDataMessageType#QUOTES}. This will remove all
     * previous {@link StockMarketDataMessageType#QUOTES} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setQuoteSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link StockMarketDataMessageType#QUOTES}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getQuoteSubscriptions();

    /**
     * Subscribes the given <code>symbols</code> to {@link StockMarketDataMessageType#MINUTE_BARS}. This will remove all
     * previous {@link StockMarketDataMessageType#MINUTE_BARS} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setMinuteBarSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link StockMarketDataMessageType#MINUTE_BARS}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getMinuteBarSubscriptions();

    /**
     * Subscribes the given <code>symbols</code> to {@link StockMarketDataMessageType#DAILY_BARS}. This will remove all
     * previous {@link StockMarketDataMessageType#DAILY_BARS} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setDailyBarSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link StockMarketDataMessageType#DAILY_BARS}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getDailyBarSubscriptions();

    /**
     * Subscribes the given <code>symbols</code> to {@link StockMarketDataMessageType#UPDATED_BARS}. This will remove
     * all previous {@link StockMarketDataMessageType#UPDATED_BARS} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setUpdatedBarSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link StockMarketDataMessageType#UPDATED_BARS}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getUpdatedBarSubscriptions();

    /**
     * Subscribes the given <code>symbols</code> to {@link StockMarketDataMessageType#LIMIT_UP_LIMIT_DOWN_BANDS}. This
     * will remove all previous {@link StockMarketDataMessageType#LIMIT_UP_LIMIT_DOWN_BANDS} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setLimitUpLimitDownBandSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link StockMarketDataMessageType#LIMIT_UP_LIMIT_DOWN_BANDS}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getLimitUpLimitDownBandSubscriptions();

    /**
     * Subscribes the given <code>symbols</code> to {@link StockMarketDataMessageType#TRADING_STATUSES}. This will
     * remove all previous {@link StockMarketDataMessageType#TRADING_STATUSES} subscriptions.
     *
     * @param symbols the {@link Set} of symbols (use an asterisk ("*") as a wildcard to subscribe to all available
     *                symbols) or <code>null</code> to unsubscribe from all symbols
     */
    void setTradingStatuseSubscriptions(Set<String> symbols);

    /**
     * Gets the current symbols subscribed to {@link StockMarketDataMessageType#TRADING_STATUSES}.
     *
     * @return a {@link Set} of {@link String} symbols
     */
    Set<String> getTradingStatuseSubscriptions();
}
