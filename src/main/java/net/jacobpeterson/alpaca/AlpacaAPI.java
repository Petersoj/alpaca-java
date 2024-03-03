package net.jacobpeterson.alpaca;

import net.jacobpeterson.alpaca.model.util.apitype.BrokerAPIEndpointType;
import net.jacobpeterson.alpaca.model.util.apitype.MarketDataAPIStreamSourceType;
import net.jacobpeterson.alpaca.model.util.apitype.TraderAPIEndpointType;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.jacobpeterson.alpaca.model.util.apitype.BrokerAPIEndpointType.SANDBOX;
import static net.jacobpeterson.alpaca.model.util.apitype.MarketDataAPIStreamSourceType.IEX;
import static net.jacobpeterson.alpaca.model.util.apitype.TraderAPIEndpointType.PAPER;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

/**
 * {@link AlpacaAPI} is the main class used to interface with the various Alpaca API endpoints. If you are using the
 * Trading or Market Data APIs for a single Alpaca account or if you are using the Broker API, you will generally only
 * need one instance of this class. However, if you are using the Trading API with OAuth to act on behalf of an Alpaca
 * account, this class is optimized so that it can be instantiated quickly, especially when an existing
 * {@link OkHttpClient} is given in the constructor. Additionally, all API endpoint instances are instantiated lazily.
 * This class is thread-safe.
 *
 * @see <a href="https://docs.alpaca.markets">Alpaca Docs</a>
 */
