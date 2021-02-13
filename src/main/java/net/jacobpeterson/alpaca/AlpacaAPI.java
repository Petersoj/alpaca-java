package net.jacobpeterson.alpaca;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import net.jacobpeterson.abstracts.enums.SortDirection;
import net.jacobpeterson.abstracts.websocket.exception.WebsocketException;
import net.jacobpeterson.alpaca.AlpacaConstants.Endpoints;
import net.jacobpeterson.alpaca.AlpacaConstants.Parameters;
import net.jacobpeterson.alpaca.enums.*;
import net.jacobpeterson.alpaca.properties.AlpacaProperties;
import net.jacobpeterson.alpaca.rest.AlpacaRequest;
import net.jacobpeterson.alpaca.rest.AlpacaRequestBuilder;
import net.jacobpeterson.alpaca.rest.exception.AlpacaAPIRequestException;
import net.jacobpeterson.alpaca.websocket.broker.client.AlpacaWebsocketClient;
import net.jacobpeterson.alpaca.websocket.broker.listener.AlpacaStreamListener;
import net.jacobpeterson.alpaca.websocket.marketdata.client.MarketDataWebsocketClient;
import net.jacobpeterson.alpaca.websocket.marketdata.listener.MarketDataStreamListener;
import net.jacobpeterson.domain.alpaca.account.Account;
import net.jacobpeterson.domain.alpaca.accountactivities.AccountActivity;
import net.jacobpeterson.domain.alpaca.accountactivities.NonTradeActivity;
import net.jacobpeterson.domain.alpaca.accountactivities.TradeActivity;
import net.jacobpeterson.domain.alpaca.accountconfiguration.AccountConfiguration;
import net.jacobpeterson.domain.alpaca.asset.Asset;
import net.jacobpeterson.domain.alpaca.calendar.Calendar;
import net.jacobpeterson.domain.alpaca.clock.Clock;
import net.jacobpeterson.domain.alpaca.marketdata.Bar;
import net.jacobpeterson.domain.alpaca.marketdata.LastQuoteResponse;
import net.jacobpeterson.domain.alpaca.marketdata.LastTradeResponse;
import net.jacobpeterson.domain.alpaca.order.CancelledOrder;
import net.jacobpeterson.domain.alpaca.order.Order;
import net.jacobpeterson.domain.alpaca.portfoliohistory.PortfolioHistory;
import net.jacobpeterson.domain.alpaca.position.ClosePositionOrder;
import net.jacobpeterson.domain.alpaca.position.Position;
import net.jacobpeterson.domain.alpaca.watchlist.Watchlist;
import net.jacobpeterson.util.format.FormatUtil;
import net.jacobpeterson.util.gson.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link AlpacaAPI} contains several methods to interface with Alpaca. You will generally only need one instance of it
 * in your application. Note that many methods allow <code>null<code/> to be passed in as a parameter if it is
 * optional.
 * <br>
 * Note that most of these methods are blocking methods and this class in not thread-safe.
 *
 * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/">Alpaca API documentation</a>
 */
