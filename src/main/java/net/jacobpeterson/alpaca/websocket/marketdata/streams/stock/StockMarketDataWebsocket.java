package net.jacobpeterson.alpaca.websocket.marketdata.streams.stock;

import com.google.gson.JsonObject;
import net.jacobpeterson.alpaca.model.util.apitype.MarketDataWebsocketSourceType;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.StockMarketDataMessageType;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.bar.StockBarMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.control.StockSubscriptionsMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.limituplimitdownband.StockLimitUpLimitDownBandMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.quote.StockQuoteMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.trade.StockTradeMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.tradecancelerror.StockTradeCancelErrorMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.tradecorrection.StockTradeCorrectionMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.tradingstatus.StockTradingStatusMessage;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataWebsocket;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.util.Set;

import static net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.StockMarketDataMessageType.ERROR;
import static net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.StockMarketDataMessageType.SUBSCRIPTION;
import static net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.StockMarketDataMessageType.SUCCESS;
import static net.jacobpeterson.alpaca.openapi.marketdata.JSON.getGson;

/**
 * {@link StockMarketDataWebsocket} is an implementation for {@link StockMarketDataWebsocketInterface}.
 */
public class StockMarketDataWebsocket
        extends MarketDataWebsocket<StockMarketDataMessageType, StockSubscriptionsMessage, StockMarketDataListener>
        implements StockMarketDataWebsocketInterface {

    private static HttpUrl createWebsocketURL(boolean isSandbox,
            MarketDataWebsocketSourceType marketDataWebsocketSourceType) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(isSandbox ? "stream.data.sandbox.alpaca.markets" : "stream.data.alpaca.markets")
                .addPathSegment("v2")
                .addPathSegment(marketDataWebsocketSourceType.toString())
                .build();
    }

    /**
     * Instantiates a new {@link StockMarketDataWebsocket}.
     *
     * @param okHttpClient                  the {@link OkHttpClient}
     * @param traderKeyID                   the trader key ID
     * @param traderSecretKey               the trader secret key
     * @param brokerAPIKey                  the broker API key
     * @param brokerAPISecret               the broker API secret
     * @param marketDataWebsocketSourceType the {@link MarketDataWebsocketSourceType}
     */
    public StockMarketDataWebsocket(OkHttpClient okHttpClient, String traderKeyID, String traderSecretKey,
            String brokerAPIKey, String brokerAPISecret, MarketDataWebsocketSourceType marketDataWebsocketSourceType) {
        super(okHttpClient,
                createWebsocketURL(brokerAPIKey != null && brokerAPISecret != null, marketDataWebsocketSourceType),
                "Stock", traderKeyID, traderSecretKey, brokerAPIKey, brokerAPISecret,
                StockMarketDataMessageType.class, StockSubscriptionsMessage.class);
    }

    @Override
    protected boolean isSuccessMessageType(StockMarketDataMessageType messageType) {
        return messageType == SUCCESS;
    }

    @Override
    protected boolean isErrorMessageType(StockMarketDataMessageType messageType) {
        return messageType == ERROR;
    }

    @Override
    protected boolean isSubscriptionMessageType(StockMarketDataMessageType messageType) {
        return messageType == SUBSCRIPTION;
    }

    @Override
    protected void callListenerWithMessage(StockMarketDataMessageType messageType, JsonObject messageObject) {
        switch (messageType) {
            case TRADES:
                listener.onTrade(getGson().fromJson(messageObject,
                        StockTradeMessage.class));
                break;
            case QUOTES:
                listener.onQuote(getGson().fromJson(messageObject,
                        StockQuoteMessage.class));
                break;
            case MINUTE_BARS:
                listener.onMinuteBar(getGson().fromJson(messageObject,
                        StockBarMessage.class));
                break;
            case DAILY_BARS:
                listener.onDailyBar(getGson().fromJson(messageObject,
                        StockBarMessage.class));
                break;
            case UPDATED_BARS:
                listener.onUpdatedBar(getGson().fromJson(messageObject,
                        StockBarMessage.class));
                break;
            case TRADE_CORRECTIONS:
                listener.onTradeCorrection(getGson().fromJson(messageObject,
                        StockTradeCorrectionMessage.class));
                break;
            case TRADE_CANCEL_ERRORS:
                listener.onTradeCancelError(getGson().fromJson(messageObject,
                        StockTradeCancelErrorMessage.class));
                break;
            case LIMIT_UP_LIMIT_DOWN_BANDS:
                listener.onLimitUpLimitDownBand(getGson().fromJson(messageObject,
                        StockLimitUpLimitDownBandMessage.class));
                break;
            case TRADING_STATUSES:
                listener.onTradingStatus(getGson().fromJson(messageObject,
                        StockTradingStatusMessage.class));
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setListener(StockMarketDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void setTradeSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getTradeSubscriptions(), symbols,
                set -> newEmptyStockSubscriptionsMessage().withTrades(set));
    }

    @Override
    public Set<String> getTradeSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getTrades();
    }

    @Override
    public void setQuoteSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getQuoteSubscriptions(), symbols,
                set -> newEmptyStockSubscriptionsMessage().withQuotes(set));
    }

    @Override
    public Set<String> getQuoteSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getQuotes();
    }

    @Override
    public void setMinuteBarSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getMinuteBarSubscriptions(), symbols,
                set -> newEmptyStockSubscriptionsMessage().withMinuteBars(set));
    }

    @Override
    public Set<String> getMinuteBarSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getMinuteBars();
    }

    @Override
    public void setDailyBarSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getDailyBarSubscriptions(), symbols,
                set -> newEmptyStockSubscriptionsMessage().withDailyBars(set));
    }

    @Override
    public Set<String> getDailyBarSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getDailyBars();
    }

    @Override
    public void setUpdatedBarSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getUpdatedBarSubscriptions(), symbols,
                set -> newEmptyStockSubscriptionsMessage().withUpdatedBars(set));
    }

    @Override
    public Set<String> getUpdatedBarSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getUpdatedBars();
    }

    @Override
    public void setLimitUpLimitDownBandSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getLimitUpLimitDownBandSubscriptions(), symbols,
                set -> newEmptyStockSubscriptionsMessage().withLimitUpLimitDownBands(set));
    }

    @Override
    public Set<String> getLimitUpLimitDownBandSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getLimitUpLimitDownBands();
    }

    @Override
    public void setTradingStatuseSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getTradingStatuseSubscriptions(), symbols,
                set -> newEmptyStockSubscriptionsMessage().withTradingStatuses(set));
    }

    @Override
    public Set<String> getTradingStatuseSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getTradingStatuses();
    }

    private StockSubscriptionsMessage newEmptyStockSubscriptionsMessage() {
        return new StockSubscriptionsMessage()
                .withTrades(null)
                .withQuotes(null)
                .withMinuteBars(null)
                .withDailyBars(null)
                .withUpdatedBars(null)
                .withLimitUpLimitDownBands(null)
                .withTradingStatuses(null);
    }
}