public class AlpacaAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaAPI.class);

    private final String traderKeyID;
    private final String traderSecretKey;
    private final String traderOAuthToken;
    private final TraderAPIEndpointType traderAPIEndpointType;
    private final MarketDataAPIStreamSourceType marketDataAPIStreamSourceType;
    private final String brokerAPIKey;
    private final String brokerAPISecret;
    private final BrokerAPIEndpointType brokerAPIEndpointType;
    private final OkHttpClient okHttpClient;

    private AlpacaTraderAPI trader;
    private AlpacaMarketDataAPI marketData;
    private AlpacaBrokerAPI broker;

    /**
     * Instantiates a new {@link AlpacaAPI}. Use this constructor if you are using the Trading or Market Data APIs for a
     * single Alpaca account.
     *
     * @param traderKeyID                   the Trader key ID
     * @param traderSecretKey               the Trader secret key
     * @param traderAPIEndpointType         the {@link TraderAPIEndpointType}
     * @param marketDataAPIStreamSourceType the {@link MarketDataAPIStreamSourceType}
     */
    public AlpacaAPI(String traderKeyID, String traderSecretKey, TraderAPIEndpointType traderAPIEndpointType,
            MarketDataAPIStreamSourceType marketDataAPIStreamSourceType) {
        this(traderKeyID, traderSecretKey, null, traderAPIEndpointType, marketDataAPIStreamSourceType, null, null, null,
                null);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}. Use this constructor if you are using the Trading or Market Data APIs for a
     * single Alpaca account and a custom {@link OkHttpClient} instance.
     *
     * @param traderKeyID                   the Trader key ID
     * @param traderSecretKey               the Trader secret key
     * @param traderAPIEndpointType         the {@link TraderAPIEndpointType}
     * @param marketDataAPIStreamSourceType the {@link MarketDataAPIStreamSourceType}
     * @param okHttpClient                  an existing {@link OkHttpClient} or <code>null</code> to create a new
     *                                      default instance
     */
    public AlpacaAPI(String traderKeyID, String traderSecretKey, TraderAPIEndpointType traderAPIEndpointType,
            MarketDataAPIStreamSourceType marketDataAPIStreamSourceType, OkHttpClient okHttpClient) {
        this(traderKeyID, traderSecretKey, null, traderAPIEndpointType, marketDataAPIStreamSourceType, null, null, null,
                okHttpClient);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}. Use this constructor if you are using the Trading API with OAuth to act on
     * behalf of an Alpaca account.
     *
     * @param traderOAuthToken      the Trader OAuth token
     * @param traderAPIEndpointType the {@link TraderAPIEndpointType}
     */
    public AlpacaAPI(String traderOAuthToken, TraderAPIEndpointType traderAPIEndpointType) {
        this(null, null, traderOAuthToken, traderAPIEndpointType, null, null, null, null, null);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}. Use this constructor if you are using the Trading API with OAuth to act on
     * behalf of an Alpaca account and a custom {@link OkHttpClient} instance.
     *
     * @param traderOAuthToken      the Trader OAuth token
     * @param traderAPIEndpointType the {@link TraderAPIEndpointType}
     * @param okHttpClient          an existing {@link OkHttpClient} or <code>null</code> to create a new default
     *                              instance
     */
    public AlpacaAPI(String traderOAuthToken, TraderAPIEndpointType traderAPIEndpointType, OkHttpClient okHttpClient) {
        this(null, null, traderOAuthToken, traderAPIEndpointType, null, null, null, null, okHttpClient);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}. Use this constructor if you are using the Broker API.
     *
     * @param brokerAPIKey          the Broker API key
     * @param brokerAPISecret       the Broker API secret
     * @param brokerAPIEndpointType the {@link BrokerAPIEndpointType}
     */
    public AlpacaAPI(String brokerAPIKey, String brokerAPISecret, BrokerAPIEndpointType brokerAPIEndpointType) {
        this(null, null, null, null, null, brokerAPIKey, brokerAPISecret, brokerAPIEndpointType, null);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}. Use this constructor if you are using the Broker API and a custom
     * {@link OkHttpClient} instance.
     *
     * @param brokerAPIKey          the Broker API key
     * @param brokerAPISecret       the Broker API secret
     * @param brokerAPIEndpointType the {@link BrokerAPIEndpointType}
     * @param okHttpClient          an existing {@link OkHttpClient} or <code>null</code> to create a new default
     *                              instance
     */
    public AlpacaAPI(String brokerAPIKey, String brokerAPISecret, BrokerAPIEndpointType brokerAPIEndpointType,
            OkHttpClient okHttpClient) {
        this(null, null, null, null, null, brokerAPIKey, brokerAPISecret, brokerAPIEndpointType, okHttpClient);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}.
     *
     * @param traderKeyID                   the Trader key ID
     * @param traderSecretKey               the Trader secret key
     * @param traderOAuthToken              the Trader OAuth token
     * @param traderAPIEndpointType         the {@link TraderAPIEndpointType}
     * @param marketDataAPIStreamSourceType the {@link MarketDataAPIStreamSourceType}
     * @param brokerAPIKey                  the Broker API key
     * @param brokerAPISecret               the Broker API secret
     * @param brokerAPIEndpointType         the {@link BrokerAPIEndpointType}
     * @param okHttpClient                  an existing {@link OkHttpClient} or <code>null</code> to create a new
     *                                      default instance
     */
    public AlpacaAPI(String traderKeyID, String traderSecretKey,
            String traderOAuthToken, TraderAPIEndpointType traderAPIEndpointType,
            MarketDataAPIStreamSourceType marketDataAPIStreamSourceType,
            String brokerAPIKey, String brokerAPISecret, BrokerAPIEndpointType brokerAPIEndpointType,
            OkHttpClient okHttpClient) {
        this.traderKeyID = traderKeyID;
        this.traderSecretKey = traderSecretKey;
        this.traderOAuthToken = traderOAuthToken;
        this.traderAPIEndpointType = traderAPIEndpointType != null ? traderAPIEndpointType : PAPER;
        this.marketDataAPIStreamSourceType = marketDataAPIStreamSourceType != null ?
                marketDataAPIStreamSourceType : IEX;
        this.brokerAPIKey = brokerAPIKey;
        this.brokerAPISecret = brokerAPISecret;
        this.brokerAPIEndpointType = brokerAPIEndpointType != null ? brokerAPIEndpointType : SANDBOX;

        // Create default OkHttpClient instance
        if (okHttpClient == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            if (LOGGER.isDebugEnabled()) {
                final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(LOGGER::debug);
                loggingInterceptor.setLevel(BODY);
                clientBuilder.addInterceptor(loggingInterceptor);
            }
            okHttpClient = clientBuilder.build();
        }
        this.okHttpClient = okHttpClient;
    }

    /**
     * Closes the {@link OkHttpClient}.
     */
    public void closeOkHttpClient() {
        okHttpClient.dispatcher().executorService().shutdown();
        okHttpClient.connectionPool().evictAll();
    }

    /**
     * Gets the {@link OkHttpClient}.
     *
     * @return the {@link OkHttpClient}
     */
    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * Gets the {@link AlpacaTraderAPI}. Lazily instantiated.
     *
     * @return the {@link AlpacaTraderAPI}
     */
    public synchronized AlpacaTraderAPI trader() {
        if (trader == null) {
            trader = new AlpacaTraderAPI(traderKeyID, traderSecretKey, traderOAuthToken, traderAPIEndpointType,
                    okHttpClient);
        }
        return trader;
    }

    /**
     * Gets the {@link AlpacaMarketDataAPI}. Lazily instantiated.
     *
     * @return the {@link AlpacaMarketDataAPI}
     */
    public synchronized AlpacaMarketDataAPI marketData() {
        if (marketData == null) {
            marketData = new AlpacaMarketDataAPI(traderKeyID, traderSecretKey, brokerAPIKey, brokerAPISecret,
                    okHttpClient);
        }
        return marketData;
    }

    /**
     * Gets the {@link AlpacaBrokerAPI}. Lazily instantiated.
     *
     * @return the {@link AlpacaBrokerAPI}
     */
    public synchronized AlpacaBrokerAPI broker() {
        if (broker == null) {
            broker = new AlpacaBrokerAPI(brokerAPIKey, brokerAPISecret, brokerAPIEndpointType, okHttpClient);
        }
        return broker;
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

        private String traderKeyID;
        private String traderSecretKey;
        private String traderOAuthToken;
        private TraderAPIEndpointType traderAPIEndpointType;
        private MarketDataAPIStreamSourceType marketDataAPISourceType;
        private String brokerAPIKey;
        private String brokerAPISecret;
        private BrokerAPIEndpointType brokerAPIEndpointType;
        private OkHttpClient okHttpClient;

        private Builder() {}

        public Builder withTraderKeyID(String traderKeyID) {
            this.traderKeyID = traderKeyID;
            return this;
        }

        public Builder withTraderSecretKey(String traderSecretKey) {
            this.traderSecretKey = traderSecretKey;
            return this;
        }

        public Builder withTraderOAuthToken(String traderOAuthToken) {
            this.traderOAuthToken = traderOAuthToken;
            return this;
        }

        public Builder withTraderAPIEndpointType(TraderAPIEndpointType traderAPIEndpointType) {
            this.traderAPIEndpointType = traderAPIEndpointType;
            return this;
        }

        public Builder withMarketDataAPIStreamSourceType(MarketDataAPIStreamSourceType marketDataAPISourceType) {
            this.marketDataAPISourceType = marketDataAPISourceType;
            return this;
        }

        public Builder withBrokerAPIKey(String brokerAPIKey) {
            this.brokerAPIKey = brokerAPIKey;
            return this;
        }

        public Builder withBrokerAPISecret(String brokerAPISecret) {
            this.brokerAPISecret = brokerAPISecret;
            return this;
        }

        public Builder withBrokerAPIEndpointType(BrokerAPIEndpointType brokerAPIEndpointType) {
            this.brokerAPIEndpointType = brokerAPIEndpointType;
            return this;
        }

        public Builder withOkHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public AlpacaAPI build() {
            return new AlpacaAPI(traderKeyID, traderSecretKey, traderOAuthToken, traderAPIEndpointType,
                    marketDataAPISourceType, brokerAPIKey, brokerAPISecret, brokerAPIEndpointType, okHttpClient);
        }
    }
}
