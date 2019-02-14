# Alpaca Java Building
This project exposes that data as a Java project.  

To build this project yourself, clone the project and run:

```
./gradlew build
```

# Alpaca Java Gradle Integration

Add the following dependency to your build.gradle file:

```
dependencies {
	compile "io.github.mainstringargs:alpaca-java:1.0"
}
```

# Alpaca Java Maven Integration

# Alpaca API

See https://docs.alpaca.markets/api-documentation/web-api/ for general Alpaca API documentation

#### `public AlpacaAPI()`

Instantiates a new Alpaca API using properties specified in alpaca.properties file (or relevant defaults)

#### `public AlpacaAPI(String keyId, String secret, String baseAccountUrl, String baseDataUrl)`

Instantiates a new Alpaca API using the specified keyId, secret, baseAccountUrl, & baseDataUrl.

 * **Parameters:**
   * `keyId` — the key id
   * `secret` — the secret
   * `baseAccountUrl` — the base account url
   * `baseDataUrl` — the base data url

#### `public AlpacaAPI(String keyId, String secret, String baseAccountUrl)`

Instantiates a new Alpaca API using the specified keyId, secret, baseAccountUrl, & baseDataUrl

 * **Parameters:**
   * `keyId` — the key id
   * `secret` — the secret
   * `baseAccountUrl` — the base account url

#### `public Account getAccount() throws AlpacaAPIException`

Gets the account.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/account/#get-the-account">https://docs.alpaca.markets/api-documentation/web-api/account/#get-the-account</a>

     <p>
 * **Returns:** the account
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public List<Order> getOrders() throws AlpacaAPIException`

Gets the orders using the following API defaults:

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders</a>

     <p>

     Status: Open 

     Limit: 50 

     Direction: Descending

     <p>
 * **Returns:** the orders
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public List<Order> getOrders(OrderStatus status, Integer limit, LocalDateTime after, LocalDateTime until, Direction direction) throws AlpacaAPIException`

Gets the orders.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders</a>

     <p>
 * **Parameters:**
   * `status` — the status (Open if null)
   * `limit` — the limit (50 if null)
   * `after` — the after
   * `until` — the until
   * `direction` — the direction (Descending if null)
 * **Returns:** the orders
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public Order requestNewOrder(String symbol, Integer quantity, OrderSide side, OrderType type, OrderTimeInForce timeInForce, Double limitPrice, Double stopPrice, String clientOrderId) throws AlpacaAPIException`

Request/Submits new order.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#request-a-new-order">https://docs.alpaca.markets/api-documentation/web-api/orders/#request-a-new-order</a>

     <p>
 * **Parameters:**
   * `symbol` — the symbol or asset_id (required)
   * `quantity` — the quantity (required)
   * `side` — the side (required)
   * `type` — the type (required)
   * `timeInForce` — the time in force (required)
   * `limitPrice` — the limit price (required if Limit or Stop Limit)
   * `stopPrice` — the stop price (required if Stop or Stop Limit)
   * `clientOrderId` — the client order id (optional)
 * **Returns:** the order
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public Order getOrder(String orderId) throws AlpacaAPIException`

Gets the order.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order</a>

     <p>
 * **Parameters:** `orderId` — the order id (required)
 * **Returns:** the order
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public Order getOrderByClientId(String clientOrderId) throws AlpacaAPIException`

Gets the order by client id.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order-by-client-order-id">https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order-by-client-order-id</a>

     <p>
 * **Parameters:** `clientOrderId` — the client order id (required)
 * **Returns:** the order by client id
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public boolean cancelOrder(String orderId) throws AlpacaAPIException`

Cancel order.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/orders/#cancel-an-order">https://docs.alpaca.markets/api-documentation/web-api/orders/#cancel-an-order</a>

     <p>
 * **Parameters:** `orderId` — the order id (required)
 * **Returns:** true, if successful
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public List<Position> getOpenPositions() throws AlpacaAPIException`

Gets the open positions.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/positions/#get-open-positions">https://docs.alpaca.markets/api-documentation/web-api/positions/#get-open-positions</a>

     <p>
 * **Returns:** the open positions
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public Position getOpenPositionBySymbol(String symbol) throws AlpacaAPIException`

