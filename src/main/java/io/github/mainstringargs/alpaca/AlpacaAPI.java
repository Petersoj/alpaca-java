package io.github.mainstringargs.alpaca;

import java.lang.reflect.Type;
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
import io.github.mainstringargs.alpaca.rest.accounts.GetAccountRequestBuilder;
import io.github.mainstringargs.alpaca.rest.assets.GetAssetBySymbolRequestBuilder;
import io.github.mainstringargs.alpaca.rest.assets.GetAssetsRequestBuilder;
import io.github.mainstringargs.alpaca.rest.bars.GetBarsRequestBuilder;
import io.github.mainstringargs.alpaca.rest.calendar.GetCalendarRequestBuilder;
import io.github.mainstringargs.alpaca.rest.clock.GetClockRequestBuilder;
import io.github.mainstringargs.alpaca.rest.exceptions.AlpacaAPIException;
import io.github.mainstringargs.alpaca.rest.orders.DeleteOrderRequestBuilder;
import io.github.mainstringargs.alpaca.rest.orders.GetListOfOrdersRequestBuilder;
import io.github.mainstringargs.alpaca.rest.orders.GetOrderByClientIdRequestBuilder;
import io.github.mainstringargs.alpaca.rest.orders.GetOrderRequestBuilder;
import io.github.mainstringargs.alpaca.rest.orders.PostOrderRequestBuilder;
import io.github.mainstringargs.alpaca.rest.positions.GetOpenPositionBySymbolRequestBuilder;
import io.github.mainstringargs.alpaca.rest.positions.GetOpenPositionsRequestBuilder;

/**
 * The Class AlpacaAPI.
 */
public class AlpacaAPI {

  /** The key id. */
  private String keyId;

  /** The secret. */
  private String secret;

  /** The base account url. */
  private String baseAccountUrl;

  /** The alpaca request. */
  private AlpacaRequest alpacaRequest;

  /** The base data url. */
  private String baseDataUrl;
  
  /** The logger. */
  private static Logger LOGGER = LogManager.getLogger(AlpacaAPI.class);

  /**
   * Instantiates a new Alpaca API using properties specified in alpaca.properties file (or relevant
   * defaults)
   */
  public AlpacaAPI() {

    keyId = AlpacaProperties.KEY_ID_VALUE;
    secret = AlpacaProperties.SECRET_VALUE;
    baseAccountUrl = AlpacaProperties.BASE_ACCOUNT_URL_VALUE;
    baseDataUrl = AlpacaProperties.BASE_DATA_URL_VALUE;
    
    LOGGER.info("AlpacaAPI is using the following properties: \nkeyId: " + keyId + "\nsecret: "
        + secret + "\nbaseAccountUrl: " + baseAccountUrl + "\nbaseDataUrl: " + baseDataUrl);
 
    
    alpacaRequest = new AlpacaRequest(keyId, secret);

  }

  /**
   * Instantiates a new Alpaca API using the specified keyId, secret, baseAccountUrl, and baseDataUrl.
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
    
    LOGGER.info("AlpacaAPI is using the following properties: \nkeyId: " + keyId + "\nsecret: "
        + secret + "\nbaseAccountUrl: " + baseAccountUrl + "\nbaseDataUrl: " + baseDataUrl);
    
    alpacaRequest = new AlpacaRequest(keyId, secret);


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
    
    LOGGER.info("AlpacaAPI is using the following properties: \nkeyId: " + keyId + "\nsecret: "
        + secret + "\nbaseAccountUrl: " + baseAccountUrl + "\nbaseDataUrl: " + baseDataUrl);
    
    alpacaRequest = new AlpacaRequest(keyId, secret);


  }

  /**
   * Gets the account.
   * 
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/account/#get-the-account">https://docs.alpaca.markets/api-documentation/web-api/account/#get-the-account</a>
   *
   * @return the account
   * @throws AlpacaAPIException the alpaca API exception
   */
  public Account getAccount() throws AlpacaAPIException {
    GetAccountRequestBuilder urlBuilder = new GetAccountRequestBuilder(baseAccountUrl);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }

    Account account = alpacaRequest.getResponseObject(response, Account.class);

