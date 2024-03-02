package net.jacobpeterson.alpaca;

import net.jacobpeterson.alpaca.model.properties.DataAPIType;
import net.jacobpeterson.alpaca.model.properties.EndpointAPIType;
import net.jacobpeterson.alpaca.properties.AlpacaProperties;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataWebsocketInterface;
import net.jacobpeterson.alpaca.websocket.marketdata.crypto.CryptoMarketDataWebsocket;
import net.jacobpeterson.alpaca.websocket.marketdata.news.NewsMarketDataWebsocket;
import net.jacobpeterson.alpaca.websocket.marketdata.stock.StockMarketDataWebsocket;
import net.jacobpeterson.alpaca.websocket.streaming.StreamingWebsocket;
import net.jacobpeterson.alpaca.websocket.streaming.StreamingWebsocketInterface;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AlpacaAPI} is the main class used to interface with the various Alpaca API endpoints. You will generally only
 * need one instance of this class in your application.
 *
 * @see <a href="https://docs.alpaca.markets">Alpaca Docs</a>
 */
public class AlpacaAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaAPI.class);

    private final OkHttpClient okHttpClient;
    private final StreamingWebsocket streamingWebsocket;
    private final CryptoMarketDataWebsocket cryptoMarketDataWebsocket;
    private final StockMarketDataWebsocket stockMarketDataWebsocket;
    private final NewsMarketDataWebsocket newsMarketDataWebsocket;

    /**
     * Instantiates a new {@link AlpacaAPI} using properties specified in <code>alpaca.properties</code> file (or their
     * associated defaults).
     */
    public AlpacaAPI() {
        this(AlpacaProperties.KEY_ID,
                AlpacaProperties.SECRET_KEY,
                AlpacaProperties.ENDPOINT_API_TYPE,
                AlpacaProperties.DATA_API_TYPE);
    }

    /**
     * Instantiates a new {@link AlpacaAPI} using properties specified in the given {@link Builder}, otherwise from
     * <code>alpaca.properties</code> file (or their associated defaults).
     */
    private AlpacaAPI(Builder builder) {
        this(builder.keyID,
                builder.secretKey,
                builder.endpointAPIType,
                builder.dataAPIType);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}.
     *
     * @param keyID     the key ID
     * @param secretKey the secret key
     */
    public AlpacaAPI(String keyID, String secretKey) {
        this(null, keyID, secretKey, null,
                AlpacaProperties.ENDPOINT_API_TYPE,
                AlpacaProperties.DATA_API_TYPE);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}.
     *
     * @param keyID           the key ID
     * @param secretKey       the secret key
     * @param endpointAPIType the {@link EndpointAPIType}
     * @param dataAPIType     the {@link DataAPIType}
     */
    public AlpacaAPI(String keyID, String secretKey, EndpointAPIType endpointAPIType, DataAPIType dataAPIType) {
        this(null, keyID, secretKey, null, endpointAPIType, dataAPIType);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}.
     *
     * @param oAuthToken the OAuth token. Note that the Data API v2 does not work with OAuth tokens.
     */
    public AlpacaAPI(String oAuthToken) {
        this(null, null, null, oAuthToken,
                AlpacaProperties.ENDPOINT_API_TYPE,
                AlpacaProperties.DATA_API_TYPE);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}.
     *
     * @param okHttpClient    the {@link OkHttpClient} or <code>null</code> to create a default instance
     * @param keyID           the key ID
     * @param secretKey       the secret key
     * @param oAuthToken      the OAuth token
     * @param endpointAPIType the {@link EndpointAPIType}
     * @param dataAPIType     the {@link DataAPIType}
     */
    public AlpacaAPI(OkHttpClient okHttpClient, String keyID, String secretKey, String oAuthToken,
            EndpointAPIType endpointAPIType, DataAPIType dataAPIType) {
        checkArgument((keyID != null && secretKey != null) ^ oAuthToken != null,
                "You must specify a (KeyID (%s) and Secret Key (%s)) or an OAuthToken (%s)!",
                keyID, secretKey, oAuthToken);
        checkNotNull(endpointAPIType);
        checkNotNull(dataAPIType);

        // Create default 'okHttpClient'
        if (okHttpClient == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .cache(null); // Ensure response caching is disabled
            if (LOGGER.isDebugEnabled()) {
                clientBuilder.addInterceptor(new HttpLoggingInterceptor(LOGGER::debug));
            }
            okHttpClient = clientBuilder.build();
        }
        this.okHttpClient = okHttpClient;

        streamingWebsocket = new StreamingWebsocket(okHttpClient, endpointAPIType, keyID, secretKey, oAuthToken);
        cryptoMarketDataWebsocket = new CryptoMarketDataWebsocket(okHttpClient, keyID, secretKey);
        stockMarketDataWebsocket = new StockMarketDataWebsocket(okHttpClient, dataAPIType, keyID, secretKey);
        newsMarketDataWebsocket = new NewsMarketDataWebsocket(okHttpClient, keyID, secretKey);
    }

    /**
     * @return the {@link StreamingWebsocketInterface}
     */
    public StreamingWebsocketInterface streaming() {
        return streamingWebsocket;
    }

    /**
     * @return the Crypto {@link MarketDataWebsocketInterface}
     */
    public MarketDataWebsocketInterface cryptoMarketDataStreaming() {
        return cryptoMarketDataWebsocket;
    }

    /**
     * @return the Stock {@link MarketDataWebsocketInterface}
     */
    public MarketDataWebsocketInterface stockMarketDataStreaming() {
        return stockMarketDataWebsocket;
    }

    /**
     * @return the News {@link MarketDataWebsocketInterface}
     */
    public MarketDataWebsocketInterface newsMarketDataStreaming() {
        return newsMarketDataWebsocket;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * Creates a {@link Builder} for {@link AlpacaAPI}.
     *
     * @return the {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder for {@link AlpacaAPI}
     */
    public static final class Builder {

        private String keyID;
        private String secretKey;
        private EndpointAPIType endpointAPIType;
        private DataAPIType dataAPIType;

        private Builder() {
            this.keyID = AlpacaProperties.KEY_ID;
            this.secretKey = AlpacaProperties.SECRET_KEY;
            this.endpointAPIType = AlpacaProperties.ENDPOINT_API_TYPE;
            this.dataAPIType = AlpacaProperties.DATA_API_TYPE;
        }

        public Builder withKeyID(String keyID) {
            this.keyID = keyID;
            return this;
        }

        public Builder withSecretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder withEndpointAPIType(EndpointAPIType endpointAPIType) {
            this.endpointAPIType = endpointAPIType;
            return this;
        }

        public Builder withDataAPIType(DataAPIType dataAPIType) {
            this.dataAPIType = dataAPIType;
            return this;
        }

        public AlpacaAPI build() {
            return new AlpacaAPI(this);
        }
    }
}