public class AlpacaAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaAPI.class);

    private final String keyID;
    private final String baseAPIURL;
    private final AlpacaRequest alpacaRequest;
    private final String baseDataUrl;
    private final AlpacaWebsocketClient alpacaWebSocketClient;
    private final MarketDataWebsocketClient marketDataWebSocketClient;

    /**
     * Instantiates a new {@link AlpacaAPI} using properties specified in <code>alpaca.properties</code> file (or their
     * associated defaults)
     */
    public AlpacaAPI() {
        this(AlpacaProperties.KEY_ID_VALUE, AlpacaProperties.SECRET_VALUE, null, AlpacaProperties.BASE_API_URL_VALUE,
                AlpacaProperties.BASE_DATA_URL_VALUE);

        LOGGER.debug(AlpacaProperties.staticToString());
    }

    /**
     * Instantiates a new {@link AlpacaAPI}.
     *
     * @param keyID  the key ID
     * @param secret the secret
     */
    public AlpacaAPI(String keyID, String secret) {
        this(keyID, secret, null, AlpacaProperties.BASE_API_URL_VALUE, AlpacaProperties.BASE_DATA_URL_VALUE);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}.
     *
     * @param keyID      the key ID
     * @param secret     the secret
     * @param baseAPIURL the API URL
     */
    public AlpacaAPI(String keyID, String secret, String baseAPIURL) {
        this(keyID, secret, null, baseAPIURL, AlpacaProperties.BASE_DATA_URL_VALUE);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}.
     *
     * @param oAuthToken the OAuth token
     */
    public AlpacaAPI(String oAuthToken) {
        this(null, null, oAuthToken, AlpacaProperties.BASE_API_URL_VALUE, AlpacaProperties.BASE_DATA_URL_VALUE);
    }

    /**
     * Instantiates a new {@link AlpacaAPI}.
     *
     * @param keyID       the key ID
     * @param secret      the secret
     * @param oAuthToken  the OAuth token
     * @param baseAPIURL  the base API URL
     * @param baseDataURL the base data URL
     */
    public AlpacaAPI(String keyID, String secret, String oAuthToken, String baseAPIURL,
            String baseDataURL) {
        Preconditions.checkState((keyID != null && secret != null) || oAuthToken != null);

        this.keyID = keyID;
        this.baseAPIURL = baseAPIURL;
        this.baseDataUrl = baseDataURL;

        boolean isOAuth = oAuthToken != null;

        alpacaRequest = isOAuth ? new AlpacaRequest(oAuthToken) :
                new AlpacaRequest(keyID, secret);
        alpacaWebSocketClient = isOAuth ? new AlpacaWebsocketClient(oAuthToken, baseAPIURL) :
                new AlpacaWebsocketClient(keyID, secret, baseAPIURL);
        marketDataWebSocketClient = isOAuth ? new MarketDataWebsocketClient(oAuthToken, baseDataURL) :
                new MarketDataWebsocketClient(keyID, secret, baseDataURL);

        LOGGER.debug(this.toString());
    }

    /**
     * Returns the {@link Account} associated with the API key.
     *
     * @return the {@link Account}
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/account/">Get the Account</a>
     */
    public Account getAccount() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ACCOUNT);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Account.class);
    }

    /**
     * Returns {@link AccountActivity} entries for many or for a specific type of activity.
     *
     * @param date          The date for which you want to see activities.
     * @param until         The response will contain only activities submitted before this date. (Cannot be used with
     *                      date.)
     * @param after         The response will contain only activities submitted after this date. (Cannot be used with
     *                      date.)
     * @param sortDirection asc or desc (default desc if unspecified.)
     * @param pageSize      The maximum number of entries to return in the response. (See the section on paging.)
     * @param pageToken     The ID of the end of your current page of results. (See the section on paging.)
     * @param activityTypes the activity types (null for all activities)
     *
     * @return the {@link AccountActivity} {@link List}
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/account-activities/">Gets the Account
     * Activities</a>
     */
    public List<AccountActivity> getAccountActivities(ZonedDateTime date, ZonedDateTime until, ZonedDateTime after,
            SortDirection sortDirection, Integer pageSize, String pageToken, ActivityType... activityTypes)
            throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ACCOUNT,
                Endpoints.ACTIVITIES);

        if (activityTypes != null) { // Check if we don't want all activity types
            if (activityTypes.length == 1) { // Get one activity
                urlBuilder.appendEndpoint(activityTypes[0].getAPIName());
            } else { // Get list of activities
                urlBuilder.appendURLParameter("activity_types", Arrays.stream(activityTypes)
                        .map(ActivityType::getAPIName).collect(Collectors.joining(","))); // Makes comma-separated list
            }
        }

        if (date != null) {
            urlBuilder.appendURLParameter(Parameters.DATE,
                    date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (until != null) {
            urlBuilder.appendURLParameter(Parameters.UNTIL,
                    until.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (after != null) {
            urlBuilder.appendURLParameter(Parameters.AFTER,
                    after.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (sortDirection != null) {
            urlBuilder.appendURLParameter(Parameters.DIRECTION, sortDirection.getAPIName());
        }

        if (pageSize != null) {
            urlBuilder.appendURLParameter(Parameters.PAGE_SIZE, pageSize.toString());
        }

        if (pageToken != null) {
            urlBuilder.appendURLParameter(Parameters.PAGE_TOKEN, pageToken);
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
     * Returns the current account configuration values.
     *
     * @return the account configurations
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/account-configuration/">Account
     * Configuration</a>
     */
    public AccountConfiguration getAccountConfiguration() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ACCOUNT,
                Endpoints.CONFIGURATIONS);

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
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/account-configuration/">Account
     * Configuration</a>
     */
    public AccountConfiguration setAccountConfiguration(AccountConfiguration accountConfiguration)
            throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(accountConfiguration);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ACCOUNT,
                Endpoints.CONFIGURATIONS);
        urlBuilder.setCustomBody(GsonUtil.GSON.toJson(accountConfiguration));

        HttpResponse<InputStream> response = alpacaRequest.invokePatch(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, AccountConfiguration.class);
    }

    /**
     * Retrieves a list of orders for the account, filtered by the supplied query parameters.
     *
     * @param status        Order status to be queried. open, closed or all. Defaults to open.
     * @param limit         The maximum number of orders in response. Defaults to 50 and max is 500.
     * @param after         The response will include only ones submitted after this timestamp (exclusive.)
     * @param until         The response will include only ones submitted until this timestamp (exclusive.)
     * @param sortDirection The chronological order of response based on the submission time. asc or desc. Defaults to
     *                      desc.
     * @param nested        If true, the result will roll up multi-leg orders under the legs field of primary order.
     * @param symbols       A list of symbols to filter the result by (null for no filter).
     *
     * @return the orders
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/orders/">Orders</a>
     */
    public ArrayList<Order> getOrders(OrderStatus status, Integer limit, ZonedDateTime after, ZonedDateTime until,
            SortDirection sortDirection, Boolean nested, List<String> symbols) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ORDERS);

        if (status != null) {
            urlBuilder.appendURLParameter(Parameters.STATUS, status.getAPIName());
        }

        if (limit != null) {
            urlBuilder.appendURLParameter(Parameters.LIMIT, limit.toString());
        }

        if (after != null) {
            urlBuilder.appendURLParameter(Parameters.AFTER,
                    after.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (until != null) {
            urlBuilder.appendURLParameter(Parameters.UNTIL,
                    until.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (sortDirection != null) {
            urlBuilder.appendURLParameter(Parameters.DIRECTION, sortDirection.getAPIName());
        }

        if (nested != null) {
            urlBuilder.appendURLParameter(Parameters.NESTED, nested.toString());
        }

        if (symbols != null && !symbols.isEmpty()) {
            urlBuilder.appendURLParameter(Parameters.SYMBOLS, String.join(",", symbols));
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type arrayListType = new TypeToken<ArrayList<Order>>() {}.getType();

        return alpacaRequest.getResponseObject(response, arrayListType);
    }

    /**
     * Places a new order for the given account. An order request may be rejected if the account is not authorized for
     * trading, or if the tradable balance is insufficient to fill the order. Note: many of the parameters for this
     * method can be set to null if they aren't required for your order type.
     *
     * @param symbol               symbol or asset ID to identify the asset to trade
     * @param quantity             number of shares to trade
     * @param side                 buy or sell
     * @param type                 market, limit, stop, or stop_limit
     * @param timeInForce          day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param limitPrice           required if type is limit or stop_limit
     * @param stopPrice            required if type is stop or stop_limit
     * @param trailPrice           this or trail_percent is required if type is trailing_stop
     * @param trailPercent         this or trail_price is required if type is trailing_stop
     * @param extendedHours        (default) false. If true, order will be eligible to execute in premarket/afterhours.
     *                             Only works with type limit and time_in_force day.
     * @param clientOrderId        A unique identifier for the order. Automatically generated if not sent.
     * @param orderClass           Simple, bracket, oco, or oto. For details of non-simple order classes, please see
     *                             Bracket Order Overview on the Alpaca Docs.
     * @param takeProfitLimitPrice Additional parameter for take-profit leg of advanced orders. Required for bracket
     *                             orders.
     * @param stopLossStopPrice    Additional parameters for stop-loss leg of advanced orders. Required for bracket
     *                             orders.
     * @param stopLossLimitPrice   Additional parameters for stop-loss leg of advanced orders. The stop-loss order
     *                             becomes a stop-limit order if specified.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/orders/">Orders</a>
     */
    public Order requestNewOrder(String symbol, Integer quantity, OrderSide side, OrderType type,
            OrderTimeInForce timeInForce, Double limitPrice, Double stopPrice, Double trailPrice, Double trailPercent,
            Boolean extendedHours, String clientOrderId, OrderClass orderClass, Double takeProfitLimitPrice,
            Double stopLossStopPrice, Double stopLossLimitPrice)
            throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(symbol);
        Preconditions.checkNotNull(quantity);
        Preconditions.checkNotNull(side);
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(timeInForce);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ORDERS);

        urlBuilder.appendJSONBodyProperty(Parameters.SYMBOL, symbol);
        urlBuilder.appendJSONBodyProperty(Parameters.QTY, quantity.toString());
        urlBuilder.appendJSONBodyProperty(Parameters.SIDE, side.getAPIName());
        urlBuilder.appendJSONBodyProperty(Parameters.TYPE, type.getAPIName());
        urlBuilder.appendJSONBodyProperty(Parameters.TIME_IN_FORCE, timeInForce.getAPIName());

        if (limitPrice != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.LIMIT_PRICE,
                    FormatUtil.toDecimalFormat(limitPrice));
        }

        if (stopPrice != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.STOP_PRICE,
                    FormatUtil.toDecimalFormat(stopPrice));
        }

        if (trailPrice != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.TRAIL_PRICE,
                    FormatUtil.toDecimalFormat(trailPrice));
        }

        if (trailPercent != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.TRAIL_PERCENT,
                    FormatUtil.toDecimalFormat(trailPercent));
        }

        if (extendedHours != null) {
            urlBuilder.appendJSONBodyJSONProperty(Parameters.EXTENDED_HOURS,
                    new JsonPrimitive(extendedHours));
        }

        if (clientOrderId != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.CLIENT_ORDER_ID, clientOrderId);
        }

        if (orderClass != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.ORDER_CLASS, orderClass.getAPIName());
        }

        if (takeProfitLimitPrice != null) {
            JsonObject takeProfit = new JsonObject();
            takeProfit.addProperty(Parameters.LIMIT_PRICE, takeProfitLimitPrice);

            urlBuilder.appendJSONBodyJSONProperty(Parameters.TAKE_PROFIT, takeProfit);
        }

        if (stopLossStopPrice != null || stopLossLimitPrice != null) {
            JsonObject stopLoss = new JsonObject();

            if (stopLossStopPrice != null) {
                stopLoss.addProperty(Parameters.STOP_PRICE, stopLossStopPrice);
            }
            if (stopLossLimitPrice != null) {
                stopLoss.addProperty(Parameters.LIMIT_PRICE, stopLossLimitPrice);
            }

            urlBuilder.appendJSONBodyJSONProperty(Parameters.STOP_LOSS, stopLoss);
        }

        HttpResponse<InputStream> response = alpacaRequest.invokePost(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Order.class);
    }

    /**
     * A market order is a request to buy or sell a security at the currently available market price. It provides the
     * most likely method of filling an order. Market orders fill nearly instantaneously. This method calls {@link
     * #requestNewOrder(String, Integer, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double,
     * Boolean, String, OrderClass, Double, Double, Double)} with {@link OrderType#MARKET}.
     *
     * @param symbol      symbol or asset ID to identify the asset to trade
     * @param quantity    number of shares to trade
     * @param side        buy or sell
     * @param timeInForce day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewMarketOrder(String symbol, Integer quantity, OrderSide side, OrderTimeInForce timeInForce)
            throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.MARKET, timeInForce, null, null, null, null, null,
                null, OrderClass.SIMPLE, null, null, null);
    }

    /**
     * A limit order is an order to buy or sell at a specified price or better. A buy limit order (a limit order to buy)
     * is executed at the specified limit price or lower (i.e., better). Conversely, a sell limit order (a limit order
     * to sell) is executed at the specified limit price or higher (better). Unlike a market order, you have to specify
     * the limit price parameter when submitting your order. This method calls {@link #requestNewOrder(String, Integer,
     * OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double,
     * Double, Double)} with {@link OrderType#LIMIT}.
     *
     * @param symbol        symbol or asset ID to identify the asset to trade
     * @param quantity      number of shares to trade
     * @param side          buy or sell
     * @param timeInForce   day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param limitPrice    required if type is limit or stop_limit
     * @param extendedHours (default) false. If true, order will be eligible to execute in premarket/afterhours. Only
     *                      works with type limit and time_in_force day.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewLimitOrder(String symbol, Integer quantity, OrderSide side, OrderTimeInForce timeInForce,
            Double limitPrice, Boolean extendedHours) throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.LIMIT, timeInForce, limitPrice, null, null, null,
                extendedHours, null, OrderClass.SIMPLE, null, null, null);
    }

    /**
     * A stop (market) order is an order to buy or sell a security when its price moves past a particular point,
     * ensuring a higher probability of achieving a predetermined entry or exit price. Once the market price crosses the
     * specified stop price, the stop order becomes a market order. Alpaca converts buy stop orders into stop limit
     * orders with a limit price that is 4% higher than a stop price &lt; $50 (or 2.5% higher than a stop price &gt;=
     * $50). Sell stop orders are not converted into stop limit orders. This method calls {@link
     * #requestNewOrder(String, Integer, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double,
     * Boolean, String, OrderClass, Double, Double, Double)} with {@link OrderType#STOP}.
     *
     * @param symbol        symbol or asset ID to identify the asset to trade
     * @param quantity      number of shares to trade
     * @param side          buy or sell
     * @param timeInForce   day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param stopPrice     required if type is stop or stop_limit
     * @param extendedHours (default) false. If true, order will be eligible to execute in premarket/afterhours. Only
     *                      works with type limit and time_in_force day.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewStopOrder(String symbol, Integer quantity, OrderSide side, OrderTimeInForce timeInForce,
            Double stopPrice, Boolean extendedHours) throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.STOP, timeInForce, null, stopPrice, null, null,
                extendedHours, null, OrderClass.SIMPLE, null, null, null);
    }

    /**
     * A stop-limit order is a conditional trade over a set time frame that combines the features of a stop order with
     * those of a limit order and is used to mitigate risk. The stop-limit order will be executed at a specified limit
     * price, or better, after a given stop price has been reached. Once the stop price is reached, the stop-limit order
     * becomes a limit order to buy or sell at the limit price or better. This method calls {@link
     * #requestNewOrder(String, Integer, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double,
     * Boolean, String, OrderClass, Double, Double, Double)} with {@link OrderType#STOP_LIMIT}.
     *
     * @param symbol        symbol or asset ID to identify the asset to trade
     * @param quantity      number of shares to trade
     * @param side          buy or sell
     * @param timeInForce   day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param limitPrice    required if type is limit or stop_limit
     * @param stopPrice     required if type is stop or stop_limit
     * @param extendedHours (default) false. If true, order will be eligible to execute in premarket/afterhours. Only
     *                      works with type limit and time_in_force day.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewStopLimitOrder(String symbol, Integer quantity, OrderSide side, OrderTimeInForce timeInForce,
            Double limitPrice, Double stopPrice, Boolean extendedHours) throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.STOP_LIMIT, timeInForce, limitPrice, stopPrice,
                null, null, extendedHours, null, OrderClass.SIMPLE, null, null, null);
    }

    /**
     * A bracket order is a chain of three orders that can be used to manage your position entry and exit. It is a
     * common use case of an OTOCO (One Triggers OCO {One Cancels Other}) order. This method calls {@link
     * #requestNewOrder(String, Integer, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double,
     * Boolean, String, OrderClass, Double, Double, Double)} with {@link OrderType#MARKET} and with parameters for a
     * bracket order.
     *
     * @param symbol               symbol or asset ID to identify the asset to trade
     * @param quantity             number of shares to trade
     * @param side                 buy or sell
     * @param timeInForce          day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param takeProfitLimitPrice Additional parameter for take-profit leg of advanced orders. Required for bracket
     *                             orders.
     * @param stopLossStopPrice    Additional parameters for stop-loss leg of advanced orders. Required for bracket
     *                             orders.
     * @param stopLossLimitPrice   Additional parameters for stop-loss leg of advanced orders. The stop-loss order
     *                             becomes a stop-limit order if specified.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewMarketBracketOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double takeProfitLimitPrice, Double stopLossStopPrice,
            Double stopLossLimitPrice) throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.MARKET, timeInForce, null, null, null, null, null,
                null, OrderClass.BRACKET, takeProfitLimitPrice, stopLossStopPrice, stopLossLimitPrice);
    }

    /**
     * A bracket order is a chain of three orders that can be used to manage your position entry and exit. It is a
     * common use case of an OTOCO (One Triggers OCO {One Cancels Other}) order. This method calls {@link
     * #requestNewOrder(String, Integer, OrderSide, OrderType, OrderTimeInForce, Double, Double, Double, Double,
     * Boolean, String, OrderClass, Double, Double, Double)} with {@link OrderType#LIMIT} and with parameters for a
     * bracket order.
     *
     * @param symbol               symbol or asset ID to identify the asset to trade
     * @param quantity             number of shares to trade
     * @param side                 buy or sell
     * @param timeInForce          day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param limitPrice           required if type is limit or stop_limit
     * @param extendedHours        (default) false. If true, order will be eligible to execute in premarket/afterhours.
     *                             Only works with type limit and time_in_force day.
     * @param takeProfitLimitPrice Additional parameter for take-profit leg of advanced orders. Required for bracket
     *                             orders.
     * @param stopLossStopPrice    Additional parameters for stop-loss leg of advanced orders. Required for bracket
     *                             orders.
     * @param stopLossLimitPrice   Additional parameters for stop-loss leg of advanced orders. The stop-loss order
     *                             becomes a stop-limit order if specified.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewLimitBracketOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double limitPrice, Boolean extendedHours, Double takeProfitLimitPrice,
            Double stopLossStopPrice, Double stopLossLimitPrice) throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.LIMIT, timeInForce, limitPrice, null, null, null,
                extendedHours, null, OrderClass.BRACKET, takeProfitLimitPrice, stopLossStopPrice, stopLossLimitPrice);
    }

    /**
     * OCO (One-Cancels-Other) is another type of advanced order type. This is a set of two orders with the same side
     * (buy/buy or sell/sell) and currently only exit order is supported. In other words, this is the second part of the
     * bracket orders where the entry order is already filled, and you can submit the take-profit and stop-loss in one
     * order submission. This method calls {@link #requestNewOrder(String, Integer, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * parameters for an OCO order type.
     *
     * @param symbol               symbol or asset ID to identify the asset to trade
     * @param quantity             number of shares to trade
     * @param side                 buy or sell
     * @param timeInForce          day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param extendedHours        (default) false. If true, order will be eligible to execute in premarket/afterhours.
     *                             Only works with type limit and time_in_force day.
     * @param takeProfitLimitPrice Additional parameter for take-profit leg of advanced orders.
     * @param stopLossStopPrice    Additional parameters for stop-loss leg of advanced orders.
     * @param stopLossLimitPrice   Additional parameters for stop-loss leg of advanced orders. The stop-loss order
     *                             becomes a stop-limit order if specified.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewOCOOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Boolean extendedHours, Double takeProfitLimitPrice,
            Double stopLossStopPrice, Double stopLossLimitPrice) throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.LIMIT, timeInForce, null, null, null, null,
                extendedHours, null, OrderClass.OCO, takeProfitLimitPrice, stopLossStopPrice, stopLossLimitPrice);
    }

    /**
     * OTO (One-Triggers-Other) is a variant of bracket order. It takes one of the take-profit or stop-loss order in
     * addition to the entry order. This method calls {@link #requestNewOrder(String, Integer, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#LIMIT} and with parameters for an OTO order type.
     *
     * @param symbol               symbol or asset ID to identify the asset to trade
     * @param quantity             number of shares to trade
     * @param side                 buy or sell
     * @param timeInForce          day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param takeProfitLimitPrice Additional parameter for take-profit leg of advanced orders.
     * @param stopLossStopPrice    Additional parameters for stop-loss leg of advanced orders.
     * @param stopLossLimitPrice   Additional parameters for stop-loss leg of advanced orders. The stop-loss order
     *                             becomes a stop-limit order if specified.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewOTOMarketOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double takeProfitLimitPrice, Double stopLossStopPrice,
            Double stopLossLimitPrice) throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.MARKET, timeInForce, null, null, null, null, null,
                null, OrderClass.OTO, takeProfitLimitPrice, stopLossStopPrice, stopLossLimitPrice);
    }

    /**
     * OTO (One-Triggers-Other) is a variant of bracket order. It takes one of the take-profit or stop-loss order in
     * addition to the entry order. This method calls {@link #requestNewOrder(String, Integer, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#LIMIT} and with parameters for an OTO order type.
     *
     * @param symbol               symbol or asset ID to identify the asset to trade
     * @param quantity             number of shares to trade
     * @param side                 buy or sell
     * @param timeInForce          day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param limitPrice           required if type is limit or stop_limit
     * @param extendedHours        (default) false. If true, order will be eligible to execute in premarket/afterhours.
     *                             Only works with type limit and time_in_force day.
     * @param takeProfitLimitPrice Additional parameter for take-profit leg of advanced orders.
     * @param stopLossStopPrice    Additional parameters for stop-loss leg of advanced orders.
     * @param stopLossLimitPrice   Additional parameters for stop-loss leg of advanced orders. The stop-loss order
     *                             becomes a stop-limit order if specified.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewOTOLimitOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double limitPrice, Boolean extendedHours, Double takeProfitLimitPrice,
            Double stopLossStopPrice, Double stopLossLimitPrice) throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.LIMIT, timeInForce, limitPrice, null, null, null,
                extendedHours, null, OrderClass.OTO, takeProfitLimitPrice, stopLossStopPrice, stopLossLimitPrice);
    }

    /**
     * OTO (One-Triggers-Other) is a variant of bracket order. It takes one of the take-profit or stop-loss order in
     * addition to the entry order. This method calls {@link #requestNewOrder(String, Integer, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#STOP} and with parameters for an OTO order type.
     *
     * @param symbol               symbol or asset ID to identify the asset to trade
     * @param quantity             number of shares to trade
     * @param side                 buy or sell
     * @param timeInForce          day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param stopPrice            required if type is stop or stop_limit
     * @param extendedHours        (default) false. If true, order will be eligible to execute in premarket/afterhours.
     *                             Only works with type limit and time_in_force day.
     * @param takeProfitLimitPrice Additional parameter for take-profit leg of advanced orders.
     * @param stopLossStopPrice    Additional parameters for stop-loss leg of advanced orders.
     * @param stopLossLimitPrice   Additional parameters for stop-loss leg of advanced orders. The stop-loss order
     *                             becomes a stop-limit order if specified.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewOTOStopOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double stopPrice, Boolean extendedHours, Double takeProfitLimitPrice,
            Double stopLossStopPrice, Double stopLossLimitPrice) throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.STOP, timeInForce, null, stopPrice, null, null,
                extendedHours, null, OrderClass.OTO, takeProfitLimitPrice, stopLossStopPrice, stopLossLimitPrice);
    }

    /**
     * OTO (One-Triggers-Other) is a variant of bracket order. It takes one of the take-profit or stop-loss order in
     * addition to the entry order. This method calls {@link #requestNewOrder(String, Integer, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#STOP_LIMIT} and with parameters for an OTO order type.
     *
     * @param symbol               symbol or asset ID to identify the asset to trade
     * @param quantity             number of shares to trade
     * @param side                 buy or sell
     * @param timeInForce          day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param limitPrice           required if type is limit or stop_limit
     * @param stopPrice            required if type is stop or stop_limit
     * @param extendedHours        (default) false. If true, order will be eligible to execute in premarket/afterhours.
     *                             Only works with type limit and time_in_force day.
     * @param takeProfitLimitPrice Additional parameter for take-profit leg of advanced orders.
     * @param stopLossStopPrice    Additional parameters for stop-loss leg of advanced orders.
     * @param stopLossLimitPrice   Additional parameters for stop-loss leg of advanced orders. The stop-loss order
     *                             becomes a stop-limit order if specified.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewOTOStopLimitOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double limitPrice, Double stopPrice, Boolean extendedHours,
            Double takeProfitLimitPrice, Double stopLossStopPrice, Double stopLossLimitPrice)
            throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.STOP_LIMIT, timeInForce, limitPrice, stopPrice,
                null, null, extendedHours, null, OrderClass.OTO, takeProfitLimitPrice,
                stopLossStopPrice, stopLossLimitPrice);
    }

    /**
     * Trailing stop orders allow you to continuously and automatically keep updating the stop price threshold based on
     * the stock price movement. This method calls {@link #requestNewOrder(String, Integer, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#TRAILING_STOP} and with parameters for a Trailing Stop Price order type.
     *
     * @param symbol        symbol or asset ID to identify the asset to trade
     * @param quantity      number of shares to trade
     * @param side          buy or sell
     * @param timeInForce   day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param trailPrice    this or trail_percent is required if type is trailing_stop
     * @param extendedHours (default) false. If true, order will be eligible to execute in premarket/afterhours. Only
     *                      works with type limit and time_in_force day.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewTrailingStopPriceOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double trailPrice, Boolean extendedHours) throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.TRAILING_STOP, timeInForce, null, null,
                trailPrice, null, extendedHours, null, null, null, null, null);
    }

    /**
     * Trailing stop orders allow you to continuously and automatically keep updating the stop price threshold based on
     * the stock price movement. This method calls {@link #requestNewOrder(String, Integer, OrderSide, OrderType,
     * OrderTimeInForce, Double, Double, Double, Double, Boolean, String, OrderClass, Double, Double, Double)} with
     * {@link OrderType#TRAILING_STOP} and with parameters for a Trailing Stop Percent order type.
     *
     * @param symbol        symbol or asset ID to identify the asset to trade
     * @param quantity      number of shares to trade
     * @param side          buy or sell
     * @param timeInForce   day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param trailPercent  this or trail_price is required if type is trailing_stop
     * @param extendedHours (default) false. If true, order will be eligible to execute in premarket/afterhours. Only
     *                      works with type limit and time_in_force day.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/trading-on-alpaca/orders/#order-types">Order Types</a>
     */
    public Order requestNewTrailingStopPercentOrder(String symbol, Integer quantity, OrderSide side,
            OrderTimeInForce timeInForce, Double trailPercent, Boolean extendedHours) throws AlpacaAPIRequestException {
        return requestNewOrder(symbol, quantity, side, OrderType.TRAILING_STOP, timeInForce, null, null,
                null, trailPercent, extendedHours, null, null, null, null, null);
    }

    /**
     * Retrieves a single order for the given order_id.
     *
     * @param orderID Order ID
     * @param nested  If true, the result will roll up multi-leg orders under the legs field of primary order.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/orders/">Orders</a>
     */
    public Order getOrder(String orderID, Boolean nested) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(orderID);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ORDERS,
                orderID);

        if (nested != null) {
            urlBuilder.appendURLParameter(Parameters.NESTED, nested.toString());
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Order.class);
    }

    /**
     * Retrieves a single order for the given client_order_id.
     *
     * @param clientOrderId the client order id
     *
     * @return the order by client id
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/orders/">Orders</a>
     */
    public Order getOrderByClientID(String clientOrderId) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(clientOrderId);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ORDERS_BY_CLIENT_ORDER_ID);
        urlBuilder.appendURLParameter(Parameters.CLIENT_ORDER_ID, clientOrderId);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Order.class);
    }

    /**
     * Replaces a single order with updated parameters. Each parameter overrides the corresponding attribute of the
     * existing order. The other attributes remain the same as the existing order.
     *
     * @param orderID       Order id
     * @param quantity      number of shares to trade
     * @param timeInForce   day, gtc, opg, cls, ioc, fok. Please see Understand Orders for more info.
     * @param limitPrice    required if type is limit or stop_limit
     * @param stopPrice     required if type is stop or stop_limit
     * @param trail         the new value of the trail_price or trail_percent value (works only for type=trailing_stop)
     * @param clientOrderId A unique identifier for the order. Automatically generated if not sent.
     *
     * @return the order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/orders/">Orders</a>
     */
    public Order replaceOrder(String orderID, Integer quantity, OrderTimeInForce timeInForce, Double limitPrice,
            Double stopPrice, Double trail, String clientOrderId) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(orderID);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ORDERS,
                orderID);

        if (quantity != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.QTY, quantity.toString());
        }

        if (timeInForce != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.TIME_IN_FORCE, timeInForce.getAPIName());
        }

        if (limitPrice != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.LIMIT_PRICE,
                    FormatUtil.toDecimalFormat(limitPrice));
        }

        if (stopPrice != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.STOP_PRICE,
                    FormatUtil.toDecimalFormat(stopPrice));
        }

        if (trail != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.TRAIL,
                    FormatUtil.toDecimalFormat(trail));
        }

        if (clientOrderId != null) {
            urlBuilder.appendJSONBodyProperty(Parameters.CLIENT_ORDER_ID, clientOrderId);
        }

        HttpResponse<InputStream> response = alpacaRequest.invokePatch(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Order.class);
    }

    /**
     * Attempts to cancel all open orders. A response will be provided for each order that is attempted to be cancelled.
     * If an order is no longer cancelable, the server will respond with status 500 and reject the request.
     *
     * @return the array list of cancelled orders
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/orders/">Orders</a>
     */
    public ArrayList<CancelledOrder> cancelAllOrders() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ORDERS);

        HttpResponse<InputStream> response = alpacaRequest.invokeDelete(urlBuilder);

        if (response.getStatus() != 207) { // This returns a 207 multi-status message
            throw new AlpacaAPIRequestException(response);
        }

        Type arrayListType = new TypeToken<ArrayList<CancelledOrder>>() {}.getType();

        return alpacaRequest.getResponseObject(response, arrayListType);
    }

    /**
     * Attempts to cancel an open order. If the order is no longer cancelable (example: status=order_filled), the server
     * will respond with status 422, and reject the request.
     *
     * @param orderId Order ID
     *
     * @return true, if successful
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/orders/">Orders</a>
     */
    public boolean cancelOrder(String orderId) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(orderId);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ORDERS,
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
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/positions/">Positions</a>
     */
    public ArrayList<Position> getOpenPositions() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.POSITIONS);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type arrayListType = new TypeToken<ArrayList<Position>>() {}.getType();

        return alpacaRequest.getResponseObject(response, arrayListType);
    }

    /**
     * Gets the open position by symbol.
     *
     * @param symbol the symbol or asset_id (required)
     *
     * @return the open position by symbol
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/positions/">Positions</a>
     */
    public Position getOpenPositionBySymbol(String symbol) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(symbol);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.POSITIONS,
                symbol.trim());

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Position.class);
    }

    /**
     * Closes (liquidates) all of the account’s open long and short positions. A response will be provided for each
     * order that is attempted to be cancelled. If an order is no longer cancelable, the server will respond with status
     * 500 and reject the request.
     *
     * @return a list of closing position orders
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/positions/">Positions</a>
     */
    public ArrayList<ClosePositionOrder> closeAllPositions() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.POSITIONS);

        HttpResponse<InputStream> response = alpacaRequest.invokeDelete(urlBuilder);

        if (response.getStatus() != 207) { // This returns a 207 multi-status message
            throw new AlpacaAPIRequestException(response);
        }

        Type arrayListType = new TypeToken<ArrayList<ClosePositionOrder>>() {}.getType();

        return alpacaRequest.getResponseObject(response, arrayListType);
    }

    /**
     * Closes (liquidates) the account’s open position for the given symbol. Works for both long and short positions.
     *
     * @param symbol symbol or asset_id
     *
     * @return a closing position order
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/positions/">Positions</a>
     */
    public Order closePosition(String symbol) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(symbol);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.POSITIONS,
                symbol);

        HttpResponse<InputStream> response = alpacaRequest.invokeDelete(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Order.class);
    }

    /**
     * Get a list of assets.
     *
     * @param assetStatus e.g. “active”. By default, all statuses are included.
     * @param assetClass  Defaults to us_equity.
     *
     * @return the assets
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/assets/">Assets</a>
     */
    public ArrayList<Asset> getAssets(AssetStatus assetStatus, String assetClass) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ASSETS);

        if (assetStatus != null) {
            urlBuilder.appendURLParameter(Parameters.STATUS, assetStatus.getAPIName());
        }

        if (assetClass != null) {
            urlBuilder.appendURLParameter(Parameters.ASSET_CLASS, assetClass.trim());
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type arrayListType = new TypeToken<ArrayList<Asset>>() {}.getType();

        return alpacaRequest.getResponseObject(response, arrayListType);
    }

    /**
     * Gets the asset by symbol or asset_id.
     *
     * @param symbolOrAssetID the symbol or asset_id (required)
     *
     * @return the asset by symbol
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/assets/">Assets</a>
     */
    public Asset getAssetBySymbol(String symbolOrAssetID) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(symbolOrAssetID);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ASSETS,
                symbolOrAssetID.trim());

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Asset.class);
    }

    /**
     * Returns the list of watchlists registered under the account.
     *
     * @return the watch lists
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/">Watchlists</a>
     */
    public ArrayList<Watchlist> getWatchlists() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.WATCHLISTS);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type arrayListType = new TypeToken<ArrayList<Watchlist>>() {}.getType();
        return alpacaRequest.getResponseObject(response, arrayListType);
    }

    /**
     * Create a new watchlist with initial set of assets.
     *
     * @param name    arbitrary name string, up to 64 characters
     * @param symbols set of symbol string
     *
     * @return the created watchlist
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/">Watchlists</a>
     */
    public Watchlist createWatchlist(String name, String... symbols) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(name);
        Preconditions.checkState(name.length() <= 64);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.WATCHLISTS);

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

        return alpacaRequest.getResponseObject(response, Watchlist.class);
    }

    /**
     * Returns a watchlist identified by the ID.
     *
     * @param watchlistID Watchlist ID
     *
     * @return the watchlist
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/">Watchlists</a>
     */
    public Watchlist getWatchlist(String watchlistID) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(watchlistID);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.WATCHLISTS,
                watchlistID);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Watchlist.class);
    }

    /**
     * Update the name and/or content of watchlist.
     *
     * @param watchlistID Watchlist ID
     * @param name        the new watchlist name
     * @param symbols     the new list of symbol names to replace the watchlist content
     *
     * @return the updated watchlist
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/">Watchlists</a>
     */
    public Watchlist updateWatchlist(String watchlistID, String name, String... symbols)
            throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(watchlistID);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.WATCHLISTS,
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
     * Append an asset for the symbol to the end of watchlist asset list
     *
     * @param watchlistID Watchlist ID
     * @param symbol      the symbol name to add to the watchlist
     *
     * @return the watchlist
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/">Watchlists</a>
     */
    public Watchlist addWatchlistAsset(String watchlistID, String symbol) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(watchlistID);
        Preconditions.checkNotNull(symbol);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.WATCHLISTS,
                watchlistID);

        urlBuilder.appendJSONBodyProperty("symbol", symbol);

        HttpResponse<InputStream> response = alpacaRequest.invokePost(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Watchlist.class);
    }

    /**
     * Delete a watchlist. This is a permanent deletion.
     *
     * @param watchlistID Watchlist ID
     *
     * @return if the watchlist was deleted
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/">Watchlists</a>
     */
    public boolean deleteWatchlist(String watchlistID) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(watchlistID);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.WATCHLISTS,
                watchlistID);

        HttpResponse<InputStream> response = alpacaRequest.invokeDelete(urlBuilder);

        if ((response.getStatus() != 200 && response.getStatus() != 204)) {
            throw new AlpacaAPIRequestException(response);
        }

        return response.getStatus() == 200 || response.getStatus() == 204;
    }

    /**
     * Delete one entry for an asset by symbol name.
     *
     * @param watchlistID Watchlist ID
     * @param symbol      symbol name to remove from the watchlist content
     *
     * @return the updated watchlist
     *
     * @throws AlpacaAPIRequestException the alpaca api exception
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/watchlist/">Watchlists</a>
     */
    public Watchlist removeSymbolFromWatchlist(String watchlistID, String symbol) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(watchlistID);
        Preconditions.checkNotNull(symbol);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.WATCHLISTS,
                watchlistID,
                symbol);

        HttpResponse<InputStream> response = alpacaRequest.invokeDelete(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Watchlist.class);
    }

    /**
     * Returns timeseries data about equity and profit/loss (P/L) of the account in requested timespan.
     *
     * @param periodLength  The duration of the data in "number + unit", such as 1D, where unit can be D for day, W for
     *                      week, M for month and A for year. Defaults to 1M. This parameter is the number.
     * @param periodUnit    The duration of the data in "number + unit", such as 1D, where unit can be D for day, W for
     *                      week, M for month and A for year. Defaults to 1M. This parameter is the unit.
     * @param timeFrame     The resolution of time window. 1Min, 5Min, 15Min, 1H, or 1D. If omitted, 1Min for less than
     *                      7 days period, 15Min for less than 30 days, or otherwise 1D.
     * @param dateEnd       The date the data is returned up to, in "YYYY-MM-DD" format. Defaults to the current market
     *                      date (rolls over at the market open if extended_hours is false, otherwise at 7am ET)
     * @param extendedHours If true, include extended hours in the result. This is effective only for timeframe less
     *                      than 1D.
     *
     * @return the portfolio history
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    public PortfolioHistory getPortfolioHistory(Integer periodLength, PortfolioPeriodUnit periodUnit,
            PortfolioTimeFrame timeFrame, LocalDate dateEnd, Boolean extendedHours) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.ACCOUNT,
                Endpoints.PORTFOLIO,
                Endpoints.HISTORY);

        if (periodLength != null && periodUnit != null) {
            urlBuilder.appendURLParameter(Parameters.PERIOD, periodLength + periodUnit.getAPIName());
        }

        if (timeFrame != null) {
            urlBuilder.appendURLParameter(Parameters.TIMEFRAME, timeFrame.getAPIName());
        }

        if (dateEnd != null) {
            urlBuilder.appendURLParameter(Parameters.DATE_END, FormatUtil.toDateString(dateEnd));
        }

        if (extendedHours != null) {
            urlBuilder.appendURLParameter(Parameters.EXTENDED_HOURS, extendedHours.toString());
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, PortfolioHistory.class);
    }

    /**
     * Returns the market {@link Calendar} from 1970 to 2029.
     *
     * @return the {@link Calendar} {@link List}
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/calendar/">Calendar</a>
     */
    public List<Calendar> getCalendar() throws AlpacaAPIRequestException {
        return getCalendar(null, null);
    }

    /**
     * Returns the market {@link Calendar}.
     *
     * @param start The first date to retrieve data for (inclusive)
     * @param end   The last date to retrieve data for (inclusive)
     *
     * @return the {@link Calendar} {@link List}
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/calendar/">Calendar</a>
     */
    public List<Calendar> getCalendar(LocalDate start, LocalDate end) throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2, Endpoints.CALENDAR);

        if (start != null) {
            urlBuilder.appendURLParameter(Parameters.START, FormatUtil.toDateString(start));
        }

        if (end != null) {
            urlBuilder.appendURLParameter(Parameters.END, FormatUtil.toDateString(end));
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type arrayListType = new TypeToken<ArrayList<Calendar>>() {}.getType();

        return alpacaRequest.getResponseObject(response, arrayListType);
    }

    /**
     * Returns the market {@link Clock}.
     *
     * @return the {@link Clock}
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/clock/">Clock</a>
     */
    public Clock getClock() throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAPIURL, Endpoints.VERSION_2,
                Endpoints.CLOCK);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, Clock.class);
    }

    /**
     * Retrieves a list of bars for each requested symbol. It is guaranteed all bars are in ascending order by time.
     * <p>
     * Currently, no “incomplete” bars are returned. For example, a 1 minute bar for 09:30 will not be returned until
     * 09:31.
     *
     * @param timeframe One of minute, 1Min, 5Min, 15Min, day or 1D. minute is an alias of 1Min. Similarly, day is of
     *                  1D.
     * @param symbol    One symbol name.
     * @param limit     The maximum number of bars to be returned for each symbol. It can be between 1 and 1000. Default
     *                  is 100 if parameter is unspecified or 0.
     * @param start     Filter bars equal to or after this time. Cannot be used with after.
     * @param end       Filter bars equal to or before this time. Cannot be used with until.
     * @param after     Filter bars after this time. Cannot be used with start.
     * @param until     Filter bars before this time. Cannot be used with end.
     *
     * @return An object with a key for each symbol and the Bars object as the values. Note that it returns status 200
     * with an empty object if no requested symbol is found.
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/market-data/bars/">Bars</a>
     */
    public Map<String, List<Bar>> getBars(BarsTimeFrame timeframe, String symbol, Integer limit, ZonedDateTime start,
            ZonedDateTime end, ZonedDateTime after, ZonedDateTime until)
            throws AlpacaAPIRequestException {
        return this.getBars(timeframe, new String[]{symbol}, limit, start, end, after, until);
    }

    /**
     * Retrieves a list of bars for each requested symbol. It is guaranteed all bars are in ascending order by time.
     * <p>
     * Currently, no “incomplete” bars are returned. For example, a 1 minute bar for 09:30 will not be returned until
     * 09:31.
     *
     * @param timeframe One of minute, 1Min, 5Min, 15Min, day or 1D. minute is an alias of 1Min. Similarly, day is of
     *                  1D.
     * @param symbols   One or more (max 200) symbol names split by commas (“,”).
     * @param limit     The maximum number of bars to be returned for each symbol. It can be between 1 and 1000. Default
     *                  is 100 if parameter is unspecified or 0.
     * @param start     Filter bars equal to or after this time. Cannot be used with after.
     * @param end       Filter bars equal to or before this time. Cannot be used with until.
     * @param after     Filter bars after this time. Cannot be used with start.
     * @param until     Filter bars before this time. Cannot be used with end.
     *
     * @return @return An object with a key for each symbol and the Bars object as the values. Note that it returns
     * status 200 with an empty object if no requested symbol is found.
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/market-data/bars/">Bars</a>
     */
    public Map<String, List<Bar>> getBars(BarsTimeFrame timeframe, String[] symbols, Integer limit, ZonedDateTime start,
            ZonedDateTime end, ZonedDateTime after, ZonedDateTime until)
            throws AlpacaAPIRequestException {
        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseDataUrl, Endpoints.VERSION_1,
                Endpoints.BARS);

        if (timeframe != null) {
            urlBuilder.appendEndpoint(timeframe.getAPIName());
        }

        if (symbols != null) {
            urlBuilder.appendURLParameter(Parameters.SYMBOLS, String.join(",", symbols));
        }

        if (limit != null) {
            urlBuilder.appendURLParameter(Parameters.LIMIT, limit.toString());
        }

        if (start != null) {
            urlBuilder.appendURLParameter(Parameters.START,
                    start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (end != null) {
            urlBuilder.appendURLParameter(Parameters.END,
                    end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (after != null) {
            urlBuilder.appendURLParameter(Parameters.AFTER,
                    after.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (until != null) {
            urlBuilder.appendURLParameter(Parameters.UNTIL,
                    until.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        Type mapType = new TypeToken<Map<String, ArrayList<Bar>>>() {}.getType();

        return alpacaRequest.getResponseObject(response, mapType);
    }

    /**
     * Retrieves the last trade for the requested symbol.
     *
     * @param symbol A stock ticker symbol to retrieve the last trade of
     *
     * @return the last trade response object
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/last-trade/">Last Trade</a>
     */
    public LastTradeResponse getLastTrade(String symbol) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(symbol);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseDataUrl, Endpoints.VERSION_1,
                Endpoints.LAST,
                Endpoints.STOCKS,
                symbol);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, LastTradeResponse.class);
    }

    /**
     * Retrieves the last quote for the requested symbol.
     *
     * @param symbol A stock ticker symbol to retrieve the last trade of
     *
     * @return the last quote response object
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @see <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/last-quote/">Last Quote</a>
     */
    public LastQuoteResponse getLastQuote(String symbol) throws AlpacaAPIRequestException {
        Preconditions.checkNotNull(symbol);

        AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseDataUrl, Endpoints.VERSION_1,
                Endpoints.LAST_QUOTE,
                Endpoints.STOCKS,
                symbol);

        HttpResponse<InputStream> response = alpacaRequest.invokeGet(urlBuilder);

        if (response.getStatus() != 200) {
            throw new AlpacaAPIRequestException(response);
        }

        return alpacaRequest.getResponseObject(response, LastQuoteResponse.class);
    }

    /**
     * Adds the {@link AlpacaStreamListener}. Note that when the first {@link AlpacaStreamListener} is added, the
     * Websocket connection is created.
     *
     * @param streamListener the stream listener
     *
     * @throws WebsocketException thrown for {@link WebsocketException}s
     */
    public void addAlpacaStreamListener(AlpacaStreamListener streamListener) throws WebsocketException {
        alpacaWebSocketClient.addListener(streamListener);
    }

    /**
     * Removes the {@link AlpacaStreamListener}. Note that when the last {@link AlpacaStreamListener} is removed, the
     * Websocket connection is closed.
     *
     * @param streamListener the {@link AlpacaStreamListener}
     *
     * @throws WebsocketException thrown for {@link WebsocketException}s
     */
    public void removeAlpacaStreamListener(AlpacaStreamListener streamListener) throws WebsocketException {
        alpacaWebSocketClient.removeListener(streamListener);
    }

    /**
     * Adds the {@link MarketDataStreamListener}. Note that when the first {@link AlpacaStreamListener} is added, the
     * Websocket connection is created.
     *
     * @param streamListener the {@link MarketDataStreamListener}
     *
     * @throws WebsocketException thrown for {@link WebsocketException}s
     */
    public void addMarketDataStreamListener(MarketDataStreamListener streamListener) throws WebsocketException {
        marketDataWebSocketClient.addListener(streamListener);
    }

    /**
     * Removes the {@link MarketDataStreamListener}. Note that when the last {@link MarketDataStreamListener} is
     * removed, the Websocket connection is closed.
     *
     * @param streamListener the {@link MarketDataStreamListener}
     *
     * @throws WebsocketException thrown for {@link WebsocketException}s
     */
    public void removeMarketDataStreamListener(MarketDataStreamListener streamListener) throws WebsocketException {
        marketDataWebSocketClient.removeListener(streamListener);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("baseAPIURL = " + baseAPIURL)
                .add("baseDataUrl = " + baseDataUrl)
                .add("keyID = " + keyID)
                .toString();
    }
}
