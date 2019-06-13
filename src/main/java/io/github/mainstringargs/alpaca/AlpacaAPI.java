package io.github.mainstringargs.alpaca;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import io.github.mainstringargs.alpaca.domain.Account;
import io.github.mainstringargs.alpaca.domain.Asset;
import io.github.mainstringargs.alpaca.domain.Bar;
import io.github.mainstringargs.alpaca.domain.Calendar;
import io.github.mainstringargs.alpaca.domain.Clock;
import io.github.mainstringargs.alpaca.domain.Order;
import io.github.mainstringargs.alpaca.domain.Position;
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
import io.github.mainstringargs.alpaca.rest.exceptions.AlpacaAPIException;
import io.github.mainstringargs.alpaca.websocket.AlpacaStreamListener;
import io.github.mainstringargs.alpaca.websocket.AlpacaWebsocketClient;

/**
 * The Class AlpacaAPI.
 */
public class AlpacaAPI {

  /** The key id. */
  private final String keyId;

  /** The secret. */
  private final String secret;

  /** The base account url. */
  private final String baseAccountUrl;

  /** The alpaca request. */
  private final AlpacaRequest alpacaRequest;

  /** The base data url. */
  private final String baseDataUrl;

  /** The logger. */
  private static Logger LOGGER = LogManager.getLogger(AlpacaAPI.class);

  /** The alpaca web socket client. */
  private final AlpacaWebsocketClient alpacaWebSocketClient;

  /**
   * Instantiates a new Alpaca API using properties specified in alpaca.properties file (or relevant
   * defaults)
   */
  public AlpacaAPI() {

    keyId = AlpacaProperties.KEY_ID_VALUE;
    secret = AlpacaProperties.SECRET_VALUE;
    baseAccountUrl = AlpacaProperties.BASE_ACCOUNT_URL_VALUE;
    baseDataUrl = AlpacaProperties.BASE_DATA_URL_VALUE;

    alpacaRequest = new AlpacaRequest(keyId, secret);
    alpacaWebSocketClient = new AlpacaWebsocketClient(keyId, secret, baseAccountUrl);

  }

  /**
   * Instantiates a new Alpaca API using the specified keyId, secret, baseAccountUrl, and
   * baseDataUrl.
   *
   * @param keyId the key id
   * @param secret the secret
   * @param baseAccountUrl the base account url
   * @param baseDataUrl the base data url
   */
  public AlpacaAPI(String keyId, String secret, String baseAccountUrl, String baseDataUrl) {
    this.keyId = keyId;
    this.secret = secret;
    this.baseAccountUrl = baseAccountUrl;
    this.baseDataUrl = baseDataUrl;
    alpacaRequest = new AlpacaRequest(keyId, secret);
    alpacaWebSocketClient = new AlpacaWebsocketClient(keyId, secret, baseAccountUrl);

  }

  /**
   * Instantiates a new Alpaca API using the specified keyId, secret, and baseAccountUrl.
   *
   * @param keyId the key id
   * @param secret the secret
   * @param baseAccountUrl the base account url
   */
  public AlpacaAPI(String keyId, String secret, String baseAccountUrl) {
    this.keyId = keyId;
    this.secret = secret;
    this.baseAccountUrl = baseAccountUrl;
    baseDataUrl = AlpacaProperties.BASE_DATA_URL_VALUE;
    alpacaRequest = new AlpacaRequest(keyId, secret);
    alpacaWebSocketClient = new AlpacaWebsocketClient(keyId, secret, baseAccountUrl);

  }
  
  /**
   * Instantiates a new Alpaca API using the specified keyId, and secret.
   *
   * @param keyId the key id
   * @param secret the secret
   */
  public AlpacaAPI(String keyId, String secret) {
    this.keyId = keyId;
    this.secret = secret;
    this.baseAccountUrl = AlpacaProperties.BASE_ACCOUNT_URL_VALUE;
    baseDataUrl = AlpacaProperties.BASE_DATA_URL_VALUE;
    alpacaRequest = new AlpacaRequest(keyId, secret);
    alpacaWebSocketClient = new AlpacaWebsocketClient(keyId, secret, baseAccountUrl);

  }

