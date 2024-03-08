package net.jacobpeterson.alpaca.rest.marketdata;

import net.jacobpeterson.alpaca.openapi.marketdata.ApiClient;
import net.jacobpeterson.alpaca.openapi.marketdata.api.CorporateActionsApi;
import net.jacobpeterson.alpaca.openapi.marketdata.api.CryptoApi;
import net.jacobpeterson.alpaca.openapi.marketdata.api.ForexApi;
import net.jacobpeterson.alpaca.openapi.marketdata.api.LogosApi;
import net.jacobpeterson.alpaca.openapi.marketdata.api.NewsApi;
import net.jacobpeterson.alpaca.openapi.marketdata.api.OptionApi;
import net.jacobpeterson.alpaca.openapi.marketdata.api.StockApi;
import okhttp3.OkHttpClient;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.jacobpeterson.alpaca.util.apikey.APIKeyUtil.createBrokerAPIAuthKey;

/**
 * {@link AlpacaMarketDataAPI} is the class used to interface with the Alpaca Market Data API endpoints. This class is
 * thread-safe.
 */
public class AlpacaMarketDataAPI {

    private final ApiClient apiClient;
    private CorporateActionsApi corporateActions;
    private CryptoApi crypto;
    private ForexApi forex;
    private LogosApi logos;
    private NewsApi news;
    private OptionApi option;
    private StockApi stock;

    /**
     * Instantiates a new {@link AlpacaMarketDataAPI}.
     *
     * @param traderKeyID     the Trader key ID
     * @param traderSecretKey the Trader secret key
     * @param brokerAPIKey    the Broker API key
     * @param brokerAPISecret the Broker API secret
     * @param okHttpClient    an existing {@link OkHttpClient} or <code>null</code> to create a new default instance
     */
    public AlpacaMarketDataAPI(String traderKeyID, String traderSecretKey,
            String brokerAPIKey, String brokerAPISecret, OkHttpClient okHttpClient) {
        checkArgument((traderKeyID != null && traderSecretKey != null) ^
                        (brokerAPIKey != null && brokerAPISecret != null),
                "You must specify a (trader key ID and secret key) or an (broker API key and secret)!");
        checkNotNull(okHttpClient);

        final boolean traderKeysGiven = traderKeyID != null && traderSecretKey != null;
        apiClient = new ApiClient(okHttpClient);
        apiClient.setServerIndex(traderKeysGiven ? 0 : 1);
        if (traderKeysGiven) {
            apiClient.addDefaultHeader("APCA-API-KEY-ID", traderKeyID);
            apiClient.addDefaultHeader("APCA-API-SECRET-KEY", traderSecretKey);
        } else {
            apiClient.addDefaultHeader("Authorization", "Basic " +
                    createBrokerAPIAuthKey(brokerAPIKey, brokerAPISecret));
        }
    }

    /**
     * Gets the internal {@link ApiClient}.
     *
     * @return the {@link ApiClient}
     */
    public ApiClient getInternalAPIClient() {
        return apiClient;
    }

    /**
     * Gets the {@link CorporateActionsApi}. Lazily instantiated.
     *
     * @return the {@link CorporateActionsApi}
     */
    public synchronized CorporateActionsApi corporateActions() {
        if (corporateActions == null) {
            corporateActions = new CorporateActionsApi(apiClient);
        }
        return corporateActions;
    }

    /**
     * Gets the {@link CryptoApi}. Lazily instantiated.
     *
     * @return the {@link CryptoApi}
     */
    public synchronized CryptoApi crypto() {
        if (crypto == null) {
            crypto = new CryptoApi(apiClient);
        }
        return crypto;
    }

    /**
     * Gets the {@link ForexApi}. Lazily instantiated.
     *
     * @return the {@link ForexApi}
     */
    public synchronized ForexApi forex() {
        if (forex == null) {
            forex = new ForexApi(apiClient);
        }
        return forex;
    }

    /**
     * Gets the {@link LogosApi}. Lazily instantiated.
     *
     * @return the {@link LogosApi}
     */
    public synchronized LogosApi logos() {
        if (logos == null) {
            logos = new LogosApi(apiClient);
        }
        return logos;
    }

    /**
     * Gets the {@link NewsApi}. Lazily instantiated.
     *
     * @return the {@link NewsApi}
     */
    public synchronized NewsApi news() {
        if (news == null) {
            news = new NewsApi(apiClient);
        }
        return news;
    }

    /**
     * Gets the {@link OptionApi}. Lazily instantiated.
     *
     * @return the {@link OptionApi}
     */
    public synchronized OptionApi option() {
        if (option == null) {
            option = new OptionApi(apiClient);
        }
        return option;
    }

    /**
     * Gets the {@link StockApi}. Lazily instantiated.
     *
     * @return the {@link StockApi}
     */
    public synchronized StockApi stock() {
        if (stock == null) {
            stock = new StockApi(apiClient);
        }
        return stock;
    }
}
