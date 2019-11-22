package io.github.mainstringargs.alpaca;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import io.github.mainstringargs.alpaca.enums.ActivityType;
import io.github.mainstringargs.alpaca.enums.AssetStatus;
import io.github.mainstringargs.alpaca.enums.BarsTimeFrame;
import io.github.mainstringargs.alpaca.enums.Direction;
import io.github.mainstringargs.alpaca.enums.OrderSide;
import io.github.mainstringargs.alpaca.enums.OrderStatus;
import io.github.mainstringargs.alpaca.enums.OrderTimeInForce;
import io.github.mainstringargs.alpaca.enums.OrderType;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;
import io.github.mainstringargs.alpaca.rest.AlpacaRequest;
import io.github.mainstringargs.alpaca.rest.AlpacaRequestBuilder;
import io.github.mainstringargs.alpaca.rest.exceptions.AlpacaAPIRequestException;
import io.github.mainstringargs.alpaca.websocket.AlpacaStreamListener;
import io.github.mainstringargs.alpaca.websocket.AlpacaWebsocketClient;
import io.github.mainstringargs.domain.alpaca.account.Account;
import io.github.mainstringargs.domain.alpaca.accountactivities.AccountActivity;
import io.github.mainstringargs.domain.alpaca.accountactivities.NonTradeActivity;
import io.github.mainstringargs.domain.alpaca.accountactivities.TradeActivity;
import io.github.mainstringargs.domain.alpaca.accountconfiguration.AccountConfiguration;
import io.github.mainstringargs.domain.alpaca.asset.Asset;
import io.github.mainstringargs.domain.alpaca.bar.Bar;
import io.github.mainstringargs.domain.alpaca.calendar.Calendar;
import io.github.mainstringargs.domain.alpaca.clock.Clock;
import io.github.mainstringargs.domain.alpaca.order.Order;
import io.github.mainstringargs.domain.alpaca.position.Position;
import io.github.mainstringargs.domain.alpaca.watchlist.Watchlist;
import io.github.mainstringargs.util.gson.GsonUtil;
import io.github.mainstringargs.util.time.TimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * The Class AlpacaAPI.
 */
public class AlpacaAPI {

    /** The logger. */
    private static Logger LOGGER = LogManager.getLogger(AlpacaAPI.class);

    /** The version. */
    private final String apiVersion;

    /** The key id. */
    private final String keyId;

    /** The base API url. */
    private final String baseAPIURL;

    /** The alpaca request. */
    private final AlpacaRequest alpacaRequest;

    /** The base data url. */
    private final String baseDataUrl;

    /** The alpaca web socket client. */
    private final AlpacaWebsocketClient alpacaWebSocketClient;

    /**
     * Instantiates a new Alpaca API using properties specified in alpaca.properties file (or relevant defaults)
     */
    public AlpacaAPI() {
        this(AlpacaProperties.API_VERSION_VALUE, AlpacaProperties.KEY_ID_VALUE,
                AlpacaProperties.SECRET_VALUE, AlpacaProperties.BASE_API_URL_VALUE,
                AlpacaProperties.BASE_DATA_URL_VALUE);

        LOGGER.info(AlpacaProperties.staticToString());
    }

    /**
     * Instantiates a new Alpaca API using the specified apiVersion
     *
     * @param apiVersion the api version
     */
    public AlpacaAPI(String apiVersion) {
        this(apiVersion, AlpacaProperties.KEY_ID_VALUE, AlpacaProperties.SECRET_VALUE,
                AlpacaProperties.BASE_API_URL_VALUE, AlpacaProperties.BASE_DATA_URL_VALUE);
    }

    /**
     * Instantiates a new Alpaca API using the specified apiVersion, keyId, and secret.
     *
     * @param keyId  the key id
     * @param secret the secret
     */
    public AlpacaAPI(String apiVersion, String keyId, String secret) {
        this(apiVersion, keyId, secret, AlpacaProperties.BASE_API_URL_VALUE,
                AlpacaProperties.BASE_DATA_URL_VALUE);
    }

