package net.jacobpeterson.alpaca;

import devcsrj.okhttp3.logging.HttpLoggingInterceptor;
import net.jacobpeterson.alpaca.model.properties.DataAPIType;
import net.jacobpeterson.alpaca.model.properties.EndpointAPIType;
import net.jacobpeterson.alpaca.properties.AlpacaProperties;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.endpoint.*;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AlpacaAPI} contains several instances of various {@link AbstractEndpoint}s that enable you to interface with
 * Alpaca. You will generally only need one instance of this class in your application. Note that many methods inside
 * the various {@link AbstractEndpoint}s allow <code>null<code/> to be passed in as a parameter if it is optional.
 *
 * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/">Alpaca API Documentation</a>
 */
public class AlpacaAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaAPI.class);

    private static final String VERSION_2_PATH_SEGMENT = "v2";

    private final OkHttpClient okHttpClient;
    private final AlpacaClient brokerClient;
    private final AlpacaClient dataClient;
    // Ordering of fields/methods below are analogous to the ordering in the Alpaca documentation
    private final AccountEndpoint accountEndpoint;
    private final MarketDataEndpoint marketDataEndpoint;
    private final OrdersEndpoint ordersEndpoint;
    private final PositionsEndpoint positionsEndpoint;
    private final AssetsEndpoint assetsEndpoint;
    private final WatchlistEndpoint watchlistEndpoint;
    private final CalendarEndpoint calendarEndpoint;
    private final ClockEndpoint clockEndpoint;
    private final AccountConfigurationEndpoint accountConfigurationEndpoint;
    private final AccountActivitiesEndpoint accountActivitiesEndpoint;
    private final PortfolioHistoryEndpoint portfolioHistoryEndpoint;

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
     * Instantiates a new {@link AlpacaAPI}.
     *
     * @param keyID  the key ID
     * @param secret the secret
     */
    public AlpacaAPI(String keyID, String secret) {
        this(null, keyID, secret, null,
                AlpacaProperties.ENDPOINT_API_TYPE,
                AlpacaProperties.DATA_API_TYPE);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}.
     *
     * @param keyID           the key ID
     * @param secret          the secret
     * @param endpointAPIType the {@link EndpointAPIType}
     * @param dataAPIType     the {@link DataAPIType}
     */
    public AlpacaAPI(String keyID, String secret, EndpointAPIType endpointAPIType, DataAPIType dataAPIType) {
        this(null, keyID, secret, null, endpointAPIType, dataAPIType);
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
        checkNotNull(endpointAPIType);
        checkNotNull(dataAPIType);

        // Create default 'okHttpClient'
        if (okHttpClient == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .cache(null); // Ensure response caching is disabled

            if (LOGGER.isDebugEnabled()) {
                clientBuilder.addInterceptor(new HttpLoggingInterceptor(LOGGER));
            }

            okHttpClient = clientBuilder.build();
        }

        this.okHttpClient = okHttpClient;

        String brokerHostSubdomain;
        switch (endpointAPIType) {
            case LIVE:
                brokerHostSubdomain = "live";
                break;
            case PAPER:
                brokerHostSubdomain = "paper-api";
                break;
            default:
                throw new UnsupportedOperationException();
        }

        if (oAuthToken == null) {
            brokerClient = new AlpacaClient(okHttpClient, keyID, secretKey,
                    brokerHostSubdomain, VERSION_2_PATH_SEGMENT);
            dataClient = new AlpacaClient(okHttpClient, keyID, secretKey, "data", VERSION_2_PATH_SEGMENT);
        } else {
            brokerClient = new AlpacaClient(okHttpClient, oAuthToken, brokerHostSubdomain, VERSION_2_PATH_SEGMENT);
            dataClient = null;
        }

        accountEndpoint = new AccountEndpoint(brokerClient);
        marketDataEndpoint = dataClient == null ? null : new MarketDataEndpoint(dataClient);
        ordersEndpoint = new OrdersEndpoint(brokerClient);
        positionsEndpoint = new PositionsEndpoint(brokerClient);
        assetsEndpoint = new AssetsEndpoint(brokerClient);
        watchlistEndpoint = new WatchlistEndpoint(brokerClient);
        calendarEndpoint = new CalendarEndpoint(brokerClient);
        clockEndpoint = new ClockEndpoint(brokerClient);
        accountConfigurationEndpoint = new AccountConfigurationEndpoint(brokerClient);
        accountActivitiesEndpoint = new AccountActivitiesEndpoint(brokerClient);
        portfolioHistoryEndpoint = new PortfolioHistoryEndpoint(brokerClient);
    }

    /**
     * @return {@link AccountEndpoint}
     */
    public AccountEndpoint account() {
        return accountEndpoint;
    }

    /**
     * @return the {@link MarketDataEndpoint}
     */
    public MarketDataEndpoint marketData() {
        return marketDataEndpoint;
    }

    /**
     * @return {@link OrdersEndpoint}
     */
    public OrdersEndpoint orders() {
        return ordersEndpoint;
    }

    /**
     * @return {@link PositionsEndpoint}
     */
    public PositionsEndpoint positions() {
        return positionsEndpoint;
    }

    /**
     * @return {@link AssetsEndpoint}
     */
    public AssetsEndpoint assets() {
        return assetsEndpoint;
    }

    /**
     * @return {@link WatchlistEndpoint}
     */
    public WatchlistEndpoint watchlist() {
        return watchlistEndpoint;
    }

    /**
     * @return {@link CalendarEndpoint}
     */
    public CalendarEndpoint calendar() {
        return calendarEndpoint;
    }

    /**
     * @return {@link ClockEndpoint}
     */
    public ClockEndpoint clock() {
        return clockEndpoint;
    }

    /**
     * @return the {@link AccountConfigurationEndpoint}
     */
    public AccountConfigurationEndpoint accountConfiguration() {
        return accountConfigurationEndpoint;
    }

    /**
     * @return the {@link AccountActivitiesEndpoint}
     */
    public AccountActivitiesEndpoint accountActivities() {
        return accountActivitiesEndpoint;
    }

    /**
     * @return the {@link PortfolioHistoryEndpoint}
     */
    public PortfolioHistoryEndpoint portfolioHistory() {
        return portfolioHistoryEndpoint;
    }

    /**
     * Gets {@link #okHttpClient}.
     *
     * @return the {@link OkHttpClient}
     */
    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * Gets {@link #brokerClient}.
     *
     * @return the broker {@link AlpacaClient}
     */
    public AlpacaClient getBrokerClient() {
        return brokerClient;
    }

    /**
     * Gets {@link #dataClient}.
     *
     * @return the data {@link AlpacaClient}
     */
    public AlpacaClient getDataClient() {
        return dataClient;
    }
}
