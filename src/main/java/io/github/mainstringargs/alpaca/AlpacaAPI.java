package io.github.mainstringargs.alpaca;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import io.github.mainstringargs.alpaca.domain.Account;
import io.github.mainstringargs.alpaca.domain.Asset;
import io.github.mainstringargs.alpaca.domain.Calendar;
import io.github.mainstringargs.alpaca.domain.Clock;
import io.github.mainstringargs.alpaca.domain.Order;
import io.github.mainstringargs.alpaca.domain.Position;
import io.github.mainstringargs.alpaca.enums.AssetStatus;
import io.github.mainstringargs.alpaca.enums.Direction;
import io.github.mainstringargs.alpaca.enums.OrderSide;
import io.github.mainstringargs.alpaca.enums.OrderStatus;
import io.github.mainstringargs.alpaca.enums.OrderTimeInForce;
import io.github.mainstringargs.alpaca.enums.OrderType;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;
import io.github.mainstringargs.alpaca.rest.AlpacaRequest;
import io.github.mainstringargs.alpaca.rest.AlpacaRequestBuilder;
import io.github.mainstringargs.alpaca.rest.accounts.AccountRequestBuilder;
import io.github.mainstringargs.alpaca.rest.assets.GetAssetBySymbolRequestBuilder;
import io.github.mainstringargs.alpaca.rest.assets.GetAssetsRequestBuilder;
import io.github.mainstringargs.alpaca.rest.calendar.CalendarRequestBuilder;
import io.github.mainstringargs.alpaca.rest.calendar.GetCalendarRequestBuilder;
import io.github.mainstringargs.alpaca.rest.clock.ClockRequestBuilder;
import io.github.mainstringargs.alpaca.rest.orders.DeleteOrderRequestBuilder;
import io.github.mainstringargs.alpaca.rest.orders.GetListOfOrdersRequestBuilder;
import io.github.mainstringargs.alpaca.rest.orders.GetOrderByClientIdRequestBuilder;
import io.github.mainstringargs.alpaca.rest.orders.GetOrderRequestBuilder;
import io.github.mainstringargs.alpaca.rest.orders.PostOrderRequestBuilder;
import io.github.mainstringargs.alpaca.rest.positions.GetOpenPositionBySymbolRequestBuilder;
import io.github.mainstringargs.alpaca.rest.positions.GetOpenPositionsRequestBuilder;
import io.github.mainstringargs.alpaca.rest.positions.PositionsRequestBuilder;

/**
 * The Class AlpacaAPI.
 */
public class AlpacaAPI {

  /** The key id. */
  private String keyId;

  /** The secret. */
  private String secret;

  /** The base url. */
  private String baseUrl;

  /** The alpaca request. */
  private AlpacaRequest alpacaRequest;

  /**
   * Instantiates a new alpaca API. Uses alpaca.properties for configuration.
   */
  public AlpacaAPI() {

    keyId = AlpacaProperties.KEY_ID_VALUE;
    secret = AlpacaProperties.SECRET_VALUE;
    baseUrl = AlpacaProperties.BASE_URL_VALUE;
    alpacaRequest = new AlpacaRequest(keyId, secret);

  }

  /**
   * Instantiates a new alpaca API.
   *
   * @param keyId the key id
   * @param secret the secret
   * @param baseUrl the base url
   */
  public AlpacaAPI(String keyId, String secret, String baseUrl) {
    this.keyId = keyId;
    this.secret = secret;
    this.baseUrl = baseUrl;
    alpacaRequest = new AlpacaRequest(keyId, secret);


  }

  /**
   * Gets the account.
   *
   * @return the account
   */
  public Account getAccount() {
    AlpacaRequestBuilder urlBuilder = new AccountRequestBuilder(baseUrl);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    Account account = alpacaRequest.getResponseObject(response, Account.class);

    return account;
  }


  /**
   * Gets the open positions.
   *
   * @return the open positions
   */
  public List<Position> getOpenPositions() {
    Type listType = new TypeToken<List<Position>>() {}.getType();

    GetOpenPositionsRequestBuilder urlBuilder = new GetOpenPositionsRequestBuilder(baseUrl);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    List<Position> positions = alpacaRequest.getResponseObject(response, listType);

    return positions;
  }


  /**
   * Gets the open position.
   *
   * @param symbol the symbol
   * @return the open position
   */
  public Position getOpenPositionBySymbol(String symbol) {
    Type listType = new TypeToken<Position>() {}.getType();

    GetOpenPositionBySymbolRequestBuilder urlBuilder =
        new GetOpenPositionBySymbolRequestBuilder(baseUrl);

    urlBuilder.symbol(symbol);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    Position position = alpacaRequest.getResponseObject(response, listType);

    return position;
  }

  /**
   * Gets the assets.
   *
   * @return the assets
   */
  public List<Asset> getAssets() {
    Type listType = new TypeToken<List<Asset>>() {}.getType();

    GetAssetsRequestBuilder urlBuilder = new GetAssetsRequestBuilder(baseUrl);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    List<Asset> assets = alpacaRequest.getResponseObject(response, listType);

    return assets;
  }


  /**
   * Gets the assets.
   *
   * @param assetStatus the asset status
   * @param assetClass the asset class
   * @return the assets
   */
  public List<Asset> getAssets(AssetStatus assetStatus, String assetClass) {
    Type listType = new TypeToken<List<Asset>>() {}.getType();

    GetAssetsRequestBuilder urlBuilder = new GetAssetsRequestBuilder(baseUrl);

    urlBuilder.status(assetStatus).assetClass(assetClass);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    List<Asset> assets = alpacaRequest.getResponseObject(response, listType);

    return assets;
  }

