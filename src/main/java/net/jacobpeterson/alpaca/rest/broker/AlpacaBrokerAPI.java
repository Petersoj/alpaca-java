package net.jacobpeterson.alpaca.rest.broker;

import com.google.gson.JsonElement;
import net.jacobpeterson.alpaca.model.util.apitype.BrokerAPIEndpointType;
import net.jacobpeterson.alpaca.openapi.broker.ApiClient;
import net.jacobpeterson.alpaca.openapi.broker.api.AccountsApi;
import net.jacobpeterson.alpaca.openapi.broker.api.AssetsApi;
import net.jacobpeterson.alpaca.openapi.broker.api.CalendarApi;
import net.jacobpeterson.alpaca.openapi.broker.api.ClockApi;
import net.jacobpeterson.alpaca.openapi.broker.api.CorporateActionsApi;
import net.jacobpeterson.alpaca.openapi.broker.api.CountryInfoApi;
import net.jacobpeterson.alpaca.openapi.broker.api.DocumentsApi;
import net.jacobpeterson.alpaca.openapi.broker.api.FundingApi;
import net.jacobpeterson.alpaca.openapi.broker.api.FundingWalletsApi;
import net.jacobpeterson.alpaca.openapi.broker.api.JournalsApi;
import net.jacobpeterson.alpaca.openapi.broker.api.KycApi;
import net.jacobpeterson.alpaca.openapi.broker.api.LogosApi;
import net.jacobpeterson.alpaca.openapi.broker.api.OAuthApi;
import net.jacobpeterson.alpaca.openapi.broker.api.RebalancingApi;
import net.jacobpeterson.alpaca.openapi.broker.api.ReportingApi;
import net.jacobpeterson.alpaca.openapi.broker.api.TradingApi;
import net.jacobpeterson.alpaca.openapi.broker.api.WatchlistApi;
import net.jacobpeterson.alpaca.openapi.broker.model.AdminActionLegacyNote;
import net.jacobpeterson.alpaca.openapi.broker.model.AdminActionLiquidation;
import net.jacobpeterson.alpaca.openapi.broker.model.AdminActionTransactionCancel;
import net.jacobpeterson.alpaca.openapi.broker.model.JNLC;
import net.jacobpeterson.alpaca.openapi.broker.model.JNLS;
import net.jacobpeterson.alpaca.openapi.broker.model.NonTradeActivity;
import net.jacobpeterson.alpaca.openapi.broker.model.TradeActivity;
import net.jacobpeterson.alpaca.rest.broker.events.EventsApiSSE;
import okhttp3.OkHttpClient;

import java.util.function.BiFunction;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.jacobpeterson.alpaca.util.apikey.APIKeyUtil.createBrokerAPIAuthKey;

/**
 * {@link AlpacaBrokerAPI} is the class used to interface with the Alpaca Broker API endpoints. This class is
 * thread-safe.
 */
public class AlpacaBrokerAPI {

    // Set validation predicates for 'anyOf' and 'oneOf' models
    static {
        // 'AccountsApi' models
        TradeActivity.isValid = jsonElement -> jsonElement.getAsJsonObject().has("type");
        NonTradeActivity.isValid = jsonElement -> !jsonElement.getAsJsonObject().has("type");

        // 'JournalsApi' models
        final BiFunction<Boolean, JsonElement, Boolean> transferPredicate = (jnlc, jsonElement) -> {
            final String entryType = jsonElement.getAsJsonObject().get("entry_type").getAsString();
            return jnlc ? entryType.equals("JNLC") : entryType.equals("JNLS");
        };
        JNLS.isValid = jsonElement -> transferPredicate.apply(true, jsonElement);
        JNLC.isValid = jsonElement -> transferPredicate.apply(false, jsonElement);

        // 'EventsApi' models
        AdminActionLegacyNote.isValid = jsonElement -> jsonElement.getAsJsonObject()
                .get("type").getAsString().contains("note_admin_event");
        AdminActionLiquidation.isValid = jsonElement -> jsonElement.getAsJsonObject()
                .get("type").getAsString().equals("liquidation_admin_event");
        AdminActionTransactionCancel.isValid = jsonElement -> jsonElement.getAsJsonObject()
                .get("type").getAsString().equals("transaction_cancel_admin_event");
    }

    private final ApiClient apiClient;
    private AccountsApi accounts;
    private AssetsApi assets;
    private CalendarApi calendar;
    private ClockApi clock;
    private CorporateActionsApi corporateActions;
    private CountryInfoApi countryInfo;
    private DocumentsApi documents;
    private EventsApiSSE events;
    private FundingApi funding;
    private FundingWalletsApi fundingWallets;
    private JournalsApi journals;
    private KycApi kyc;
    private LogosApi logos;
    private OAuthApi oAuth;
    private RebalancingApi rebalancing;
    private ReportingApi reporting;
    private TradingApi trading;
    private WatchlistApi watchlist;