    /**
     * Instantiates a new Alpaca API using the specified apiVersion, keyId, secret, and baseAPIURL.
     *
     * @param apiVersion the api version
     * @param keyId      the key id
     * @param secret     the secret
     * @param baseAPIURL the api account url
     */
    public AlpacaAPI(String apiVersion, String keyId, String secret, String baseAPIURL) {
        this(apiVersion, keyId, secret, baseAPIURL, AlpacaProperties.BASE_DATA_URL_VALUE);
    }

    /**
     * Instantiates a new Alpaca API using the specified keyId, secret, baseAPIURL, and baseDataUrl.
     *
     * @param apiVersion  the api version
     * @param keyId       the key id
     * @param secret      the secret
     * @param baseAPIURL  the base api url
     * @param baseDataUrl the base data url
     */
    public AlpacaAPI(String apiVersion, String keyId, String secret, String baseAPIURL, String baseDataUrl) {
        this.apiVersion = apiVersion;
        this.keyId = keyId;
        this.baseAPIURL = baseAPIURL;
        this.baseDataUrl = baseDataUrl;

        alpacaRequest = new AlpacaRequest(keyId, secret);
        alpacaWebSocketClient = new AlpacaWebsocketClient(keyId, secret, baseAPIURL);

        LOGGER.info(this.toString());
    }