  /**
   * Gets the asset by symbol.
   *
   * @param symbol the symbol
   * @return the asset by symbol
   */
  public Asset getAssetBySymbol(String symbol) {
    Type listType = new TypeToken<Asset>() {}.getType();

    GetAssetBySymbolRequestBuilder urlBuilder = new GetAssetBySymbolRequestBuilder(baseUrl);

    urlBuilder.symbol(symbol);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    Asset asset = alpacaRequest.getResponseObject(response, listType);

    return asset;
  }

  /**
   * Gets the order.
   *
   * @param orderId the order id
   * @return the order
   */
  public Order getOrder(String orderId) {
    Type objectType = new TypeToken<Order>() {}.getType();

    GetOrderRequestBuilder urlBuilder = new GetOrderRequestBuilder(baseUrl);

    urlBuilder.orderId(orderId);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    Order order = alpacaRequest.getResponseObject(response, objectType);

    return order;
  }

  /**
   * Gets the order by client id.
   *
   * @param clientOrderId the cleint order id
   * @return the order
   */
  public Order getOrderByClientId(String clientOrderId) {
    Type objectType = new TypeToken<Order>() {}.getType();

    GetOrderByClientIdRequestBuilder urlBuilder = new GetOrderByClientIdRequestBuilder(baseUrl);

    urlBuilder.ordersByClientOrderId(clientOrderId);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    Order order = alpacaRequest.getResponseObject(response, objectType);

    return order;
  }



  /**
   * Cancel order.
   *
   * @param orderId the order id
   * @return true, if successful
   */
  public boolean cancelOrder(String orderId) {
    DeleteOrderRequestBuilder urlBuilder = new DeleteOrderRequestBuilder(baseUrl);

    urlBuilder.orderId(orderId);

    HttpResponse<JsonNode> response = alpacaRequest.invokeDelete(urlBuilder);

    return response.getStatus() == 204;
  }

  /**
   * Request new order.
   *
   * @param symbol the symbol
   * @param quantity the quantity
   * @param side the side
   * @param type the type
   * @param timeInForce the time in force
   * @param limitPrice the limit price
   * @param stopPrice the stop price
   * @param clientOrderId the client order id
   * @return the order
   */
  public Order requestNewOrder(String symbol, Integer quantity, OrderSide side, OrderType type,
      OrderTimeInForce timeInForce, Double limitPrice, Double stopPrice, String clientOrderId) {

    Type objectType = new TypeToken<Order>() {}.getType();

    PostOrderRequestBuilder urlBuilder = new PostOrderRequestBuilder(baseUrl);

    urlBuilder.symbol(symbol).quantity(quantity).side(side).type(type).timeInForce(timeInForce)
        .limitPrice(limitPrice).stopPrice(stopPrice).clientOrderId(clientOrderId);

    HttpResponse<JsonNode> response = alpacaRequest.invokePost(urlBuilder);

    Order order = alpacaRequest.getResponseObject(response, objectType);

    return order;
  }



  /**
   * Gets the orders with API defaults.
   *
   * @return the orders
   */
  public List<Order> getOrders() {
    Type listType = new TypeToken<List<Order>>() {}.getType();

    GetListOfOrdersRequestBuilder urlBuilder = new GetListOfOrdersRequestBuilder(baseUrl);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    List<Order> orders = alpacaRequest.getResponseObject(response, listType);

    return orders;
  }


  /**
   * Gets the orders.
   *
   * @param status the status
   * @param limit the limit
   * @param after the after
   * @param until the until
   * @param direction the direction
   * @return the orders
   */
  public List<Order> getOrders(OrderStatus status, Integer limit, LocalDateTime after,
      LocalDateTime until, Direction direction) {
    Type listType = new TypeToken<List<Order>>() {}.getType();

    GetListOfOrdersRequestBuilder urlBuilder = new GetListOfOrdersRequestBuilder(baseUrl);

    urlBuilder.status(status).limit(limit).after(after).until(until).direction(direction);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    List<Order> orders = alpacaRequest.getResponseObject(response, listType);

    return orders;
  }



  /**
   * Gets the calendar.
   *
   * @return the calendar
   */
  public List<Calendar> getCalendar() {
    Type listType = new TypeToken<List<Calendar>>() {}.getType();

    AlpacaRequestBuilder urlBuilder = new CalendarRequestBuilder(baseUrl);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    List<Calendar> calendar = alpacaRequest.getResponseObject(response, listType);

    return calendar;
  }

  /**
   * Gets the calendar.
   *
   * @param start the start
   * @param end the end
   * @return the calendar
   */
  public List<Calendar> getCalendar(LocalDate start, LocalDate end) {
    Type listType = new TypeToken<List<Calendar>>() {}.getType();

    GetCalendarRequestBuilder urlBuilder = new GetCalendarRequestBuilder(baseUrl);

    urlBuilder.start(start).end(end);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    List<Calendar> calendar = alpacaRequest.getResponseObject(response, listType);

    return calendar;
  }

  /**
   * Gets the clock.
   *
   * @return the clock
   */
  public Clock getClock() {
    AlpacaRequestBuilder urlBuilder = new ClockRequestBuilder(baseUrl);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    Clock clock = alpacaRequest.getResponseObject(response, Clock.class);

    return clock;
  }

}
