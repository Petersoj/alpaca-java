package net.jacobpeterson.alpaca.websocket.marketdata.streams.crypto;

import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.bar.CryptoBarMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.orderbook.CryptoOrderBookMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.quote.CryptoQuoteMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.trade.CryptoTradeMessage;

/**
 * {@link CryptoMarketDataListener} defines a listener interface for {@link CryptoMarketDataWebsocketInterface}
 * messages.
 */
public interface CryptoMarketDataListener {

    /**
     * Called when a {@link CryptoTradeMessage} is received.
     *
     * @param trade the {@link CryptoTradeMessage}
     */
    void onTrade(CryptoTradeMessage trade);

    /**
     * Called when a {@link CryptoQuoteMessage} is received.
     *
     * @param quote the {@link CryptoQuoteMessage}
     */
    void onQuote(CryptoQuoteMessage quote);

    /**
     * Called when a {@link CryptoBarMessage} is received.
     *
     * @param bar the {@link CryptoBarMessage}
     */
    void onMinuteBar(CryptoBarMessage bar);

    /**
     * Called when a {@link CryptoBarMessage} is received.
     *
     * @param bar the {@link CryptoBarMessage}
     */
    void onDailyBar(CryptoBarMessage bar);

    /**
     * Called when a {@link CryptoBarMessage} is received.
     *
     * @param bar the {@link CryptoBarMessage}
     */
    void onUpdatedBar(CryptoBarMessage bar);

    /**
     * Called when a {@link CryptoOrderBookMessage} is received.
     *
     * @param orderBook the {@link CryptoOrderBookMessage}
     */
    void onOrderBook(CryptoOrderBookMessage orderBook);
}
