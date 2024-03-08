package net.jacobpeterson.alpaca.websocket.marketdata.streams.crypto;

import com.google.gson.JsonObject;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.CryptoMarketDataMessageType;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.bar.CryptoBarMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.control.CryptoSubscriptionsMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.orderbook.CryptoOrderBookMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.quote.CryptoQuoteMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.trade.CryptoTradeMessage;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataWebsocket;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.util.Set;

import static net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.CryptoMarketDataMessageType.ERROR;
import static net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.CryptoMarketDataMessageType.SUBSCRIPTION;
import static net.jacobpeterson.alpaca.model.websocket.marketdata.streams.crypto.model.CryptoMarketDataMessageType.SUCCESS;
import static net.jacobpeterson.alpaca.openapi.marketdata.JSON.getGson;

/**
 * {@link CryptoMarketDataWebsocket} is an implementation for {@link CryptoMarketDataWebsocketInterface}.
 */
public class CryptoMarketDataWebsocket
        extends MarketDataWebsocket<CryptoMarketDataMessageType, CryptoSubscriptionsMessage, CryptoMarketDataListener>
        implements CryptoMarketDataWebsocketInterface {

    /**
     * Instantiates a new {@link CryptoMarketDataWebsocket}.
     *
     * @param okHttpClient    the {@link OkHttpClient}
     * @param traderKeyID     the trader key ID
     * @param traderSecretKey the trader secret key
     * @param brokerAPIKey    the broker API key
     * @param brokerAPISecret the broker API secret
     */
    public CryptoMarketDataWebsocket(OkHttpClient okHttpClient, String traderKeyID, String traderSecretKey,
            String brokerAPIKey, String brokerAPISecret) {
        super(okHttpClient, new HttpUrl.Builder()
                        .scheme("https")
                        .host("stream.data.alpaca.markets")
                        .addPathSegments("v1beta3/crypto/us")
                        .build(),
                "Crypto", traderKeyID, traderSecretKey, brokerAPIKey, brokerAPISecret,
                CryptoMarketDataMessageType.class, CryptoSubscriptionsMessage.class);
    }

    @Override
    protected boolean isSuccessMessageType(CryptoMarketDataMessageType messageType) {
        return messageType == SUCCESS;
    }

    @Override
    protected boolean isErrorMessageType(CryptoMarketDataMessageType messageType) {
        return messageType == ERROR;
    }

    @Override
    protected boolean isSubscriptionMessageType(CryptoMarketDataMessageType messageType) {
        return messageType == SUBSCRIPTION;
    }

    @Override
    protected void callListenerWithMessage(CryptoMarketDataMessageType messageType, JsonObject messageObject) {
        switch (messageType) {
            case TRADES:
                listener.onTrade(getGson().fromJson(messageObject, CryptoTradeMessage.class));
                break;
            case QUOTES:
                listener.onQuote(getGson().fromJson(messageObject, CryptoQuoteMessage.class));
                break;
            case MINUTE_BARS:
                listener.onMinuteBar(getGson().fromJson(messageObject, CryptoBarMessage.class));
                break;
            case DAILY_BARS:
                listener.onDailyBar(getGson().fromJson(messageObject, CryptoBarMessage.class));
                break;
            case UPDATED_BARS:
                listener.onUpdatedBar(getGson().fromJson(messageObject, CryptoBarMessage.class));
                break;
            case ORDER_BOOKS:
                listener.onOrderBook(getGson().fromJson(messageObject, CryptoOrderBookMessage.class));
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setListener(CryptoMarketDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void setTradeSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getTradeSubscriptions(), symbols,
                set -> newEmptyCryptoSubscriptionsMessage().withTrades(set));
    }

    @Override
    public Set<String> getTradeSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getTrades();
    }

    @Override
    public void setQuoteSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getQuoteSubscriptions(), symbols,
                set -> newEmptyCryptoSubscriptionsMessage().withQuotes(set));
    }

    @Override
    public Set<String> getQuoteSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getQuotes();
    }

    @Override
    public void setMinuteBarSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getMinuteBarSubscriptions(), symbols,
                set -> newEmptyCryptoSubscriptionsMessage().withMinuteBars(set));
    }

    @Override
    public Set<String> getMinuteBarSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getMinuteBars();
    }

    @Override
    public void setDailyBarSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getDailyBarSubscriptions(), symbols,
                set -> newEmptyCryptoSubscriptionsMessage().withDailyBars(set));
    }

    @Override
    public Set<String> getDailyBarSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getDailyBars();
    }

    @Override
    public void setUpdatedBarSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getUpdatedBarSubscriptions(), symbols,
                set -> newEmptyCryptoSubscriptionsMessage().withUpdatedBars(set));
    }

    @Override
    public Set<String> getUpdatedBarSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getUpdatedBars();
    }

    @Override
    public void setOrderBookSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getOrderBookSubscriptions(), symbols,
                set -> newEmptyCryptoSubscriptionsMessage().withOrderBooks(set));
    }

    @Override
    public Set<String> getOrderBookSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getOrderBooks();
    }

    private CryptoSubscriptionsMessage newEmptyCryptoSubscriptionsMessage() {
        return new CryptoSubscriptionsMessage()
                .withTrades(null)
                .withQuotes(null)
                .withMinuteBars(null)
                .withDailyBars(null)
                .withUpdatedBars(null)
                .withOrderBooks(null);
    }
}
