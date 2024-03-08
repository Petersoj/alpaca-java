package net.jacobpeterson.alpaca.websocket.marketdata.streams.news;

import com.google.gson.JsonObject;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.news.model.NewsMarketDataMessageType;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.news.model.control.NewsSubscriptionsMessage;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.news.model.news.NewsMessage;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataWebsocket;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.util.Set;

import static net.jacobpeterson.alpaca.model.websocket.marketdata.streams.news.model.NewsMarketDataMessageType.ERROR;
import static net.jacobpeterson.alpaca.model.websocket.marketdata.streams.news.model.NewsMarketDataMessageType.SUBSCRIPTION;
import static net.jacobpeterson.alpaca.model.websocket.marketdata.streams.news.model.NewsMarketDataMessageType.SUCCESS;
import static net.jacobpeterson.alpaca.openapi.marketdata.JSON.getGson;

/**
 * {@link NewsMarketDataWebsocket} is an implementation for {@link NewsMarketDataWebsocketInterface}.
 */
public class NewsMarketDataWebsocket
        extends MarketDataWebsocket<NewsMarketDataMessageType, NewsSubscriptionsMessage, NewsMarketDataListener>
        implements NewsMarketDataWebsocketInterface {

    /**
     * Instantiates a new {@link NewsMarketDataWebsocket}.
     *
     * @param okHttpClient    the {@link OkHttpClient}
     * @param traderKeyID     the trader key ID
     * @param traderSecretKey the trader secret key
     * @param brokerAPIKey    the broker API key
     * @param brokerAPISecret the broker API secret
     */
    public NewsMarketDataWebsocket(OkHttpClient okHttpClient, String traderKeyID, String traderSecretKey,
            String brokerAPIKey, String brokerAPISecret) {
        super(okHttpClient, new HttpUrl.Builder()
                        .scheme("https")
                        .host("stream.data.alpaca.markets")
                        .addPathSegments("v1beta1/news")
                        .build(),
                "News", traderKeyID, traderSecretKey, brokerAPIKey, brokerAPISecret,
                NewsMarketDataMessageType.class, NewsSubscriptionsMessage.class);
    }

    @Override
    protected boolean isSuccessMessageType(NewsMarketDataMessageType messageType) {
        return messageType == SUCCESS;
    }

    @Override
    protected boolean isErrorMessageType(NewsMarketDataMessageType messageType) {
        return messageType == ERROR;
    }

    @Override
    protected boolean isSubscriptionMessageType(NewsMarketDataMessageType messageType) {
        return messageType == SUBSCRIPTION;
    }

    @Override
    protected void callListenerWithMessage(NewsMarketDataMessageType messageType, JsonObject messageObject) {
        if (messageType == NewsMarketDataMessageType.NEWS) {
            listener.onNews(getGson().fromJson(messageObject, NewsMessage.class));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setListener(NewsMarketDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void setNewsSubscriptions(Set<String> symbols) {
        symbols = symbols == null ? Set.of() : symbols;
        setSubscriptions(getNewsSubscriptions(), symbols,
                set -> new NewsSubscriptionsMessage().withNews(set));
    }

    @Override
    public Set<String> getNewsSubscriptions() {
        return subscriptionsMessage == null ? Set.of() : subscriptionsMessage.getNews();
    }
}
