# Overview

[![Build Status](https://travis-ci.org/mainstringargs/alpaca-java.svg?branch=master)](https://travis-ci.org/mainstringargs/alpaca-java)

This is a Java implementation for the Alpaca @ <a href="https://alpaca.markets/">https://alpaca.markets/</a> (See <a href="https://docs.alpaca.markets/api-documentation/web-api/">https://docs.alpaca.markets/api-documentation/web-api/</a> for general Alpaca API documentation).  Alpaca API lets you build and trade with real-time market data for free. 

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
	compile "io.github.mainstringargs:alpaca-java:3.0.0"
}
```

# Alpaca Java Maven Integration

Add the following dependency to your pom.xml file:

```
    <dependency>
      <groupId>io.github.mainstringargs</groupId>
      <artifactId>alpaca-java</artifactId>
      <version>3.0.0</version>
      <scope>compile</scope>
    </dependency>
```
# Configuration

If you plan on using the alpaca.properties, generate a secret and a key at Alpaca and set the following properties in an alpaca.properties file on the classpath:

```
key_id = <YOUR KEY>
secret = <YOUR SECRET>
base_url = https://api.alpaca.markets
```

# Simple Example

This example uses the AlpacaAPI class to print out account information, submit a limit order, and print out bars.

```java

    // This logs into Alpaca using the alpaca.properties file on the classpath.
    AlpacaAPI alpacaApi = new AlpacaAPI();

    // Register explicitly for ACCOUNT_UPDATES and ORDER_UPDATES Messages via stream listener
    alpacaApi.addAlpacaStreamListener(
        new AlpacaStreamListenerAdapter(MessageType.ACCOUNT_UPDATES, MessageType.ORDER_UPDATES) {
          @Override
          public void streamUpdate(MessageType messageType, UpdateMessage message) {

            switch (messageType) {
              case ACCOUNT_UPDATES:
                AccountUpdateMessage accounUpdateMessage = (AccountUpdateMessage) message;
                System.out
                    .println("\nReceived Account Update: \n\t" + accounUpdateMessage.toString());
                break;
              case ORDER_UPDATES:
                OrderUpdateMessage orderUpdateMessage = (OrderUpdateMessage) message;
                System.out.println("\nReceived Order Update: \n\t" + orderUpdateMessage.toString());
                break;
            }
          }
        });

    // Get Account Information
    try {
      Account alpacaAccount = alpacaApi.getAccount();

      System.out.println("\n\nAccount Information:");
      System.out
          .println("\tCreated At: " + Utilities.fromDateTimeString(alpacaAccount.getCreatedAt())
              + "\n\tBuying Power: " + alpacaAccount.getBuyingPower() + "\n\tPortfolio Value: "
              + alpacaAccount.getPortfolioValue());

    } catch (AlpacaAPIException e) {
      e.printStackTrace();
    }

    // Get Stock Market Hours
    try {
      Clock alpacaClock = alpacaApi.getClock();

      System.out.println("\n\nClock:");
      System.out.println("\tCurrent Time: "
          + Utilities.fromDateTimeString(alpacaClock.getTimestamp()) + "\n\tIs Open: "
          + alpacaClock.isIsOpen() + "\n\tMarket Next Open Time: "
          + Utilities.fromDateTimeString(alpacaClock.getNextOpen()) + "\n\tMark Next Close Time: "
          + Utilities.fromDateTimeString(alpacaClock.getNextClose()));


    } catch (AlpacaAPIException e) {
      e.printStackTrace();
    }

    Order limitOrder = null;
    String orderClientId = UUID.randomUUID().toString();

    // Request an Order
    try {
      // Lets submit a limit order for when AMZN gets down to $100.0!
      limitOrder = alpacaApi.requestNewOrder("AMZN", 1, OrderSide.BUY, OrderType.LIMIT,
          OrderTimeInForce.DAY, 100.0, null, orderClientId);

      System.out.println("\n\nLimit Order Response:");

      System.out.println("\tSymbol: " + limitOrder.getSymbol() + "\n\tClient Order Id: "
          + limitOrder.getClientOrderId() + "\n\tQty: " + limitOrder.getQty() + "\n\tType: "
          + limitOrder.getType() + "\n\tLimit Price: $" + limitOrder.getLimitPrice()
          + "\n\tCreated At: " + Utilities.fromDateTimeString(limitOrder.getCreatedAt()));



    } catch (AlpacaAPIException e) {
      e.printStackTrace();
    }

    // Get an existing Order by Id
    try {
      Order limitOrderById = alpacaApi.getOrder(limitOrder.getId());

      System.out.println("\n\nLimit Order By Id Response:");

      System.out.println("\tSymbol: " + limitOrderById.getSymbol() + "\n\tClient Order Id: "
          + limitOrderById.getClientOrderId() + "\n\tQty: " + limitOrderById.getQty() + "\n\tType: "
          + limitOrderById.getType() + "\n\tLimit Price: $" + limitOrderById.getLimitPrice()
          + "\n\tCreated At: " + Utilities.fromDateTimeString(limitOrderById.getCreatedAt()));



    } catch (AlpacaAPIException e) {
      e.printStackTrace();
    }

    // Get an existing Order by Client Id
    try {
      Order limitOrderByClientId = alpacaApi.getOrderByClientId(limitOrder.getClientOrderId());

      System.out.println("\n\nLimit Order By Id Response:");

      System.out.println("\tSymbol: " + limitOrderByClientId.getSymbol() + "\n\tClient Order Id: "
          + limitOrderByClientId.getClientOrderId() + "\n\tQty: " + limitOrderByClientId.getQty()
          + "\n\tType: " + limitOrderByClientId.getType() + "\n\tLimit Price: $"
          + limitOrderByClientId.getLimitPrice() + "\n\tCreated At: "
          + Utilities.fromDateTimeString(limitOrderByClientId.getCreatedAt()));



    } catch (AlpacaAPIException e) {
      e.printStackTrace();
    }

    // Cancel the existing order
    try {
      boolean orderCanceled = alpacaApi.cancelOrder(limitOrder.getId());

      System.out.println("\n\nCancel order response:");

      System.out.println("\tCancelled: " + orderCanceled);



    } catch (AlpacaAPIException e) {
      e.printStackTrace();
    }

    // Get bars
    try {
      List<Bar> bars = alpacaApi.getBars(BarsTimeFrame.ONE_DAY, "AMZN", 10,
          LocalDateTime.of(2019, 2, 13, 10, 30), LocalDateTime.of(2019, 2, 14, 10, 30), null, null);

      System.out.println("\n\nBars response:");

      for (Bar bar : bars) {
        System.out.println("\t==========");
        System.out.println("\tUnix Time "
            + LocalDateTime.ofInstant(Instant.ofEpochMilli(bar.getT() * 1000), ZoneId.of("UTC")));
        System.out.println("\tOpen: $" + bar.getO());
        System.out.println("\tHigh: $" + bar.getH());
        System.out.println("\tLow: $" + bar.getL());
        System.out.println("\tClose: $" + bar.getC());
        System.out.println("\tVolume: " + bar.getV());
      }



    } catch (AlpacaAPIException e) {
      e.printStackTrace();
    }

```

This code will output the following:

```
Account Information:
	Created At: 2018-12-29T20:02:27.328159
	Buying Power: 3901.0089
	Portfolio Value: 8070.0289


Clock:
	Current Time: 2019-02-15T13:51:05.774672683
	Is Open: true
	Market Next Open Time: 2019-02-19T08:30
	Mark Next Close Time: 2019-02-15T15:00


Limit Order Response:
	Symbol: AMZN
	Client Order Id: 9b31e674-9775-4357-bfb8-4841f22427de
	Qty: 1
	Type: limit
	Limit Price: $100
	Created At: 2019-02-15T18:51:06.377720987

Received Order Update: 
	OrderUpdateMessage [event=NEW, price=null, timestamp=null, order=io.github.mainstringargs.alpaca.domain.Order@5c728415[id=ed02131f-d1c6-4815-a941-888ce1addd84,clientOrderId=e23ca503-ebec-4754-b65a-ee42806fc67d,createdAt=2019-02-26T14:41:05.503352Z,updatedAt=2019-02-26T14:41:05.599835871Z,submittedAt=2019-02-26T14:41:05.445148Z,filledAt=<null>,expiredAt=<null>,canceledAt=<null>,failedAt=<null>,assetId=f801f835-bfe6-4a9d-a6b1-ccbb84bfd75f,symbol=AMZN,exchange=<null>,assetClass=us_equity,qty=1,filledQty=0,type=limit,side=buy,timeInForce=day,limitPrice=100,stopPrice=<null>,filledAvgPrice=<null>,status=new]]


Limit Order By Id Response:
	Symbol: AMZN
	Client Order Id: 9b31e674-9775-4357-bfb8-4841f22427de
	Qty: 1
	Type: limit
	Limit Price: $100
	Created At: 2019-02-15T18:51:06.377721


Limit Order By Id Response:
	Symbol: AMZN
	Client Order Id: 9b31e674-9775-4357-bfb8-4841f22427de
	Qty: 1
	Type: limit
	Limit Price: $100
	Created At: 2019-02-15T18:51:06.377721


Cancel order response:
	Cancelled: true

Received Order Update: 
	OrderUpdateMessage [event=CANCELED, price=null, timestamp=2019-02-26T08:41:05.990, order=io.github.mainstringargs.alpaca.domain.Order@2da73a2c[id=ed02131f-d1c6-4815-a941-888ce1addd84,clientOrderId=e23ca503-ebec-4754-b65a-ee42806fc67d,createdAt=2019-02-26T14:41:05.503352Z,updatedAt=2019-02-26T14:41:06.076159839Z,submittedAt=2019-02-26T14:41:05.445148Z,filledAt=<null>,expiredAt=<null>,canceledAt=2019-02-26T14:41:05.99Z,failedAt=<null>,assetId=f801f835-bfe6-4a9d-a6b1-ccbb84bfd75f,symbol=AMZN,exchange=<null>,assetClass=us_equity,qty=1,filledQty=0,type=limit,side=buy,timeInForce=day,limitPrice=100,stopPrice=<null>,filledAvgPrice=<null>,status=canceled]]


Bars response:
	==========
	Unix Time 2019-02-13T05:00
	Open: $1647.0
	High: $1656.38
	Low: $1637.11
	Close: $1640.0
	Volume: 3544703
	==========
	Unix Time 2019-02-14T05:00
	Open: $1624.5
	High: $1637.9
	Low: $1606.06
	Close: $1622.65
	Volume: 4105694
```

# Alpaca API

See <a href="https://docs.alpaca.markets/api-documentation/web-api/">https://docs.alpaca.markets/api-documentation/web-api/</a> for general Alpaca API documentation

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

#### `public void addAlpacaStreamListener(AlpacaStreamListener streamListener)`

Adds the alpaca stream listener.

 * **Parameters:** `streamListener` — the stream listener

#### `public void removeAlpacaStreamListener(AlpacaStreamListener streamListener)`

Removes the alpaca stream listener.

 * **Parameters:** `streamListener` — the stream listener

# Polygon API

See <a href="https://polygon.io/docs/#getting-started">https://polygon.io/docs/#getting-started</a> for general Polygon API documentation

#### `public PolygonAPI()`

Instantiates a new polygon API.

#### `public PolygonAPI(String keyId)`

Instantiates a new polygon API.

 * **Parameters:** `keyId` — the key id

#### `public PolygonAPI(String keyId, String... polygonNatsServers)`

Instantiates a new polygon API.

 * **Parameters:**
   * `keyId` — the key id
   * `polygonNatsServers` — the polygon nats servers

#### `public SymbolEndpoints getSymbolEndpoints(String symbol) throws PolygonAPIException`

Get gets the endpoints that are supported for a symbol. Note: The endpoints object is key/values of the endpoint name and url. These will almost always be the same of all symbols.

 * **Parameters:** `symbol` — we want the endpoint list for.
 * **Returns:** the symbol endpoints
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol

#### `public SymbolDetails getSymbolDetails(String symbol) throws PolygonAPIException`

Get the details of the symbol company/entity. These are important details which offer an overview of the entity. Things like name, sector, description, logo and similar companies.

 * **Parameters:** `symbol` — we want details for
 * **Returns:** the symbol details
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_company

#### `public SymbolAnalystRatings getSymbolAnalystRatings(String symbol) throws PolygonAPIException`

Get the analyst ratings of the symbol company/entity. Ratings are from current date, up to 5months into the future.

 * **Parameters:** `symbol` — we want analyst ratings for
 * **Returns:** the symbol analyst ratings
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_analysts

#### `public List<SymbolDividend> getSymbolDividends(String symbol) throws PolygonAPIException`

Get the historical dividends for this symbol.

 * **Parameters:** `symbol` — we want details for
 * **Returns:** the symbol dividends
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_dividends

#### `public List<SymbolEarning> getSymbolEarnings(String symbol) throws PolygonAPIException`

Get the historical earnings for a company

 * **Parameters:** `symbol` — we want details for
 * **Returns:** the symbol earnings
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_earnings

#### `public List<SymbolFinancial> getSymbolFinancials(String symbol) throws PolygonAPIException`

Get the historical financials for a company

 * **Parameters:** `symbol` — we want details for
 * **Returns:** the symbol financials
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_financials

#### `public List<SymbolNews> getSymbolNews(String symbol) throws PolygonAPIException`

Get news articles for this symbol.

 * **Parameters:** `symbol` — the symbol we want details for
 * **Returns:** the symbol news
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_news

#### `public List<SymbolNews> getSymbolNews(String symbol, Integer perpage, Integer page) throws PolygonAPIException`

Get news articles for this symbol.

 * **Parameters:**
   * `symbol` — the symbol we want details for
   * `perpage` — How many items to be on each page during pagination. Max 50
   * `page` — Which page of results to return
 * **Returns:** the symbol news
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_news

#### `public Tickers getTickers(Sort sort, io.github.mainstringargs.polygon.enums.Type type, io.github.mainstringargs.polygon.enums.Market market, Locale locale, String search, Integer perpage, Integer page, Boolean active) throws PolygonAPIException`

Query all ticker symbols which are supported by Polygon.io.

 * **Parameters:**
   * `sort` — Which field to sort by.
   * `type` — If you want the results to only container a certain type.
   * `market` — Get tickers for a specific market
   * `locale` — Get tickers for a specific region/locale
   * `search` — Search the name of tickers
   * `perpage` — How many items to be on each page during pagination. Max 50
   * `page` — Which page of results to return
   * `active` — Filter for only active or inactive symbols
 * **Returns:** the tickers
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Reference/get_v2_reference_tickers

#### `public List<Market> getMarkets() throws PolygonAPIException`

Get the list of currently supported markets

 * **Returns:** the markets
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Reference/get_v2_reference_markets

#### `public List<io.github.mainstringargs.polygon.domain.reference.Locale> getLocales() throws PolygonAPIException`

Get the list of currently supported locales

 * **Returns:** the locales
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Reference/get_v2_reference_locales

#### `public TypesMapping getTypesMapping() throws PolygonAPIException`

Get the mapping of ticker types to descriptions / long names

 * **Returns:** the types mapping
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Reference/get_v2_reference_types

#### `public List<Split> getSplits(String symbol) throws PolygonAPIException`

Get the historical splits for this symbol

 * **Parameters:** `symbol` — we want details for
 * **Returns:** the splits
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Reference/get_v2_reference_splits_symbol

#### `public List<Exchange> getExchanges() throws PolygonAPIException`

List of stock exchanges which are supported by Polygon.io

 * **Returns:** the exchange
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v1_meta_exchanges

#### `public Trades getHistoricTrades(String symbol, LocalDate date, Integer offset, Integer limit) throws PolygonAPIException`

Get historic trades for a symbol.

 * **Parameters:**
   * `symbol` — the symbol of the company to retrieve
   * `date` — Date/Day of the historic ticks to retreive
   * `offset` — Timestamp offset, used for pagination. This is the offset at which to start the

     results. Using the timestamp of the last result as the offset will give you the next

     page of results.
   * `limit` — Limit the size of response, Max 50000
 * **Returns:** the historic trades
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v1_historic_trades_symbol_date

#### `public Quotes getHistoricQuotes(String symbol, LocalDate date, Integer offset, Integer limit) throws PolygonAPIException`

Get historic quotes for a symbol.

 * **Parameters:**
   * `symbol` — the symbol of the company to retrieve
   * `date` — Date/Day of the historic ticks to retreive
   * `offset` — Timestamp offset, used for pagination. This is the offset at which to start the

     results. Using the timestamp of the last result as the offset will give you the next

     page of results.
   * `limit` — Limit the size of response, Max 50000
 * **Returns:** the historic quotes
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v1_historic_quotes_symbol_date

#### `public Trade getLastTrade(String symbol) throws PolygonAPIException`

Get the last trade for a given stock.

 * **Parameters:** `symbol` — Symbol of the stock to get
 * **Returns:** the last trade
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_stocks_symbol

#### `public Quote getLastQuote(String symbol) throws PolygonAPIException`

Get the last quote tick for a given stock.

 * **Parameters:** `symbol` — Symbol of the quote to get
 * **Returns:** the last quote
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_quote_stocks_symbol

#### `public DailyOpenClose getDailyOpenClose(String symbol, LocalDate date) throws PolygonAPIException`

Get the open, close and afterhours prices of a symbol on a certain date.

 * **Parameters:**
   * `symbol` — Symbol of the stock to get
   * `dateDate` — of the requested open/close
 * **Returns:** the daily open close
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v1_open_close_symbol_date

#### `public List<Snapshot> getSnapshotAllTickers() throws PolygonAPIException`

Snapshot allows you to see all tickers current minute aggregate, daily aggregate and last trade. As well as previous days aggregate and calculated change for today.

 * **Returns:** the snapshot all tickers
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers

#### `public Snapshot getSnapshot(String symbol) throws PolygonAPIException`

See the current snapshot of a single ticker

 * **Parameters:** `symbol` — Ticker of the snapshot
 * **Returns:** the snapshot
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers_ticker

#### `public List<Snapshot> getSnapshotsGainers() throws PolygonAPIException`

See the current snapshot of the top 20 gainers of the day at the moment.

 * **Returns:** the snapshots gainers
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_gainers

#### `public List<Snapshot> getSnapshotsLosers() throws PolygonAPIException`

See the current snapshot of the top 20 losers of the day at the moment.

 * **Returns:** the snapshots losers
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_losers

#### `public Aggregates getPreviousClose(String ticker, Boolean unadjusted) throws PolygonAPIException`

Get the previous day close for the specified ticker

 * **Parameters:**
   * `ticker` — Ticker symbol of the request
   * `unadjusted` — Set to true if the results should NOT be adjusted for splits.
 * **Returns:** the previous close
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_prev

#### `public Aggregates getAggregates(String ticker, Integer multiplier, Timespan timeSpan, LocalDate fromDate, LocalDate toDate, Boolean unadjusted) throws PolygonAPIException`

Get aggregates for a date range, in custom time window sizes

 * **Parameters:**
   * `ticker` — Ticker symbol of the request
   * `multiplier` — Size of the timespan multiplier
   * `timeSpan` — Size of the time window
   * `fromDate` — the from date
   * `toDate` — the to date
   * `unadjusted` — Set to true if the results should NOT be adjusted for splits
 * **Returns:** the aggregates
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_range_multiplier_timespan_from_to

#### `public Aggregates getGroupedDaily(Locale locale, io.github.mainstringargs.polygon.enums.Market market, LocalDate date, Boolean unadjusted) throws PolygonAPIException`

Get the daily OHLC for entire markets.

 * **Parameters:**
   * `locale` — Locale of the aggregates ( See 'Locales' API )
   * `market` — Market of the aggregates ( See 'Markets' API )
   * `date` — to date
   * `unadjusted` — Set to true if the results should NOT be adjusted for splits.
 * **Returns:** the grouped daily
 * **Exceptions:** `PolygonAPIException` — the polygon API exception
 * **See also:** https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_grouped_locale_locale_market_market_date

#### `public void addPolygonStreamListener(PolygonStreamListener streamListener)`

Adds the polygon stream listener.

 * **Parameters:** `streamListener` — the stream listener

#### `public void removePolygonStreamListener(PolygonStreamListener streamListener)`

Removes the polygon stream listener.

 * **Parameters:** `streamListener` — the stream listener