Gets the open position by symbol.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/positions/#get-an-open-position">https://docs.alpaca.markets/api-documentation/web-api/positions/#get-an-open-position</a>

     <p>
 * **Parameters:** `symbol` — the symbol or asset_id (required)
 * **Returns:** the open position by symbol
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public List<Asset> getAssets() throws AlpacaAPIException`

Gets the assets using the following API defaults:

Status: Active Asset Class: us_equity

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets">https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets</a>

     <p>
 * **Returns:** the assets
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public List<Asset> getAssets(AssetStatus assetStatus, String assetClass) throws AlpacaAPIException`

Gets the assets.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets">https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets</a>

     <p>
 * **Parameters:**
   * `assetStatus` — the asset status (Active if null)
   * `assetClass` — the asset class (us_equity if null)
 * **Returns:** the assets
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public Asset getAssetBySymbol(String symbol) throws AlpacaAPIException`

Gets the asset by symbol or asset_id.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/assets/#get-an-asset">https://docs.alpaca.markets/api-documentation/web-api/assets/#get-an-asset</a>

     <p>
 * **Parameters:** `symbol` — the symbol (required)
 * **Returns:** the asset by symbol
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public List<Calendar> getCalendar() throws AlpacaAPIException`

Gets the calendar.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar">https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar</a>

     <p>
 * **Returns:** the calendar
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public List<Calendar> getCalendar(LocalDate start, LocalDate end) throws AlpacaAPIException`

Gets the calendar.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar">https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar</a>

     <p>
 * **Parameters:**
   * `start` — the start
   * `end` — the end
 * **Returns:** the calendar
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public Clock getClock() throws AlpacaAPIException`

Gets the clock.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/clock/#get-the-clock">https://docs.alpaca.markets/api-documentation/web-api/clock/#get-the-clock</a>

     <p>
 * **Returns:** the clock
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public Map<String, List<Bar>> getBars(BarsTimeFrame timeframe, String[] symbols, Integer limit, LocalDateTime start, LocalDateTime end, LocalDateTime after, LocalDateTime until) throws AlpacaAPIException`

Gets the bars.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars">https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars</a>

     <p>
 * **Parameters:**
   * `timeframe` — the timeframe (required)
   * `symbols` — One or more (max 200) symbol names (required)
   * `limit` — The maximum number of bars to be returned for each symbol. It can be between 1 and 1000. Default is 100 if parameter is unspecified or 0. (100 if null)
   * `start` — Filter bars equal to or after this time. Cannot be used with after.
   * `end` — Filter bars equal to or before this time. Cannot be used with until.
   * `after` — Filter bars after this time. Cannot be used with start.
   * `until` — Filter bars before this time. Cannot be used with end.
 * **Returns:** the bars
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception

#### `public List<Bar> getBars(BarsTimeFrame timeframe, String symbol, Integer limit, LocalDateTime start, LocalDateTime end, LocalDateTime after, LocalDateTime until) throws AlpacaAPIException`

Gets the bars.

 * **See also:** <a href="https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars">https://docs.alpaca.markets/api-documentation/web-api/market-data/bars/#get-a-list-of-bars</a>

     <p>
 * **Parameters:**
   * `timeframe` — the timeframe (required)
   * `symbol` — One symbol name (required)
   * `limit` — The maximum number of bars to be returned for each symbol. It can be between 1 and

     1000. Default is 100 if parameter is unspecified or 0. (100 if null)
   * `start` — Filter bars equal to or after this time. Cannot be used with after.
   * `end` — Filter bars equal to or before this time. Cannot be used with until.
   * `after` — Filter bars after this time. Cannot be used with start.
   * `until` — Filter bars before this time. Cannot be used with end.
 * **Returns:** the bars
 * **Exceptions:** `AlpacaAPIException` — the alpaca API exception
