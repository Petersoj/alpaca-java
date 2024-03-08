package net.jacobpeterson.alpaca.websocket.marketdata.streams.crypto;

import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.bar.CryptoBarMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.orderbook.CryptoOrderBookMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.quote.CryptoQuoteMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.trade.CryptoTradeMessage;

/**
 * {@link CryptoMarketDataListenerAdapter} is an adapter for {@link CryptoMarketDataListener}.
 */
public class CryptoMarketDataListenerAdapter implements CryptoMarketDataListener {

    @Override
    public void onTrade(CryptoTradeMessage trade) {}

    @Override
    public void onQuote(CryptoQuoteMessage quote) {}

    @Override
    public void onMinuteBar(CryptoBarMessage bar) {}

    @Override
    public void onDailyBar(CryptoBarMessage bar) {}

    @Override
    public void onUpdatedBar(CryptoBarMessage bar) {}

    @Override
    public void onOrderBook(CryptoOrderBookMessage orderBook) {}
}