    return account;
  }


  /**
   * Gets the orders using the following API defaults:
   * 
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders</a>
   *
   * Status: Open 
   * Limit: 50 
   * Direction: Descending
   *
   * @return the orders
   * @throws AlpacaAPIException the alpaca API exception
   */
  public List<Order> getOrders() throws AlpacaAPIException {
    Type listType = new TypeToken<List<Order>>() {}.getType();

    GetListOfOrdersRequestBuilder urlBuilder = new GetListOfOrdersRequestBuilder(baseAccountUrl);

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
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders</a>
   *
   * @param status the status (Open if null)
   * @param limit the limit (50 if null)
   * @param after the after
   * @param until the until
   * @param direction the direction (Descending if null)
   * @return the orders
   * @throws AlpacaAPIException the alpaca API exception
   */
  public List<Order> getOrders(OrderStatus status, Integer limit, LocalDateTime after,
      LocalDateTime until, Direction direction) throws AlpacaAPIException {
    Type listType = new TypeToken<List<Order>>() {}.getType();

    GetListOfOrdersRequestBuilder urlBuilder = new GetListOfOrdersRequestBuilder(baseAccountUrl);

    urlBuilder.status(status).limit(limit).after(after).until(until).direction(direction);

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
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#request-a-new-order">https://docs.alpaca.markets/api-documentation/web-api/orders/#request-a-new-order</a>
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
   */
  public Order requestNewOrder(String symbol, Integer quantity, OrderSide side, OrderType type,
      OrderTimeInForce timeInForce, Double limitPrice, Double stopPrice, String clientOrderId)
      throws AlpacaAPIException {

    Type objectType = new TypeToken<Order>() {}.getType();

    PostOrderRequestBuilder urlBuilder = new PostOrderRequestBuilder(baseAccountUrl);

    urlBuilder.symbol(symbol).quantity(quantity).side(side).type(type).timeInForce(timeInForce)
        .limitPrice(limitPrice).stopPrice(stopPrice).clientOrderId(clientOrderId);

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
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order</a>
   *
   * @param orderId the order id (required)
   * @return the order
   * @throws AlpacaAPIException the alpaca API exception
   */
  public Order getOrder(String orderId) throws AlpacaAPIException {
    Type objectType = new TypeToken<Order>() {}.getType();

    GetOrderRequestBuilder urlBuilder = new GetOrderRequestBuilder(baseAccountUrl);

    urlBuilder.orderId(orderId);

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
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order-by-client-order-id">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order-by-client-order-id</a>
   *
   * @param clientOrderId the client order id (required)
   * @return the order by client id
   * @throws AlpacaAPIException the alpaca API exception
   */
  public Order getOrderByClientId(String clientOrderId) throws AlpacaAPIException {
    Type objectType = new TypeToken<Order>() {}.getType();

    GetOrderByClientIdRequestBuilder urlBuilder =
        new GetOrderByClientIdRequestBuilder(baseAccountUrl);

    urlBuilder.ordersByClientOrderId(clientOrderId);

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
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#cancel-an-order">https://docs.alpaca.markets/api-documentation/web-api/orders/#cancel-an-order</a>
   *
   * @param orderId the order id (required)
   * @return true, if successful
   * @throws AlpacaAPIException the alpaca API exception
   */
  public boolean cancelOrder(String orderId) throws AlpacaAPIException {
    DeleteOrderRequestBuilder urlBuilder = new DeleteOrderRequestBuilder(baseAccountUrl);

    urlBuilder.orderId(orderId);

    HttpResponse<JsonNode> response = alpacaRequest.invokeDelete(urlBuilder);

    if ((response.getStatus() != 200 && response.getStatus() != 204)) {
      throw new AlpacaAPIException(response);
    }


    return response.getStatus() == 200 || response.getStatus() == 204;
  }

  /**
   * Gets the open positions.
   * 
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/positions/#get-open-positions">https://docs.alpaca.markets/api-documentation/web-api/positions/#get-open-positions</a>
   *
   * @return the open positions
   * @throws AlpacaAPIException the alpaca API exception
   */
  public List<Position> getOpenPositions() throws AlpacaAPIException {
    Type listType = new TypeToken<List<Position>>() {}.getType();

    GetOpenPositionsRequestBuilder urlBuilder = new GetOpenPositionsRequestBuilder(baseAccountUrl);

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
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/positions/#get-an-open-position">https://docs.alpaca.markets/api-documentation/web-api/positions/#get-an-open-position</a>
   *
   * @param symbol the symbol or asset_id (required)
   * @return the open position by symbol
   * @throws AlpacaAPIException the alpaca API exception
   */
  public Position getOpenPositionBySymbol(String symbol) throws AlpacaAPIException {
    Type listType = new TypeToken<Position>() {}.getType();

    GetOpenPositionBySymbolRequestBuilder urlBuilder =
        new GetOpenPositionBySymbolRequestBuilder(baseAccountUrl);

    urlBuilder.symbol(symbol);

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
   * Status: Active
   * Asset Class: us_equity
   * 
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets">https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets</a>
   *
   * @return the assets
   * @throws AlpacaAPIException the alpaca API exception
   */
  public List<Asset> getAssets() throws AlpacaAPIException {
    Type listType = new TypeToken<List<Asset>>() {}.getType();

    GetAssetsRequestBuilder urlBuilder = new GetAssetsRequestBuilder(baseAccountUrl);

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
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets">https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets</a>
   *
   * @param assetStatus the asset status (Active if null)
   * @param assetClass the asset class (us_equity if null)
   * @return the assets
   * @throws AlpacaAPIException the alpaca API exception
   */
  public List<Asset> getAssets(AssetStatus assetStatus, String assetClass)
      throws AlpacaAPIException {
    Type listType = new TypeToken<List<Asset>>() {}.getType();

    GetAssetsRequestBuilder urlBuilder = new GetAssetsRequestBuilder(baseAccountUrl);

    urlBuilder.status(assetStatus).assetClass(assetClass);

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
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/assets/#get-an-asset">https://docs.alpaca.markets/api-documentation/web-api/assets/#get-an-asset</a>
   *
   * @param symbol the symbol (required)
   * @return the asset by symbol
   * @throws AlpacaAPIException the alpaca API exception
   */
  public Asset getAssetBySymbol(String symbol) throws AlpacaAPIException {
    Type listType = new TypeToken<Asset>() {}.getType();

    GetAssetBySymbolRequestBuilder urlBuilder = new GetAssetBySymbolRequestBuilder(baseAccountUrl);

    urlBuilder.symbol(symbol);

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
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar">https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar</a>
   *
   * @return the calendar
   * @throws AlpacaAPIException the alpaca API exception
   */
  public List<Calendar> getCalendar() throws AlpacaAPIException {
    Type listType = new TypeToken<List<Calendar>>() {}.getType();

    GetCalendarRequestBuilder urlBuilder = new GetCalendarRequestBuilder(baseAccountUrl);

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
   * @see <a href=
   *      "https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar">https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar</a>
   *
   * @param start the start
   * @param end the end
   * @return the calendar
   * @throws AlpacaAPIException the alpaca API exception
   */
  public List<Calendar> getCalendar(LocalDate start, LocalDate end) throws AlpacaAPIException {
    Type listType = new TypeToken<List<Calendar>>() {}.getType();

    GetCalendarRequestBuilder urlBuilder = new GetCalendarRequestBuilder(baseAccountUrl);

    urlBuilder.start(start).end(end);

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
   * @see <a href="https://docs.alpaca.markets/api-documentation/web-api/clock/#get-the-clock">https://docs.alpaca.markets/api-documentation/web-api/clock/#get-the-clock</a>
   *
   * @return the clock
   * @throws AlpacaAPIException the alpaca API exception
   */
  public Clock getClock() throws AlpacaAPIException {
    GetClockRequestBuilder urlBuilder = new GetClockRequestBuilder(baseAccountUrl);

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
   * @see <a
   *      href="https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars">https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars</a>
   *
   * @param timeframe the timeframe (required)
   * @param symbols One or more (max 200) symbol names (required)
   * @param limit The maximum number of bars to be returned for each symbol. It can be between 1 and 1000. Default is 100 if parameter is unspecified or 0. (100 if null)
   * @param start Filter bars equal to or after this time. Cannot be used with after.
   * @param end Filter bars equal to or before this time. Cannot be used with until.
   * @param after Filter bars after this time. Cannot be used with start.
   * @param until Filter bars before this time. Cannot be used with end.
   * @return the bars
   * @throws AlpacaAPIException the alpaca API exception
   */
  public Map<String, List<Bar>> getBars(BarsTimeFrame timeframe, String[] symbols, Integer limit,
      LocalDateTime start, LocalDateTime end, LocalDateTime after, LocalDateTime until)
      throws AlpacaAPIException {
    GetBarsRequestBuilder urlBuilder = new GetBarsRequestBuilder(baseDataUrl);
    urlBuilder.timeframe(timeframe).symbols(symbols).limit(limit).start(start).end(end).after(after)
        .until(until);

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
   * @see <a
   *      href="https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars">https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars</a>
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
   */
  public List<Bar> getBars(BarsTimeFrame timeframe, String symbol, Integer limit,
      LocalDateTime start, LocalDateTime end, LocalDateTime after, LocalDateTime until)
      throws AlpacaAPIException {
    GetBarsRequestBuilder urlBuilder = new GetBarsRequestBuilder(baseDataUrl);
    urlBuilder.timeframe(timeframe).symbols(symbol).limit(limit).start(start).end(end).after(after)
        .until(until);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    if (response.getStatus() != 200) {
      throw new AlpacaAPIException(response);
    }


    Type mapType = new TypeToken<Map<String, List<Bar>>>() {}.getType();

    Map<String, List<Bar>> bars = alpacaRequest.getResponseObject(response, mapType);

    return bars.get(symbol);
  }


}
