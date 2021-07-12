<p align="center"><a href="https://petersoj.github.io/alpaca-java/" target="_blank"><img src="https://i.imgur.com/D8XzGvJ.png" alt="Logo"></a></p>
<p align="center">
    <a href="https://search.maven.org/artifact/net.jacobpeterson/alpaca-java" target="_blank"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/net.jacobpeterson/alpaca-java"></a>
    <a href="https://javadoc.io/doc/net.jacobpeterson/alpaca-java" target="_blank"><img src="https://javadoc.io/badge/net.jacobpeterson/alpaca-java.svg" alt="Javadocs"></a>
    <a href="https://travis-ci.com/github/Petersoj/alpaca-java" target="_blank"><img src="https://travis-ci.com/Petersoj/alpaca-java.svg?branch=master" alt="Build Status"></a>
    <a href="https://codecov.io/gh/petersoj/alpaca-java"><img src="https://codecov.io/gh/petersoj/alpaca-java/branch/master/graph/badge.svg" alt="CodeCov badge"/></a>
    <a href="https://opensource.org/licenses/MIT" target="_blank"><img alt="GitHub" src="https://img.shields.io/github/license/petersoj/alpaca-java"></a>    
</p>

# Overview
This is a Java implementation for the <a href="https://alpaca.markets/">Alpaca</a> API. Alpaca lets you trade with algorithms, connect with apps, and build services all with a commission-free stock trading API. This library is community developed and if you have any questions, please ask them on [Github Discussions](https://github.com/Petersoj/alpaca-java/discussions), the [Alpaca Slack #dev-alpaca-java channel](https://alpaca.markets/slack), or on the [Alpaca Forums](https://forum.alpaca.markets/).

Give this repository a star ⭐ if it helped you build a trading algorithm in Java!

# Gradle and Maven Integration
If you are using Gradle as your build tool, add the following dependency to your `build.gradle` file:

```
dependencies {
    implementation group: 'net.jacobpeterson', name: 'alpaca-java', version: '7.2.1'
}
```

If you are using Maven as your build tool, add the following dependency to your `pom.xml` file:

```
<dependency>
    <groupId>net.jacobpeterson</groupId>
    <artifactId>alpaca-java</artifactId>
    <version>7.2.1</version>
    <scope>compile</scope>
</dependency>
```

Note that you don't have to use the Maven Central artifacts and instead can just install a clone of this project to your local Maven repository as shown in the [Building](#building) section.

# Configuration
Creating an `alpaca.properties` file on the classpath with the following format allows you to easily load properties using the `AlpacaAPI` default constructor:
```
key_id = <your Key ID>
secret_key = <your Secret Key>
endpoint_api_type = <must be either "paper" or "live">
data_api_type = <must be either "iex" or "sip">
```
The default values for `alpaca.properties` can be found [here](src/main/resources/alpaca.default.properties).

# Logger
For logging, this library uses [SLF4j](http://www.slf4j.org/) which serves as an interface for various logging frameworks. This enables you to use whatever logging framework you would like. However, if you do not add a logging framework as a dependency in your project, the console will output a message stating that SLF4j is defaulting to a no-operation (NOP) logger implementation. To enable logging, add a logging framework of your choice as a dependency to your project such as [Log4j 2](http://logging.apache.org/log4j/2.x/index.html), [SLF4j-simple](http://www.slf4j.org/manual.html), or [Apache Commons Logging](https://commons.apache.org/proper/commons-logging/).

# Examples
Note that the examples below are not exhaustive. Refer to the [Javadoc](https://javadoc.io/doc/net.jacobpeterson/alpaca-java) for all classes and method signatures.

## [`AlpacaAPI`](src/main/java/net/jacobpeterson/alpaca/AlpacaAPI.java)
The [`AlpacaAPI`](src/main/java/net/jacobpeterson/alpaca/AlpacaAPI.java) class contains several instances of various [`AlpacaEndpoints`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/AlpacaEndpoint.java) and [`AlpacaWebsockets`](src/main/java/net/jacobpeterson/alpaca/websocket/AlpacaWebsocket.java) to interface with Alpaca. You will generally only need one instance of this class in your application. Note that many methods inside the various [`AlpacaEndpoints`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/AlpacaEndpoint.java) allow `null` to be passed in as a parameter if it is optional. 

The Alpaca API specification is located [here](https://docs.alpaca.markets/api-documentation/api-v2/) and the [`AlpacaAPI`](src/main/java/net/jacobpeterson/alpaca/AlpacaAPI.java) Javadoc is located [here](https://javadoc.io/doc/net.jacobpeterson/alpaca-java/latest/net/jacobpeterson/alpaca/AlpacaAPI.html).

Example usage:
```java
// This constructor uses the 'alpaca.properties' file on the classpath for configuration
AlpacaAPI alpacaAPI = new AlpacaAPI();

// This constructor passes in a 'keyID' and 'secret' String
String keyID = "<some key ID>";
String secretKey = "<some secret>";
AlpacaAPI alpacaAPI = new AlpacaAPI(keyID, secret);

// This constructor is for OAuth tokens
String oAuthToken = "<some OAuth token>";
AlpacaAPI alpacaAPI = new AlpacaAPI(oAuthToken);
```

Note that this library uses [OkHttp](https://square.github.io/okhttp/) as its HTTP client library which creates background threads to service requests. These threads persist even if the main thread exists so if you want to destroy these threads when you're done using [`AlpacaAPI`](src/main/java/net/jacobpeterson/alpaca/AlpacaAPI.java) so your program can exit without calling `System.exit()`, use the following snippet:
```java
alpacaAPI.getOkHttpClient().dispatcher().executorService().shutdown();
alpacaAPI.getOkHttpClient().connectionPool().evictAll();
```

See the [OkHttpClient Documentation](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/#shutdown-isnt-necessary) for more information.

## [`AlpacaClientException`](src/main/java/net/jacobpeterson/alpaca/rest/AlpacaClientException.java)
[`AlpacaClientException`](src/main/java/net/jacobpeterson/alpaca/rest/AlpacaClientException.java) is thrown anytime an exception occurs when using various [`AlpacaEndpoints`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/AlpacaEndpoint.java). It should be caught and handled within your trading algorithm application accordingly.

## [`AccountEndpoint`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/AccountEndpoint.java)
The Account API serves important information related to an account, including account status, funds available for trade, funds available for withdrawal, and various flags relevant to an account's ability to trade.

Example usage:
```java
try {
    // Get 'Account' information and print it out
    Account account = alpacaAPI.account().get();
    System.out.println(account);
} catch (AlpacaClientException exception) {
    exception.printStackTrace();
}
```

## [`MarketDataEndpoint`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/MarketDataEndpoint.java)
The Data API v2 provides market data through an easy-to-use API for historical data.

Example usage:
```java
try {
    // Get AAPL one hour bars from 7/6/2021 market open
    // to 7/8/2021 market close and print them out
    BarsResponse aaplBarsResponse = alpacaAPI.marketData().getBars(
            "AAPL",
            ZonedDateTime.of(2021, 7, 6, 9, 30, 0, 0, ZoneId.of("America/New_York")),
            ZonedDateTime.of(2021, 7, 8, 12 + 4, 0, 0, 0, ZoneId.of("America/New_York")),
            null,
            null,
            BarsTimeFrame.ONE_HOUR);
    aaplBarsResponse.getBars().forEach(System.out::println);

    // Get AAPL first 10 trades on 7/8/2021 at market open and print them out
    TradesResponse aaplTradesResponse = alpacaAPI.marketData().getTrades(
            "AAPL",
            ZonedDateTime.of(2021, 7, 8, 9, 30, 0, 0, ZoneId.of("America/New_York")),
            ZonedDateTime.of(2021, 7, 8, 9, 31, 0, 0, ZoneId.of("America/New_York")),
            10,
            null);
    aaplTradesResponse.getTrades().forEach(System.out::println);

    // Print out latest AAPL trade
    Trade latestAAPLTrade = alpacaAPI.marketData().getLatestTrade("AAPL").getTrade();
    System.out.printf("Latest AAPL Trade: %s\n", latestAAPLTrade);

    // Print out snapshot of AAPL, GME, and TSLA
    Map<String, Snapshot> snapshots = alpacaAPI.marketData()
            .getSnapshots(Arrays.asList("AAPL", "GME", "TSLA"));
    snapshots.forEach((symbol, snapshot) ->
            System.out.printf("Symbol: %s\nSnapshot: %s\n\n", symbol, snapshot));
} catch (AlpacaClientException exception) {
    exception.printStackTrace();
}
```

## [`OrdersEndpoint`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/OrdersEndpoint.java)
The Orders API allows a user to monitor, place, and cancel their orders with Alpaca.

Example usage:
```java
try {
    // Print all the 'Order's of AAPL and TSLA until 7/6/2021 in
    // ascending (oldest to newest) order
    List<Order> orders = alpacaAPI.orders().get(
            CurrentOrderStatus.ALL,
            null,
            null,
            ZonedDateTime.of(2021, 7, 6, 0, 0, 0, 0, ZoneId.of("America/New_York")),
            SortDirection.ASCENDING,
            true,
            Arrays.asList("AAPL", "TSLA"));
    orders.forEach(System.out::println);

    // Request a new limit order for TSLA for 100 shares at a limit price
    // of $653.00 and TIF of DAY.
    Order tslaLimitOrder = alpacaAPI.orders().requestLimitOrder(
            "TSLA",
            100,
            OrderSide.BUY,
            OrderTimeInForce.DAY,
            653.00,
            false);
    System.out.printf("Requested %s %s order at %s\n",
            tslaLimitOrder.getSymbol(),
            tslaLimitOrder.getType(),
            tslaLimitOrder.getSubmittedAt());

    // Check if TSLA limit order has filled 5 seconds later and if it hasn't
    // replace it with a higher limit order so it fills!
    Thread.sleep(5000);
    tslaLimitOrder = alpacaAPI.orders().get(tslaLimitOrder.getId(), false);
    if (tslaLimitOrder.getFilledAt() == null && tslaLimitOrder.getStatus().equals(OrderStatus.NEW)) {
        Order replacedOrder = alpacaAPI.orders().replace(
                tslaLimitOrder.getId(),
                100,
                OrderTimeInForce.DAY,
                655.00,
                null, null, null);
        System.out.printf("Replaced TSLA order: %s\n", replacedOrder);
    }

    // Cancel all open orders after 2 seconds
    Thread.sleep(2000);
    List<CancelledOrder> cancelledOrders = alpacaAPI.orders().cancelAll();
    cancelledOrders.forEach((cancelledOrder) -> System.out.printf("Cancelled: %s\n", cancelledOrder));

    // Request a new fractional market order for 0.5 shares of GME
    alpacaAPI.orders().requestFractionalMarketOrder("GME", 0.5, OrderSide.BUY);
    // Request a new notional market order for $25 worth of GME shares
    alpacaAPI.orders().requestNotionalMarketOrder("GME", 25d, OrderSide.BUY);
} catch (AlpacaClientException | InterruptedException exception) {
    exception.printStackTrace();
}
```

## [`PositionsEndpoint`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/PositionsEndpoint.java)
The Positions API provides information about an account's current open positions.

Example usage:
```java
try {
    // Close 50% of all open TSLA and AAPL positions
    List<Position> openPositions = alpacaAPI.positions().get();
    for (Position openPosition : openPositions) {
        if (openPosition.getSymbol().equals("TSLA") || openPosition.getSymbol().equals("AAPL")) {
            Order closePositionOrder = alpacaAPI.positions()
                    .close(openPosition.getSymbol(), null, 50d);
            System.out.printf("Closing 50%% of %s position: %s\n",
                    openPosition.getSymbol(), closePositionOrder);
        }
    }
} catch (AlpacaClientException exception) {
    exception.printStackTrace();
}
```

## [`AssetsEndpoint`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/AssetsEndpoint.java)
The Assets API serves as the master list of assets available for trade and data consumption from Alpaca.

Example usage:
```java
try {
    // Print out a CSV of all active US equity 'Asset's
    List<Asset> activeUSEquities = alpacaAPI.assets().get(AssetStatus.ACTIVE, null);
    System.out.println(activeUSEquities
            .stream().map(Asset::getSymbol)
            .collect(Collectors.joining(", ")));

    // Print out TSLA 'Asset' information
    Asset tslaAsset = alpacaAPI.assets().getBySymbol("TSLA");
    System.out.println(tslaAsset);
} catch (AlpacaClientException exception) {
    exception.printStackTrace();
}
```

## [`WatchlistEndpoint`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/WatchlistEndpoint.java)
The Watchlist API provides CRUD operation for the account's watchlist.

Example usage:
```java
try {
    // Print out CSV all 'Watchlist' names
    List<Watchlist> watchlists = alpacaAPI.watchlist().get();
    System.out.println(watchlists.stream()
            .map(Watchlist::getName)
            .collect(Collectors.joining(", ")));

    // Create a watchlist for day trading with TSLA and AAPL initially
    Watchlist dayTradeWatchlist = alpacaAPI.watchlist().create("Day Trade", "TSLA", "AAPL");

    // Remove TSLA and add MSFT to 'dayTradeWatchlist'
    alpacaAPI.watchlist().removeSymbol(dayTradeWatchlist.getId(), "TSLA");
    alpacaAPI.watchlist().addAsset(dayTradeWatchlist.getId(), "MSFT");

    // Set the updated watchlist variable
    dayTradeWatchlist = alpacaAPI.watchlist().get(dayTradeWatchlist.getId());

    // Print CSV of 'dayTradeWatchlist' 'Asset's
    System.out.println(dayTradeWatchlist.getAssets()
            .stream().map(Asset::getSymbol)
            .collect(Collectors.joining(", ")));

    // Delete the 'dayTradeWatchlist'
    alpacaAPI.watchlist().delete(dayTradeWatchlist.getId());
} catch (AlpacaClientException exception) {
    exception.printStackTrace();
}
```

## [`CalendarEndpoint`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/CalendarEndpoint.java)
The calendar API serves the full list of market days from 1970 to 2029.

Example usage:
```java
try {
    // Get the 'Calendar's of the week of Christmas 2020 and print them out
    List<Calendar> calendar = alpacaAPI.calendar().get(
            LocalDate.of(2020, 12, 20),
            LocalDate.of(2020, 12, 27));
    calendar.forEach(System.out::println);
} catch (AlpacaClientException exception) {
    exception.printStackTrace();
}
```

## [`ClockEndpoint`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/ClockEndpoint.java)
The clock API serves the current market timestamp, whether the market is currently open, as well as the times of the next market open and close.

Example usage:
```java
try {
    // Get the market 'Clock' and print it out
    Clock clock = alpacaAPI.clock().get();
    System.out.println(clock);
} catch (AlpacaClientException exception) {
    exception.printStackTrace();
}
```

## [`AccountConfigurationEndpoint`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/AccountConfigurationEndpoint.java)
The Account Configuration API provides custom configurations about your trading account settings. These configurations control various allow you to modify settings to suit your trading needs.

Example usage:
```java
try {
    // Update the 'AccountConfiguration' to block new orders
    AccountConfiguration accountConfiguration = alpacaAPI.accountConfiguration().get();
    accountConfiguration.setSuspendTrade(true);
    alpacaAPI.accountConfiguration().set(accountConfiguration);
} catch (AlpacaClientException exception) {
    exception.printStackTrace();
}
```

## [`AccountActivitiesEndpoint`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/AccountActivitiesEndpoint.java)
The Account Activities API provides access to a historical record of transaction activities that have impacted your account.

Example usage:
```java
try {
    // Print all order fill and cash deposit 'AccountActivity's on 7/8/2021
    List<AccountActivity> accountActivities = alpacaAPI.accountActivities().get(
            ZonedDateTime.of(2021, 7, 8, 0, 0, 0, 0, ZoneId.of("America/New_York")),
            null,
            null,
            SortDirection.ASCENDING,
            null,
            null,
            ActivityType.FILL, ActivityType.CSD);
    for (AccountActivity accountActivity : accountActivities) {
        if (accountActivity instanceof TradeActivity) {
            System.out.println("TradeActivity: " + (TradeActivity) accountActivity);
        } else if (accountActivity instanceof NonTradeActivity) {
            System.out.println("NonTradeActivity: " + (NonTradeActivity) accountActivity);
        }
    }
} catch (AlpacaClientException exception) {
    exception.printStackTrace();
}
```

## [`PortfolioHistoryEndpoint`](src/main/java/net/jacobpeterson/alpaca/rest/endpoint/PortfolioHistoryEndpoint.java)
The Portfolio History API returns the timeseries data for equity and profit loss information of the account.

Example usage:
```java
try {
    // Get 3 days of one-hour 'PortfolioHistory' on 7/8/2021 and print out its data points
    PortfolioHistory portfolioHistory = alpacaAPI.portfolioHistory().get(
            3,
            PortfolioPeriodUnit.DAY,
            PortfolioTimeFrame.ONE_HOUR,
            LocalDate.of(2021, 7, 8),
            false);
    System.out.printf("Timeframe: %s, Base value: %s \n",
            portfolioHistory.getTimeframe(),
            portfolioHistory.getBaseValue());
    portfolioHistory.getDataPoints().forEach(System.out::println);
} catch (AlpacaClientException exception) {
    exception.printStackTrace();
}
```

## [`StreamingWebsocket`](src/main/java/net/jacobpeterson/alpaca/websocket/streaming/StreamingWebsocket.java)
Alpaca offers WebSocket streaming of order updates.

Example usage:
```java
// Add a 'StreamingListener' that simply prints streaming information
StreamingListener streamingListener = (messageType, message) ->
        System.out.printf("%s: %s\n", messageType.name(), message);
alpacaAPI.streaming().addListener(streamingListener);

// Listen to the 'authorization' and 'trade update' streams.
// Note this will connect the websocket and wait for authorization
// if it isn't already connected.
alpacaAPI.streaming().streams(
        StreamingMessageType.AUTHORIZATION,
        StreamingMessageType.TRADE_UPDATES);

// Wait for 5 seconds
Thread.sleep(5000);

// Note that when the last 'StreamingListener' is removed, the websocket
// connection is closed.
alpacaAPI.streaming().removeListener(streamingListener);
```

The following methods show how you can control the state of the [`StreamingWebsocket`](src/main/java/net/jacobpeterson/alpaca/websocket/streaming/StreamingWebsocket.java) directly.
```java
alpacaAPI.streaming().connect();
alpacaAPI.streaming().waitForAuthorization();
System.out.println(alpacaAPI.streaming().isValid());
alpacaAPI.streaming().disconnect();
```

## [`MarketDataWebsocket`](src/main/java/net/jacobpeterson/alpaca/websocket/marketdata/MarketDataWebsocket.java)
Alpaca's Data API v2 provides websocket streaming for trades, quotes, and minute bars. This helps receive the most up to date market information that could help your trading strategy to act upon certain market movement.

Example usage:
```java
// Add a 'MarketDataListener' that simply prints market data information
MarketDataListener marketDataListener = (messageType, message) ->
        System.out.printf("%s: %s\n", messageType.name(), message);
alpacaAPI.marketDataStreaming().addListener(marketDataListener);

// Listen to 'SubscriptionsMessage', 'SuccessMessage', and 'ErrorMessage' control messages
// that contain information about the stream's current state.
alpacaAPI.marketDataStreaming().subscribeToControl(
        MarketDataMessageType.SUCCESS,
        MarketDataMessageType.SUBSCRIPTION,
        MarketDataMessageType.ERROR);
// Listen to the AAPL and TSLA trades and all ('*') bars.
// Note this will connect the websocket and wait for authorization
// if it isn't already connected.
alpacaAPI.marketDataStreaming().subscribe(
        Arrays.asList("AAPL", "TSLA"),
        null,
        Arrays.asList("*"));

// Wait for 5 seconds
Thread.sleep(5000);

// Note that when the last 'MarketDataListener' is removed, the websocket
// connection is closed.
alpacaAPI.marketDataStreaming().removeListener(marketDataListener);
```

The following methods show how you can control the state of the [`MarketDataWebsocket`](src/main/java/net/jacobpeterson/alpaca/websocket/marketdata/MarketDataWebsocket.java) directly.
```java
alpacaAPI.marketDataStreaming().connect();
alpacaAPI.marketDataStreaming().waitForAuthorization();
System.out.println(alpacaAPI.marketDataStreaming().isValid());
alpacaAPI.marketDataStreaming().disconnect();
```

# Building
To build this project yourself, clone this repository and run:
```
./gradlew build
```

To install built artifacts to your local maven repo, run:
```
./gradlew install -x test
```

# Testing

To run mocked tests using Mockito, run:
```
./gradlew test
```
Note that mocked tests never send real API requests to Alpaca. Mocked tests are meant to test the basic integrity of this API locally.

To run live endpoint tests with Alpaca Paper credentials, create the `alpaca.properties` file in `src/test/resources` with the corresponding credentials. Then run:
```
./gradlew test -PtestPackage=live
```
Note that the live tests will modify your account minimally. It's meant to test live endpoints on a real paper account during market hours to confirm that API methods are working properly. Please read through the live endpoint tests [here](https://github.com/Petersoj/alpaca-java/tree/master/src/test/java/live/net/jacobpeterson/alpaca) before running this testing suite on your own paper account.

# TODO
- Finish Unit Testing (both live and mocked)
- Use [TA4j](https://github.com/ta4j/ta4j) `Num` interface instead of `Double` for number variables so that users can use either `Double` or `BigDecimal` for performance or precision in price data.
- Add [TimeSeriesDataStore](https://github.com/Petersoj/TimeSeriesDataStore) using Alpaca Data API

Contributions are welcome!

If you are creating a Pull Request, be sure to create a new branch in your forked repository for your feature or bug fix instead of committing directly to the `master` branch in your fork. 