    /**
     * Instantiates a new {@link AlpacaBrokerAPI}.
     *
     * @param brokerAPIKey          the Broker API key
     * @param brokerAPISecret       the Broker API secret
     * @param brokerAPIEndpointType the {@link BrokerAPIEndpointType}
     * @param okHttpClient          an existing {@link OkHttpClient} or <code>null</code> to create a new default
     *                              instance
     */
    @SuppressWarnings("UnnecessaryDefault")
    public AlpacaBrokerAPI(String brokerAPIKey, String brokerAPISecret, BrokerAPIEndpointType brokerAPIEndpointType,
            OkHttpClient okHttpClient) {
        checkNotNull(brokerAPIKey);
        checkNotNull(brokerAPISecret);
        checkNotNull(brokerAPIEndpointType);
        checkNotNull(okHttpClient);

        apiClient = new ApiClient(okHttpClient);
        apiClient.setServerIndex(switch (brokerAPIEndpointType) {
            case SANDBOX -> 0;
            case PRODUCTION -> 1;
            default -> throw new UnsupportedOperationException();
        });
        apiClient.addDefaultHeader("Authorization", "Basic " +
                createBrokerAPIAuthKey(brokerAPIKey, brokerAPISecret));
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
     * Gets the {@link AccountsApi}. Lazily instantiated.
     *
     * @return the {@link AccountsApi}
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
     * @return the {@link AssetsApi}
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
     * @return the {@link CalendarApi}
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
     * @return the {@link ClockApi}
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
    public synchronized CorporateActionsApi corporateActions() {
        if (corporateActions == null) {
            corporateActions = new CorporateActionsApi(apiClient);
        }
        return corporateActions;
    }

    /**
     * Gets the {@link CountryInfoApi}. Lazily instantiated.
     *
     * @return the {@link CountryInfoApi}
     */
    public synchronized CountryInfoApi countryInfo() {
        if (countryInfo == null) {
            countryInfo = new CountryInfoApi(apiClient);
        }
        return countryInfo;
    }

    /**
     * Gets the {@link DocumentsApi}. Lazily instantiated.
     *
     * @return the {@link DocumentsApi}
     */
    public synchronized DocumentsApi documents() {
        if (documents == null) {
            documents = new DocumentsApi(apiClient);
        }
        return documents;
    }

    /**
     * Gets the {@link EventsApiSSE}. Lazily instantiated.
     *
     * @return the {@link EventsApiSSE}
     */
    public synchronized EventsApiSSE events() {
        if (events == null) {
            events = new EventsApiSSE(apiClient);
        }
        return events;
    }

    /**
     * Gets the {@link FundingApi}. Lazily instantiated.
     *
     * @return the {@link FundingApi}
     */
    public synchronized FundingApi funding() {
        if (funding == null) {
            funding = new FundingApi(apiClient);
        }
        return funding;
    }

    /**
     * Gets the {@link FundingWalletsApi}. Lazily instantiated.
     *
     * @return the {@link FundingWalletsApi}
     */
    public synchronized FundingWalletsApi fundingWallets() {
        if (fundingWallets == null) {
            fundingWallets = new FundingWalletsApi(apiClient);
        }
        return fundingWallets;
    }

    /**
     * Gets the {@link JournalsApi}. Lazily instantiated.
     *
     * @return the {@link JournalsApi}
     */
    public synchronized JournalsApi journals() {
        if (journals == null) {
            journals = new JournalsApi(apiClient);
        }
        return journals;
    }

    /**
     * Gets the {@link KycApi}. Lazily instantiated.
     *
     * @return the {@link KycApi}
     */
    public synchronized KycApi kyc() {
        if (kyc == null) {
            kyc = new KycApi(apiClient);
        }
        return kyc;
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
     * Gets the {@link OAuthApi}. Lazily instantiated.
     *
     * @return the {@link OAuthApi}
     */
    public synchronized OAuthApi oAuth() {
        if (oAuth == null) {
            oAuth = new OAuthApi(apiClient);
        }
        return oAuth;
    }

    /**
     * Gets the {@link RebalancingApi}. Lazily instantiated.
     *
     * @return the {@link RebalancingApi}
     */
    public synchronized RebalancingApi rebalancing() {
        if (rebalancing == null) {
            rebalancing = new RebalancingApi(apiClient);
        }
        return rebalancing;
    }

    /**
     * Gets the {@link ReportingApi}. Lazily instantiated.
     *
     * @return the {@link ReportingApi}
     */
    public synchronized ReportingApi reporting() {
        if (reporting == null) {
            reporting = new ReportingApi(apiClient);
        }
        return reporting;
    }

    /**
     * Gets the {@link TradingApi}. Lazily instantiated.
     *
     * @return the {@link TradingApi}
     */
    public synchronized TradingApi trading() {
        if (trading == null) {
            trading = new TradingApi(apiClient);
        }
        return trading;
    }

    /**
     * Gets the {@link WatchlistApi}. Lazily instantiated.
     *
     * @return the {@link WatchlistApi}
     */
    public synchronized WatchlistApi watchlist() {
        if (watchlist == null) {
            watchlist = new WatchlistApi(apiClient);
        }
        return watchlist;
    }
}
