package net.jacobpeterson.alpaca.websocket.marketdata.streams.stock;

import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.bar.StockBarMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.limituplimitdownband.StockLimitUpLimitDownBandMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.quote.StockQuoteMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.trade.StockTradeMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.tradecancelerror.StockTradeCancelErrorMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.tradecorrection.StockTradeCorrectionMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.tradingstatus.StockTradingStatusMessage;

/**
 * {@link StockMarketDataListenerAdapter} is an adapter for {@link StockMarketDataListener}.
 */
public class StockMarketDataListenerAdapter implements StockMarketDataListener {

    @Override
    public void onTrade(StockTradeMessage trade) {}

    @Override
    public void onQuote(StockQuoteMessage quote) {}

    @Override
    public void onMinuteBar(StockBarMessage bar) {}

    @Override
    public void onDailyBar(StockBarMessage bar) {}

    @Override
    public void onUpdatedBar(StockBarMessage bar) {}

    @Override
    public void onTradeCorrection(StockTradeCorrectionMessage tradeCorrection) {}

    @Override
    public void onTradeCancelError(StockTradeCancelErrorMessage tradeCancelError) {}

    @Override
    public void onLimitUpLimitDownBand(StockLimitUpLimitDownBandMessage limitUpLimitDownBand) {}

    @Override
    public void onTradingStatus(StockTradingStatusMessage tradingStatus) {}
}