  /**
   * Gets the account.
   *
   * @return the account
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/account/#get-the-account">https://docs.alpaca.markets/api-documentation/web-api/account/#get-the-account</a>
   */
  public Account getAccount() throws AlpacaAPIException {
    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.ACCOUNT_ENDPOINT);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }

    Account account = alpacaRequest.getResponseObject(response, Account.class);

    return account;
  }


  /**
   * Gets the orders using the following API defaults:.
   *
   * @return the orders
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders</a>
   * 
   *      Status: Open Limit: 50 Direction: Descending
   */
  public List<Order> getOrders() throws AlpacaAPIException {
    Type listType = new TypeToken<List<Order>>() {}.getType();

    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.ORDERS_ENDPOINT);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    List<Order> orders = alpacaRequest.getResponseObject(response, listType);

    return orders;
  }

  /**
   * Gets the orders.
   *
   * @param status the status (Open if null)
   * @param limit the limit (50 if null)
   * @param after the after
   * @param until the until
   * @param direction the direction (Descending if null)
   * @return the orders
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders</a>
   */
  public List<Order> getOrders(OrderStatus status, Integer limit, LocalDateTime after,
      LocalDateTime until, Direction direction) throws AlpacaAPIException {
    Type listType = new TypeToken<List<Order>>() {}.getType();

    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.ORDERS_ENDPOINT);

    if (status != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.STATUS_PARAMETER, status.getAPIName());
    }

    if (limit != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.LIMIT_PARAMETER, limit.toString());
    }

    if (after != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.AFTER_PARAMETER,
          Utilities.toDateTimeString(after));
    }

    if (until != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.UNTIL_PARAMETER,
          Utilities.toDateTimeString(until));
    }

    if (direction != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.DIRECTION_PARAMETER, direction.getAPIName());
    }

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    List<Order> orders = alpacaRequest.getResponseObject(response, listType);

    return orders;
  }

  /**
   * Request/Submits new order.
   *
   * @param symbol the symbol or asset_id (required)
   * @param quantity the quantity (required)
   * @param side the side (required)
   * @param type the type (required)
   * @param timeInForce the time in force (required)
   * @param limitPrice the limit price (required if Limit or Stop Limit)
   * @param stopPrice the stop price (required if Stop or Stop Limit)
   * @param clientOrderId the client order id (optional)
   * @return the order
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/orders/#request-a-new-order">https://docs.alpaca.markets/api-documentation/web-api/orders/#request-a-new-order</a>
   */
  public Order requestNewOrder(String symbol, Integer quantity, OrderSide side, OrderType type,
      OrderTimeInForce timeInForce, Double limitPrice, Double stopPrice, String clientOrderId)
      throws AlpacaAPIException {

    Type objectType = new TypeToken<Order>() {}.getType();

    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.ORDERS_ENDPOINT);

    if (symbol != null) {
      urlBuilder.appendBodyProperty(AlpacaConstants.SYMBOL_PARAMETER, symbol);
    }

    if (quantity != null) {
      urlBuilder.appendBodyProperty(AlpacaConstants.QTY_PARAMETER, quantity.toString());
    }

    if (side != null) {
      urlBuilder.appendBodyProperty(AlpacaConstants.SIDE_PARAMETER, side.getAPIName());
    }

    if (type != null) {
      urlBuilder.appendBodyProperty(AlpacaConstants.TYPE_PARAMETER, type.getAPIName());
    }

    if (timeInForce != null) {
      urlBuilder.appendBodyProperty(AlpacaConstants.TIME_IN_FORCE_PARAMETER,
          timeInForce.getAPIName());
    }

    if (limitPrice != null) {
      urlBuilder.appendBodyProperty(AlpacaConstants.LIMIT_PRICE_PARAMETER,
          Utilities.toDecimalFormat(limitPrice));
    }

    if (stopPrice != null) {
      urlBuilder.appendBodyProperty(AlpacaConstants.STOP_PRICE_PARAMETER,
          Utilities.toDecimalFormat(stopPrice));
    }

    if (clientOrderId != null) {
      urlBuilder.appendBodyProperty(AlpacaConstants.CLIENT_ORDER_ID_PARAMETER, clientOrderId);
    }

    HttpResponse<JsonNode> response = alpacaRequest.invokePost(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    Order order = alpacaRequest.getResponseObject(response, objectType);

    return order;
  }

  /**
   * Gets the order.
   *
   * @param orderId the order id (required)
   * @return the order
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order</a>
   */
  public Order getOrder(String orderId) throws AlpacaAPIException {
    Type objectType = new TypeToken<Order>() {}.getType();


    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.ORDERS_ENDPOINT);

    if (orderId != null) {
      urlBuilder.appendEndpoint(orderId);
    }

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    Order order = alpacaRequest.getResponseObject(response, objectType);

    return order;
  }

  /**
   * Gets the order by client id.
   *
   * @param clientOrderId the client order id (required)
   * @return the order by client id
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order-by-client-order-id">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order-by-client-order-id</a>
   */
  public Order getOrderByClientId(String clientOrderId) throws AlpacaAPIException {
    Type objectType = new TypeToken<Order>() {}.getType();

    AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(baseAccountUrl,
        AlpacaConstants.ORDERS_BY_CLIENT_ORDER_ID_ENDPOINT);

    if (clientOrderId != null) {

      try {
        clientOrderId = URLEncoder.encode(clientOrderId, AlpacaConstants.UTF_ENCODING);
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }

      urlBuilder.appendURLParameter(AlpacaConstants.CLIENT_ORDER_ID_PARAMETER, clientOrderId);
    }

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    Order order = alpacaRequest.getResponseObject(response, objectType);

    return order;
  }

  /**
   * Cancel order.
   *
   * @param orderId the order id (required)
   * @return true, if successful
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/orders/#cancel-an-order">https://docs.alpaca.markets/api-documentation/web-api/orders/#cancel-an-order</a>
   */
  public boolean cancelOrder(String orderId) throws AlpacaAPIException {

    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.ORDERS_ENDPOINT);

    if (orderId != null) {
      urlBuilder.appendEndpoint(orderId);
    }

    HttpResponse<JsonNode> response = alpacaRequest.invokeDelete(urlBuilder);

    if ((response.getStatus() != 200 && response.getStatus() != 204)) {
      throw new AlpacaAPIException(response);
    }


    return response.getStatus() == 200 || response.getStatus() == 204;
  }

  /**
   * Gets the open positions.
   *
   * @return the open positions
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/positions/#get-open-positions">https://docs.alpaca.markets/api-documentation/web-api/positions/#get-open-positions</a>
   */
  public List<Position> getOpenPositions() throws AlpacaAPIException {
    Type listType = new TypeToken<List<Position>>() {}.getType();

    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.POSITIONS_ENDPOINT);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    List<Position> positions = alpacaRequest.getResponseObject(response, listType);

    return positions;
  }


  /**
   * Gets the open position by symbol.
   *
   * @param symbol the symbol or asset_id (required)
   * @return the open position by symbol
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/positions/#get-an-open-position">https://docs.alpaca.markets/api-documentation/web-api/positions/#get-an-open-position</a>
   */
  public Position getOpenPositionBySymbol(String symbol) throws AlpacaAPIException {
    Type listType = new TypeToken<Position>() {}.getType();

    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.POSITIONS_ENDPOINT);


    if (symbol != null) {
      urlBuilder.appendEndpoint(symbol.trim());
    }

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    Position position = alpacaRequest.getResponseObject(response, listType);

    return position;
  }

  /**
   * Gets the assets using the following API defaults:
   * 
   * Status: Active Asset Class: us_equity.
   *
   * @return the assets
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets">https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets</a>
   */
  public List<Asset> getAssets() throws AlpacaAPIException {
    Type listType = new TypeToken<List<Asset>>() {}.getType();


    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.ASSETS_ENDPOINT);


    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    List<Asset> assets = alpacaRequest.getResponseObject(response, listType);

    return assets;
  }


  /**
   * Gets the assets.
   *
   * @param assetStatus the asset status (Active if null)
   * @param assetClass the asset class (us_equity if null)
   * @return the assets
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets">https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets</a>
   */
  public List<Asset> getAssets(AssetStatus assetStatus, String assetClass)
      throws AlpacaAPIException {
    Type listType = new TypeToken<List<Asset>>() {}.getType();

    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.ASSETS_ENDPOINT);

    if (assetStatus != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.STATUS_PARAMETER, assetStatus.getAPIName());
    }

    if (assetClass != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.ASSET_CLASS_PARAMETER, assetClass.trim());
    }

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    List<Asset> assets = alpacaRequest.getResponseObject(response, listType);

    return assets;
  }

  /**
   * Gets the asset by symbol or asset_id.
   *
   * @param symbol the symbol (required)
   * @return the asset by symbol
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/assets/#get-an-asset">https://docs.alpaca.markets/api-documentation/web-api/assets/#get-an-asset</a>
   */
  public Asset getAssetBySymbol(String symbol) throws AlpacaAPIException {
    Type listType = new TypeToken<Asset>() {}.getType();

    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.ASSETS_ENDPOINT);

    if (symbol != null) {
      urlBuilder.appendEndpoint(symbol.trim());
    }

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }

    Asset asset = alpacaRequest.getResponseObject(response, listType);

    return asset;
  }

  /**
   * Gets the calendar.
   *
   * @return the calendar
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar">https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar</a>
   */
  public List<Calendar> getCalendar() throws AlpacaAPIException {
    Type listType = new TypeToken<List<Calendar>>() {}.getType();

    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.CALENDAR_ENDPOINT);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }

    List<Calendar> calendar = alpacaRequest.getResponseObject(response, listType);

    return calendar;
  }

  /**
   * Gets the calendar.
   *
   * @param start the start
   * @param end the end
   * @return the calendar
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar">https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar</a>
   */
  public List<Calendar> getCalendar(LocalDate start, LocalDate end) throws AlpacaAPIException {
    Type listType = new TypeToken<List<Calendar>>() {}.getType();

    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.CALENDAR_ENDPOINT);

    if (start != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.START_PARAMETER, Utilities.toDateString(start));
    }

    if (end != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.END_PARAMETER, Utilities.toDateString(end));
    }

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    List<Calendar> calendar = alpacaRequest.getResponseObject(response, listType);

    return calendar;
  }

  /**
   * Gets the clock.
   *
   * @return the clock
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/clock/#get-the-clock">https://docs.alpaca.markets/api-documentation/web-api/clock/#get-the-clock</a>
   */
  public Clock getClock() throws AlpacaAPIException {
    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseAccountUrl, AlpacaConstants.CLOCK_ENDPOINT);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    Clock clock = alpacaRequest.getResponseObject(response, Clock.class);

    return clock;
  }


  /**
   * Gets the bars.
   *
   * @param timeframe the timeframe (required)
   * @param symbols One or more (max 200) symbol names (required)
   * @param limit The maximum number of bars to be returned for each symbol. It can be between 1 and
   *        1000. Default is 100 if parameter is unspecified or 0. (100 if null)
   * @param start Filter bars equal to or after this time. Cannot be used with after.
   * @param end Filter bars equal to or before this time. Cannot be used with until.
   * @param after Filter bars after this time. Cannot be used with start.
   * @param until Filter bars before this time. Cannot be used with end.
   * @return the bars
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars">https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars</a>
   */
  public Map<String, List<Bar>> getBars(BarsTimeFrame timeframe, String[] symbols, Integer limit,
      LocalDateTime start, LocalDateTime end, LocalDateTime after, LocalDateTime until)
      throws AlpacaAPIException {

    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseDataUrl, AlpacaConstants.BARS_ENDPOINT);

    if (timeframe != null) {
      urlBuilder.appendEndpoint(timeframe.getAPIName());
    }

    if (start != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.START_PARAMETER,
          Utilities.toDateTimeString(start));
    }

    if (end != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.END_PARAMETER, Utilities.toDateTimeString(end));
    }

    if (after != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.AFTER_PARAMETER,
          Utilities.toDateTimeString(after));
    }

    if (until != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.UNTIL_PARAMETER,
          Utilities.toDateTimeString(until));
    }

    if (limit != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.LIMIT_PARAMETER, limit.toString());
    }

    if (symbols != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.SYMBOLS_PARAMETER, String.join(",", symbols));
    }

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    Type mapType = new TypeToken<Map<String, List<Bar>>>() {}.getType();

    Map<String, List<Bar>> bars = alpacaRequest.getResponseObject(response, mapType);

    return bars;
  }

  /**
   * Gets the bars.
   *
   * @param timeframe the timeframe (required)
   * @param symbol One symbol name (required)
   * @param limit The maximum number of bars to be returned for each symbol. It can be between 1 and
   *        1000. Default is 100 if parameter is unspecified or 0. (100 if null)
   * @param start Filter bars equal to or after this time. Cannot be used with after.
   * @param end Filter bars equal to or before this time. Cannot be used with until.
   * @param after Filter bars after this time. Cannot be used with start.
   * @param until Filter bars before this time. Cannot be used with end.
   * @return the bars
   * @throws AlpacaAPIException the alpaca API exception
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars">https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars</a>
   */
  public List<Bar> getBars(BarsTimeFrame timeframe, String symbol, Integer limit,
      LocalDateTime start, LocalDateTime end, LocalDateTime after, LocalDateTime until)
      throws AlpacaAPIException {
    AlpacaRequestBuilder urlBuilder =
        new AlpacaRequestBuilder(baseDataUrl, AlpacaConstants.BARS_ENDPOINT);

    if (timeframe != null) {
      urlBuilder.appendEndpoint(timeframe.getAPIName());
    }

    if (start != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.START_PARAMETER,
          Utilities.toDateTimeString(start));
    }

    if (end != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.END_PARAMETER, Utilities.toDateTimeString(end));
    }

    if (after != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.AFTER_PARAMETER,
          Utilities.toDateTimeString(after));
    }

    if (until != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.UNTIL_PARAMETER,
          Utilities.toDateTimeString(until));
    }

    if (limit != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.LIMIT_PARAMETER, limit.toString());
    }

    if (symbol != null) {
      urlBuilder.appendURLParameter(AlpacaConstants.SYMBOLS_PARAMETER, String.join(",", symbol));
    }

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
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
}
