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
	compile "io.github.mainstringargs:alpaca-java:2.0"
}
```

# Alpaca Java Maven Integration

Add the following dependency to your pom.xml file:

```
    <dependency>
      <groupId>io.github.mainstringargs</groupId>
      <artifactId>alpaca-java</artifactId>
      <version>2.0</version>
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
