package net.jacobpeterson.alpaca.websocket.marketdata;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.realtime.MarketDataMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.model.properties.DataAPIType;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocket;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * {@link MarketDataWebsocket} is an {@link AlpacaWebsocket} implementation and provides the {@link
 * MarketDataWebsocketInterface} interface for
 * <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/real-time/">Real-time
 * Market Data</a>
 */
public class MarketDataWebsocket extends AlpacaWebsocket<MarketDataMessageType, MarketDataMessage, MarketDataListener>
        implements MarketDataWebsocketInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataWebsocket.class);
    private static final String MESSAGE_TYPE_ELEMENT_KEY = "T";
    private static final List<MarketDataMessageType> SUBSCRIBABLE_MARKET_DATA_MESSAGE_TYPES = Arrays.asList(
            MarketDataMessageType.TRADE,
            MarketDataMessageType.QUOTE,
            MarketDataMessageType.BAR);

    /**
     * Creates a {@link HttpUrl} for {@link MarketDataWebsocket} with the given <code>dataAPIType</code>.
     *
     * @param dataAPIType the {@link DataAPIType}
     *
     * @return a {@link HttpUrl}
     */
    private static HttpUrl createWebsocketURL(DataAPIType dataAPIType) {
        return new HttpUrl.Builder()
                .scheme("https") // HttpUrl.Builder doesn't recognize "wss" scheme, so "https" works fine
                .host("stream.data.alpaca.markets")
                .addPathSegment("v2")
                .addPathSegment(dataAPIType.toString())
                .build();
    }

    /**
     * Instantiates a new {@link MarketDataWebsocket}.
     *
     * @param okHttpClient the {@link OkHttpClient}
     * @param dataAPIType  the {@link DataAPIType}
     * @param keyID        the key ID
     * @param secretKey    the secret key
     * @param oAuthToken   the OAuth token
     */
    public MarketDataWebsocket(OkHttpClient okHttpClient, DataAPIType dataAPIType,
            String keyID, String secretKey, String oAuthToken) {
        super(okHttpClient, createWebsocketURL(dataAPIType), "Market Data", keyID, secretKey, oAuthToken);
    }

    @Override
    protected void onConnection() {
        // TODO
    }

    @Override
    protected void onReconnection() {
        // TODO
    }

    @Override
    protected void sendAuthenticationMessage() {
        // TODO
    }

    // This websocket uses string frames and not binary frames.
    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        // TODO
    }

    @Override
    public void subscribe(List<String> tradeSymbols, List<String> quoteSymbols, List<String> barSymbols) {
        // TODO
    }

    @Override
    public void unsubscribe(List<String> tradeSymbols, List<String> quoteSymbols, List<String> barSymbols) {
        // TODO
    }
}
