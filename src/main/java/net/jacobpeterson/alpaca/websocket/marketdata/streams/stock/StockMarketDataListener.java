package net.jacobpeterson.alpaca.websocket.marketdata.streams.stock;

import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.bar.StockBarMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.limituplimitdownband.StockLimitUpLimitDownBandMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.quote.StockQuoteMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.trade.StockTradeMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.tradecancelerror.StockTradeCancelErrorMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.tradecorrection.StockTradeCorrectionMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.tradingstatus.StockTradingStatusMessage;

/**
 * {@link StockMarketDataListener} defines a listener interface for {@link StockMarketDataWebsocketInterface} messages.
 */
public interface StockMarketDataListener {

    /**
     * Called when a {@link StockTradeMessage} is received.
     *
     * @param trade the {@link StockTradeMessage}
     */
    void onTrade(StockTradeMessage trade);

    /**
     * Called when a {@link StockQuoteMessage} is received.
     *
     * @param quote the {@link StockQuoteMessage}
     */
    void onQuote(StockQuoteMessage quote);

    /**
     * Called when a {@link StockBarMessage} is received.
     *
     * @param bar the {@link StockBarMessage}
     */
    void onMinuteBar(StockBarMessage bar);

    /**
     * Called when a {@link StockBarMessage} is received.
     *
     * @param bar the {@link StockBarMessage}
     */
    void onDailyBar(StockBarMessage bar);

    /**
     * Called when a {@link StockBarMessage} is received.
     *
     * @param bar the {@link StockBarMessage}
     */
    void onUpdatedBar(StockBarMessage bar);

    /**
     * Called when a {@link StockTradeCorrectionMessage} is received.
     *
     * @param tradeCorrection the {@link StockTradeCorrectionMessage}
     */
    void onTradeCorrection(StockTradeCorrectionMessage tradeCorrection);

    /**
     * Called when a {@link StockTradeCancelErrorMessage} is received.
     *
     * @param tradeCancelError the {@link StockTradeCancelErrorMessage}
     */
    void onTradeCancelError(StockTradeCancelErrorMessage tradeCancelError);

    /**
     * Called when a {@link StockLimitUpLimitDownBandMessage} is received.
     *
     * @param limitUpLimitDownBand the {@link StockLimitUpLimitDownBandMessage}
     */
    void onLimitUpLimitDownBand(StockLimitUpLimitDownBandMessage limitUpLimitDownBand);

    /**
     * Called when a {link StockTradingStatusMessage} is received.
     *
     * @param tradingStatus the {@link StockTradingStatusMessage}
     */
    void onTradingStatus(StockTradingStatusMessage tradingStatus);
}
