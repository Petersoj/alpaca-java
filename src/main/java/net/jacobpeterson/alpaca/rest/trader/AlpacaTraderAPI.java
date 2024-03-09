package net.jacobpeterson.alpaca.rest.trader;

import net.jacobpeterson.alpaca.model.util.apitype.TraderAPIEndpointType;
import net.jacobpeterson.alpaca.openapi.trader.ApiClient;
import net.jacobpeterson.alpaca.openapi.trader.api.AccountActivitiesApi;
import net.jacobpeterson.alpaca.openapi.trader.api.AccountConfigurationsApi;
import net.jacobpeterson.alpaca.openapi.trader.api.AccountsApi;
import net.jacobpeterson.alpaca.openapi.trader.api.AssetsApi;
import net.jacobpeterson.alpaca.openapi.trader.api.CalendarApi;
import net.jacobpeterson.alpaca.openapi.trader.api.ClockApi;
import net.jacobpeterson.alpaca.openapi.trader.api.CorporateActionsApi;
import net.jacobpeterson.alpaca.openapi.trader.api.OrdersApi;
import net.jacobpeterson.alpaca.openapi.trader.api.PortfolioHistoryApi;
import net.jacobpeterson.alpaca.openapi.trader.api.PositionsApi;
import net.jacobpeterson.alpaca.openapi.trader.api.WatchlistsApi;
import net.jacobpeterson.alpaca.openapi.trader.model.NonTradeActivities;
import net.jacobpeterson.alpaca.openapi.trader.model.TradingActivities;
import okhttp3.OkHttpClient;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AlpacaTraderAPI} is the class used to interface with the Alpaca Trader API endpoints. This class is
 * thread-safe.
 */
public class AlpacaTraderAPI {

    // Set validation predicates for 'anyOf' and 'oneOf' models
    static {
        // 'AccountConfigurationsApi' models
        TradingActivities.isValid = jsonElement -> jsonElement.getAsJsonObject().has("type");
        NonTradeActivities.isValid = jsonElement -> !jsonElement.getAsJsonObject().has("type");
    }

    private final ApiClient apiClient;
    private AccountActivitiesApi accountActivities;
    private AccountConfigurationsApi accountConfigurations;
    private AccountsApi accounts;
    private AssetsApi assets;
    private CalendarApi calendar;
    private ClockApi clock;
    private CorporateActionsApi traderCorporateActions;
    private OrdersApi orders;
    private PortfolioHistoryApi portfolioHistory;
    private PositionsApi positions;
    private WatchlistsApi watchlists;

    /**
     * Instantiates a new {@link AlpacaTraderAPI}.
     *
     * @param traderKeyID           the Trader key ID
     * @param traderSecretKey       the Trader secret key
     * @param traderOAuthToken      the Trader OAuth token
     * @param traderAPIEndpointType the {@link TraderAPIEndpointType}
     * @param okHttpClient          an existing {@link OkHttpClient} or <code>null</code> to create a new default
     *                              instance
     */
    @SuppressWarnings("UnnecessaryDefault")
    public AlpacaTraderAPI(String traderKeyID, String traderSecretKey, String traderOAuthToken,
            TraderAPIEndpointType traderAPIEndpointType, OkHttpClient okHttpClient) {
        checkArgument((traderKeyID != null && traderSecretKey != null) ^ (traderOAuthToken != null),
                "You must specify a (trader key ID and secret key) or an (OAuth token)!");
        checkNotNull(traderAPIEndpointType);
        checkNotNull(okHttpClient);

        apiClient = new ApiClient(okHttpClient);
        apiClient.setServerIndex(switch (traderAPIEndpointType) {
            case PAPER -> 0;
            case LIVE -> 1;
            default -> throw new UnsupportedOperationException();
        });
        if (traderKeyID != null && traderSecretKey != null) {
            apiClient.addDefaultHeader("APCA-API-KEY-ID", traderKeyID);
            apiClient.addDefaultHeader("APCA-API-SECRET-KEY", traderSecretKey);
        } else {
            apiClient.addDefaultHeader("Authorization", "Bearer " + traderOAuthToken);
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
     * Gets the {@link AccountActivitiesApi}. Lazily instantiated.
     *
     * @return the {@link AccountActivitiesApi}
     */
    public synchronized AccountActivitiesApi accountActivities() {
        if (accountActivities == null) {
            accountActivities = new AccountActivitiesApi(apiClient);
        }
        return accountActivities;
    }

    /**
     * Gets the {@link AccountConfigurationsApi}. Lazily instantiated.
     *
     * @return the {@link AccountConfigurationsApi}
     */
    public synchronized AccountConfigurationsApi accountConfigurations() {
        if (accountConfigurations == null) {
            accountConfigurations = new AccountConfigurationsApi(apiClient);
        }
        return accountConfigurations;
    }

    /**
     * Gets the {@link AccountsApi}. Lazily instantiated.
     *
     * @return {@link AccountsApi}
     */
    public synchronized AccountsApi accounts() {
        if (accounts == null) {
            accounts = new AccountsApi(apiClient);
        }
        return accounts;
    }

    /**
     * Gets the {@link AssetsApi}. Lazily instantiated.
     *
     * @return {@link AssetsApi}
     */
    public synchronized AssetsApi assets() {
        if (assets == null) {
            assets = new AssetsApi(apiClient);
        }
        return assets;
    }

    /**
     * Gets the {@link CalendarApi}. Lazily instantiated.
     *
     * @return {@link CalendarApi}
     */
    public synchronized CalendarApi calendar() {
        if (calendar == null) {
            calendar = new CalendarApi(apiClient);
        }
        return calendar;
    }

    /**
     * Gets the {@link ClockApi}. Lazily instantiated.
     *
     * @return {@link ClockApi}
     */
    public synchronized ClockApi clock() {
        if (clock == null) {
            clock = new ClockApi(apiClient);
        }
        return clock;
    }

    /**
     * Gets the {@link CorporateActionsApi}. Lazily instantiated.
     *
     * @return the {@link CorporateActionsApi}
     */
    public synchronized CorporateActionsApi traderCorporateActions() {
        if (traderCorporateActions == null) {
            traderCorporateActions = new CorporateActionsApi(apiClient);
        }
        return traderCorporateActions;
    }

    /**
     * Gets the {@link OrdersApi}. Lazily instantiated.
     *
     * @return {@link OrdersApi}
     */
    public synchronized OrdersApi orders() {
        if (orders == null) {
            orders = new OrdersApi(apiClient);
        }
        return orders;
    }

    /**
     * Gets the {@link PortfolioHistoryApi}. Lazily instantiated.
     *
     * @return {@link PortfolioHistoryApi}
     */
    public synchronized PortfolioHistoryApi portfolioHistory() {
        if (portfolioHistory == null) {
            portfolioHistory = new PortfolioHistoryApi(apiClient);
        }
        return portfolioHistory;
    }

    /**
     * Gets the {@link PositionsApi}. Lazily instantiated.
     *
     * @return {@link PositionsApi}
     */
    public synchronized PositionsApi positions() {
        if (positions == null) {
            positions = new PositionsApi(apiClient);
        }
        return positions;
    }

    /**
     * Gets the {@link WatchlistsApi}. Lazily instantiated.
     *
     * @return {@link WatchlistsApi}
     */
    public synchronized WatchlistsApi watchlists() {
        if (watchlists == null) {
            watchlists = new WatchlistsApi(apiClient);
        }
        return watchlists;
    }
}