    /**
     * Gets the account.
     *
     * @return the account
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/account/#get-the-account">Get the Account</a>
     */
    public Account getAccount() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ACCOUNT_ENDPOINT);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Account.class);
    }

    /**
     * Gets account activities.
     *
     * @param date          the date
     * @param until         the until
     * @param after         the after
     * @param direction     the direction
     * @param pageSize      the page size
     * @param pageToken     the page token
     * @param activityTypes the activity types (null for all activities)
     *
     * @return the account activities
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/account-activities/">Gets the Account
     * Activities</a>
     */
    public ArrayList<AccountActivity> getAccountActivities(LocalDateTime date,
            LocalDateTime until, LocalDateTime after, Direction direction, Integer pageSize, String pageToken,
            ActivityType... activityTypes)
            throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, AlpacaConstants.VERSION_2_ENDPOINT,
                AlpacaConstants.ACCOUNT_ENDPOINT,
                AlpacaConstants.ACTIVITIES_ENDPOINT);

        if (activityTypes != null) { // Check if we don't want all activity types
            if (activityTypes.length == 1) { // Get one activity
                urlBuilder.appendEndpoint(activityTypes[0].getAPIName());
            } else { // Get list of activities
                urlBuilder.appendURLParameter("activity_types", Arrays.stream(activityTypes)
                        .map(ActivityType::getAPIName).collect(Collectors.joining(","))); // Makes comma-separated list
            }
        }

        if (date != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.AFTER_PARAMETER, TimeUtil.toDateTimeString(date));
        }

        if (until != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.UNTIL_PARAMETER, TimeUtil.toDateTimeString(until));
        }

        if (after != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.AFTER_PARAMETER, TimeUtil.toDateTimeString(after));
        }

        if (direction != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.DIRECTION_PARAMETER, direction.getAPIName());
        }

        if (pageSize != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.PAGE_SIZE_PARAMTER, pageSize.toString());
        }

        if (pageToken != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.PAGE_TOKEN_PARAMTER, pageToken);
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        JsonElement responseJsonElement = alpacaRequest.getResponseJSON(response);
        ArrayList<AccountActivity> accountActivities = new ArrayList<>();

        if (responseJsonElement instanceof JsonArray) {
            JsonArray responseJsonArray = (JsonArray) responseJsonElement;

            for (JsonElement arrayJsonElement : responseJsonArray) { // Loop through response array
                if (arrayJsonElement instanceof JsonObject) {
                    JsonObject arrayJsonObject = (JsonObject) arrayJsonElement;

                    if (GsonUtil.doesGsonPOJOMatch(TradeActivity.class, arrayJsonObject)) {
                        accountActivities.add(GsonUtil.GSON.fromJson(arrayJsonObject, TradeActivity.class));
                    } else if (GsonUtil.doesGsonPOJOMatch(NonTradeActivity.class, arrayJsonObject)) {
                        accountActivities.add(GsonUtil.GSON.fromJson(arrayJsonObject, NonTradeActivity.class));
                    } else {
                        LOGGER.warn("Received unknown JSON Object in response!");
                    }
                } else {
                    throw new IllegalStateException("All array elements must be objects!");
                }
            }
            return accountActivities;
        } else {
            throw new IllegalStateException("The response must be an array!");
        }
    }

    /**
     * Gets account configurations.
     *
     * @return the account configurations
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see
     * <a href="https://docs.alpaca.markets/api-documentation/api-v2/account-configuration/#get-the-account-configurations">Get
     * the account configuration</a>
     */
    public AccountConfiguration getAccountConfigurations() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ACCOUNT_ENDPOINT,
                AlpacaConstants.CONFIGURATIONS_ENDPOINT);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, AccountConfiguration.class);
    }

    /**
     * Sets account configuration.
     *
     * @param accountConfiguration the account configuration
     *
     * @return the updated account configuration
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see
     * <a href="https://docs.alpaca.markets/api-documentation/api-v2/account-configuration/#update-the-account-configurations">Set
     * the Account Configuration</a>
     */
    public AccountConfiguration setAccountConfiguration(AccountConfiguration accountConfiguration)
            throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ACCOUNT_ENDPOINT,
                AlpacaConstants.CONFIGURATIONS_ENDPOINT);
        urlBuilder.setCustomBody(GsonUtil.GSON.toJson(accountConfiguration));

        HttpResponse<InputStream> response = alpacaRequest.invokePatch(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, AccountConfiguration.class);
    }

    /**
     * Gets the orders using the following API defaults:.
     *
     * @return the orders
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders">Get a list of
     * Orders</a>
     */
    public List<Order> getOrders() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ORDERS_ENDPOINT);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type listType = new TypeToken<List<Order>>() {}.getType();

        return alpacaRequest.getResponseObject(response, listType);
    }

    /**
     * Gets the orders.
     *
     * @param status    the status (Open if null)
     * @param limit     the limit (50 if null)
     * @param after     the after
     * @param until     the until
     * @param direction the direction (Descending if null)
     *
     * @return the orders
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders">Get a list of
     * Orders</a>
     */
    public List<Order> getOrders(OrderStatus status, Integer limit, LocalDateTime after,
            LocalDateTime until, Direction direction) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ORDERS_ENDPOINT);

        if (status != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.STATUS_PARAMETER, status.getAPIName());
        }

        if (limit != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.LIMIT_PARAMETER, limit.toString());
        }

        if (after != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.AFTER_PARAMETER,
                    TimeUtil.toDateTimeString(after));
        }

        if (until != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.UNTIL_PARAMETER,
                    TimeUtil.toDateTimeString(until));
        }

        if (direction != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.DIRECTION_PARAMETER, direction.getAPIName());
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type listType = new TypeToken<List<Order>>() {}.getType();

        return alpacaRequest.getResponseObject(response, listType);
    }

    /**
     * Request/Submits new order.
     *
     * @param symbol        the symbol or asset_id (required)
     * @param quantity      the quantity (required)
     * @param side          the side (required)
     * @param type          the type (required)
     * @param timeInForce   the time in force (required)
     * @param limitPrice    the limit price (required if Limit or Stop Limit)
     * @param stopPrice     the stop price (required if Stop or Stop Limit)
     * @param clientOrderId the client order id (optional)
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#request-a-new-order">Request a new
     * Order</a>
     */
    public Order requestNewOrder(String symbol, Integer quantity, OrderSide side, OrderType type,
            OrderTimeInForce timeInForce, Double limitPrice, Double stopPrice,
            String clientOrderId)
            throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ORDERS_ENDPOINT);

        if (symbol != null) {
            urlBuilder.appendJSONBodyProperty(AlpacaConstants.SYMBOL_PARAMETER, symbol);
        }

        if (quantity != null) {
            urlBuilder.appendJSONBodyProperty(AlpacaConstants.QTY_PARAMETER, quantity.toString());
        }

        if (side != null) {
            urlBuilder.appendJSONBodyProperty(AlpacaConstants.SIDE_PARAMETER, side.getAPIName());
        }

        if (type != null) {
            urlBuilder.appendJSONBodyProperty(AlpacaConstants.TYPE_PARAMETER, type.getAPIName());
        }

        if (timeInForce != null) {
            urlBuilder.appendJSONBodyProperty(AlpacaConstants.TIME_IN_FORCE_PARAMETER, timeInForce.getAPIName());
        }

        if (limitPrice != null) {
            urlBuilder.appendJSONBodyProperty(AlpacaConstants.LIMIT_PRICE_PARAMETER,
                    TimeUtil.toDecimalFormat(limitPrice));
        }

        if (stopPrice != null) {
            urlBuilder.appendJSONBodyProperty(AlpacaConstants.STOP_PRICE_PARAMETER,
                    TimeUtil.toDecimalFormat(stopPrice));
        }

        if (clientOrderId != null) {
            urlBuilder.appendJSONBodyProperty(AlpacaConstants.CLIENT_ORDER_ID_PARAMETER, clientOrderId);
        }

        HttpResponse<InputStream> response = alpacaRequest.invokePost(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Order.class);
    }

    /**
     * Gets the order.
     *
     * @param orderId the order id (required)
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order">Get an Order</a>
     */
    public Order getOrder(String orderId) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ORDERS_ENDPOINT,
                orderId);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Order.class);
    }

    /**
     * Gets the order by client id.
     *
     * @param clientOrderId the client order id (required)
     *
     * @return the order by client id
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order-by-client-order-id">Get
     * an Order by Client ID</a>
     */
    public Order getOrderByClientId(String clientOrderId) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ORDERS_BY_CLIENT_ORDER_ID_ENDPOINT);

        if (clientOrderId != null) {
            try {
                clientOrderId = URLEncoder.encode(clientOrderId, AlpacaConstants.UTF_ENCODING);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            urlBuilder.appendURLParameter(AlpacaConstants.CLIENT_ORDER_ID_PARAMETER, clientOrderId);
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Order.class);
    }

    /**
     * Cancel order.
     *
     * @param orderId the order id (required)
     *
     * @return true, if successful
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#cancel-an-order">Cancel an Order</a>
     */
    public boolean cancelOrder(String orderId) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ORDERS_ENDPOINT,
                orderId);

        HttpResponse<InputStream> response = alpacaRequest.invokeDelete(urlBuilder);

        if ((response.getStatus() != 200 && response.getStatus() != 204)) {
            throw new AlpacaAPIRequestException(response);
        }

        return response.getStatus() == 200 || response.getStatus() == 204;
    }

    /**
     * Gets the open positions.
     *
     * @return the open positions
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/positions/#get-open-positions">Get open
     * Positions</a>
     */
    public List<Position> getOpenPositions() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.POSITIONS_ENDPOINT);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type listType = new TypeToken<List<Position>>() {}.getType();

        return alpacaRequest.getResponseObject(response, listType);
    }

    /**
     * Gets the open position by symbol.
     *
     * @param symbol the symbol or asset_id (required)
     *
     * @return the open position by symbol
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/positions/#get-an-open-position">Get an open
     * Position</a>
     */
    public Position getOpenPositionBySymbol(String symbol) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.POSITIONS_ENDPOINT);

        if (symbol != null) {
            urlBuilder.appendEndpoint(symbol.trim());
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Position.class);
    }

    /**
     * Gets the assets using the following API defaults:
     * <p>
     * Status: Active Asset Class: us_equity.
     *
     * @return the assets
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets">Get Assets</a>
     */
    public List<Asset> getAssets() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ASSETS_ENDPOINT);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type listType = new TypeToken<List<Asset>>() {}.getType();

        return alpacaRequest.getResponseObject(response, listType);
    }

    /**
     * Gets the assets.
     *
     * @param assetStatus the asset status (Active if null)
     * @param assetClass  the asset class (us_equity if null)
     *
     * @return the assets
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets">Get Assets</a>
     */
    public List<Asset> getAssets(AssetStatus assetStatus, String assetClass)
            throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ASSETS_ENDPOINT);

        if (assetStatus != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.STATUS_PARAMETER, assetStatus.getAPIName());
        }

        if (assetClass != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.ASSET_CLASS_PARAMETER, assetClass.trim());
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type listType = new TypeToken<List<Asset>>() {}.getType();

        return alpacaRequest.getResponseObject(response, listType);
    }

    /**
     * Gets the asset by symbol or asset_id.
     *
     * @param symbol the symbol (required)
     *
     * @return the asset by symbol
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/assets/#get-an-asset">Get an Asset</a>
     */
    public Asset getAssetBySymbol(String symbol) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.ASSETS_ENDPOINT);

        if (symbol != null) {
            urlBuilder.appendEndpoint(symbol.trim());
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Asset.class);
    }

    /**
     * Gets watch lists.
     *
     * @return the watch lists
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/#get-a-list-of-watchlists">Get a
     * list of Watchlists</a>
     */
    public List<Watchlist> getWatchlists() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.WATCHLISTS_ENDPOINT);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type listType = new TypeToken<List<Watchlist>>() {}.getType();
        return alpacaRequest.getResponseObject(response, listType);
    }

    /**
     * Create watchlist watchlist.
     *
     * @param name    the name
     * @param symbols the symbols
     *
     * @return the created watchlist
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/#create-a-watchlist">Create a
     * Watchlist</a>
     */
    public Watchlist createWatchlist(String name, String... symbols) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.WATCHLISTS_ENDPOINT);

        urlBuilder.appendJSONBodyProperty("name", name);

        if (symbols != null) {
            JsonArray symbolsArray = new JsonArray();
            Arrays.stream(symbols).forEach(symbolsArray::add);

            urlBuilder.appendJSONBodyJSONProperty("symbols", symbolsArray);
        }

        HttpResponse<InputStream> response = alpacaRequest.invokePost(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type listType = new TypeToken<List<Watchlist>>() {}.getType();
        return alpacaRequest.getResponseObject(response, listType);
    }

    /**
     * Gets watchlist.
     *
     * @param watchlistID the watchlist id
     *
     * @return the watchlist
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/#get-a-watchlist">Get a
     * Watchlist</a>
     */
    public Watchlist getWatchlist(String watchlistID) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.WATCHLISTS_ENDPOINT,
                watchlistID);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Watchlist.class);
    }

    /**
     * Update watchlist.
     *
     * @param watchlistID the watchlist id
     * @param name        the name
     * @param symbols     the symbols
     *
     * @return the updated watchlist
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/#update-watchlist">Update
     * Watchlist</a>
     */
    public Watchlist updateWatchlist(String watchlistID, String name, String... symbols)
            throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.WATCHLISTS_ENDPOINT,
                watchlistID);

        if (name != null) {
            urlBuilder.appendJSONBodyProperty("name", name);
        }

        if (symbols != null) {
            JsonArray symbolsArray = new JsonArray();
            Arrays.stream(symbols).forEach(symbolsArray::add);

            urlBuilder.appendJSONBodyJSONProperty("symbols", symbolsArray);
        }

        HttpResponse<InputStream> response = alpacaRequest.invokePut(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Watchlist.class);
    }

    /**
     * Add watchlist asset watchlist.
     *
     * @param watchlistID the watchlist id
     * @param symbols     the symbols
     *
     * @return the watchlist
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/#add-an-asset">Add a Watchlist
     * Asset</a>
     */
    public Watchlist addWatchlistAsset(String watchlistID, String... symbols) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.WATCHLISTS_ENDPOINT,
                watchlistID);

        if (symbols == null) {
            throw new NullPointerException("Symbols are required!");
        }

        JsonArray symbolsArray = new JsonArray();
        Arrays.stream(symbols).forEach(symbolsArray::add);

        urlBuilder.appendJSONBodyJSONProperty("symbols", symbolsArray);

        HttpResponse<InputStream> response = alpacaRequest.invokePost(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Watchlist.class);
    }

    /**
     * Delete watchlist.
     *
     * @param watchlistID the watchlist id
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/#delete-a-watchlist">Delete a
     * Watchlist</a>
     */
    public void deleteWatchlist(String watchlistID) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.WATCHLISTS_ENDPOINT,
                watchlistID);

        HttpResponse<InputStream> response = alpacaRequest.invokeDelete(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }
    }

    /**
     * Remove symbol from watchlist.
     *
     * @param watchlistID the watchlist id
     * @param symbol      the symbol
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see
     * <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/#remove-a-symbol-from-the-watchlist">Remove
     * a Symbol from the Watchlistt</a>
     */
    public void removeSymbolFromWatchlist(String watchlistID, String symbol) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.WATCHLISTS_ENDPOINT,
                watchlistID,
                symbol);

        HttpResponse<InputStream> response = alpacaRequest.invokeDelete(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }
    }

    /**
     * Gets the calendar.
     *
     * @return the calendar
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar">Get the
     * Calendar</a>
     */
    public List<Calendar> getCalendar() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.CALENDAR_ENDPOINT);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type listType = new TypeToken<List<Calendar>>() {}.getType();

        return alpacaRequest.getResponseObject(response, listType);
    }

    /**
     * Gets the calendar.
     *
     * @param start the start
     * @param end   the end
     *
     * @return the calendar
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar">Get the
     * Calendar</a>
     */
    public List<Calendar> getCalendar(LocalDate start, LocalDate end) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.CALENDAR_ENDPOINT);

        if (start != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.START_PARAMETER, TimeUtil.toDateString(start));
        }

        if (end != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.END_PARAMETER, TimeUtil.toDateString(end));
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type listType = new TypeToken<List<Calendar>>() {}.getType();

        return alpacaRequest.getResponseObject(response, listType);
    }

    /**
     * Gets the clock.
     *
     * @return the clock
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/clock/#get-the-clock">Get the Clock</a>
     */
    public Clock getClock() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, apiVersion,
                AlpacaConstants.CLOCK_ENDPOINT);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Clock.class);
    }

    /**
     * Gets the bars.
     *
     * @param timeframe the timeframe (required)
     * @param symbols   One or more (max 200) symbol names (required)
     * @param limit     The maximum number of bars to be returned for each symbol. It can be between 1 and 1000. Default
     *                  is 100 if parameter is unspecified or 0. (100 if null)
     * @param start     Filter bars equal to or after this time. Cannot be used with after.
     * @param end       Filter bars equal to or before this time. Cannot be used with until.
     * @param after     Filter bars after this time. Cannot be used with start.
     * @param until     Filter bars before this time. Cannot be used with end.
     *
     * @return the bars
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars">Get a
     * list of Bars</a>
     */
    public Map<String, List<Bar>> getBars(BarsTimeFrame timeframe, String[] symbols, Integer limit,
            LocalDateTime start, LocalDateTime end, LocalDateTime after, LocalDateTime until)
            throws AlpacaAPIRequestException {
        //data urls still use v1 (https://docs.alpaca.markets/api-documentation/api-v2/market-data/#endpoint)
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(AlpacaConstants.VERSION_1_ENDPOINT,
                baseDataUrl, AlpacaConstants.BARS_ENDPOINT);

        if (timeframe != null) {
            urlBuilder.appendEndpoint(timeframe.getAPIName());
        }

        if (start != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.START_PARAMETER,
                    TimeUtil.toDateTimeString(start));
        }

        if (end != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.END_PARAMETER, TimeUtil.toDateTimeString(end));
        }

        if (after != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.AFTER_PARAMETER,
                    TimeUtil.toDateTimeString(after));
        }

        if (until != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.UNTIL_PARAMETER,
                    TimeUtil.toDateTimeString(until));
        }

        if (limit != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.LIMIT_PARAMETER, limit.toString());
        }

        if (symbols != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.SYMBOLS_PARAMETER, String.join(",", symbols));
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type mapType = new TypeToken<Map<String, List<Bar>>>() {}.getType();

        return alpacaRequest.getResponseObject(response, mapType);
    }

    /**
     * Gets the bars.
     *
     * @param timeframe the timeframe (required)
     * @param symbol    One symbol name (required)
     * @param limit     The maximum number of bars to be returned for each symbol. It can be between 1 and 1000. Default
     *                  is 100 if parameter is unspecified or 0. (100 if null)
     * @param start     Filter bars equal to or after this time. Cannot be used with after.
     * @param end       Filter bars equal to or before this time. Cannot be used with until.
     * @param after     Filter bars after this time. Cannot be used with start.
     * @param until     Filter bars before this time. Cannot be used with end.
     *
     * @return the bars
     *
     * @throws AlpacaAPIRequestException the alpaca API exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars">Get a
     * list of Bars</a>
     */
    public List<Bar> getBars(BarsTimeFrame timeframe, String symbol, Integer limit,
            LocalDateTime start, LocalDateTime end, LocalDateTime after, LocalDateTime until)
            throws AlpacaAPIRequestException {
        //data urls still use v1 (https://docs.alpaca.markets/api-documentation/api-v2/market-data/#endpoint)
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(AlpacaConstants.VERSION_1_ENDPOINT,
                baseDataUrl, AlpacaConstants.BARS_ENDPOINT);

        if (timeframe != null) {
            urlBuilder.appendEndpoint(timeframe.getAPIName());
        }

        if (start != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.START_PARAMETER,
                    TimeUtil.toDateTimeString(start));
        }

        if (end != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.END_PARAMETER, TimeUtil.toDateTimeString(end));
        }

        if (after != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.AFTER_PARAMETER,
                    TimeUtil.toDateTimeString(after));
        }

        if (until != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.UNTIL_PARAMETER,
                    TimeUtil.toDateTimeString(until));
        }

        if (limit != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.LIMIT_PARAMETER, limit.toString());
        }

        if (symbol != null) {
            urlBuilder.appendURLParameter(AlpacaConstants.SYMBOLS_PARAMETER, String.join(",", symbol));
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type mapType = new TypeToken<Map<String, List<Bar>>>() {}.getType();

        Map<String, List<Bar>> bars = alpacaRequest.getResponseObject(response, mapType);

        return bars.get(symbol);
    }

    /**
     * Adds the alpaca stream listener.
     *
     * @param streamListener the stream listener
     */
    public void addAlpacaStreamListener(AlpacaStreamListener streamListener) {
        alpacaWebSocketClient.addListener(streamListener);
    }

    /**
     * Removes the alpaca stream listener.
     *
     * @param streamListener the stream listener
     */
    public void removeAlpacaStreamListener(AlpacaStreamListener streamListener) {
        alpacaWebSocketClient.removeListener(streamListener);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("apiVersion = " + apiVersion)
                .add("baseAPIURL = " + baseAPIURL)
                .add("baseDataUrl = " + baseDataUrl)
                .add("keyId = " + keyId)
                .toString();
    }
}
