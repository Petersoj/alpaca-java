<p align="center"><a href="https://petersoj.github.io/alpaca-java/" target="_blank"><img src="https://raw.githubusercontent.com/Petersoj/alpaca-java/master/.github/image/logo.png" alt="Logo"></a></p>
<p align="center">
    <a href="https://github.com/Petersoj/alpaca-java" target="_blank"><img alt="GitHub Repository" src="https://img.shields.io/badge/GitHub-000000?logo=github"></a>
    <a href="https://search.maven.org/artifact/net.jacobpeterson.alpaca/alpaca-java" target="_blank"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/net.jacobpeterson.alpaca/alpaca-java"></a>
    <a href="https://javadoc.io/doc/net.jacobpeterson.alpaca/alpaca-java" target="_blank"><img src="https://javadoc.io/badge/net.jacobpeterson.alpaca/alpaca-java.svg" alt="Javadocs"></a>
    <a href="https://opensource.org/licenses/MIT" target="_blank"><img alt="GitHub" src="https://img.shields.io/github/license/petersoj/alpaca-java"></a>
</p>

# Overview
This library is a Java client implementation of the [Alpaca](https://alpaca.markets) API. Alpaca lets you trade with algorithms, connect with apps, and build services all with a commission-free trading API for stocks, crypto, and options. This library uses the [Alpaca OpenAPI Specifications](https://docs.alpaca.markets/openapi) to generate clients for the REST API with the [OkHttp](https://square.github.io/okhttp/) library, but implements the websocket and SSE streaming interface using a custom implementation with the [OkHttp](https://square.github.io/okhttp/) library. This library is community developed and if you have any questions, please ask them on [Github Discussions](https://github.com/Petersoj/alpaca-java/discussions), the [Alpaca Slack #dev-alpaca-java channel](https://alpaca.markets/slack), or on the [Alpaca Forums](https://forum.alpaca.markets/). This library strives to provide the complete Alpaca API as a Java client implementation, so open a [new issue](https://github.com/Petersoj/alpaca-java/issues) or [new pull request](https://github.com/Petersoj/alpaca-java/pulls) if you find something missing.

Give this repository a star ⭐ if it helped you build a trading algorithm in Java!

# Gradle and Maven Integration
If you are using Gradle as your build tool, add the following dependency to your `build.gradle` file:

```
implementation group: "net.jacobpeterson.alpaca", name: "alpaca-java", version: "10.0.1"
```

If you are using Maven as your build tool, add the following dependency to your `pom.xml` file:

```
<dependency>
    <groupId>net.jacobpeterson.alpaca</groupId>
    <artifactId>alpaca-java</artifactId>
    <version>10.0.1</version>
</dependency>
```

Note that you don't have to use the Maven Central artifacts. Instead, you can clone this repository, build this project, and install the artifacts to your local Maven repository as shown in the [Building](#building) section.

# Logger
For logging, this library uses [SLF4j](http://www.slf4j.org/) which serves as an interface for various logging frameworks. This enables you to use whatever logging framework you would like. However, if you do not add a logging framework as a dependency in your project, the console will output a message stating that SLF4j is defaulting to a no-operation (NOP) logger implementation. To enable logging, add a logging framework of your choice as a dependency to your project such as [Logback](http://logback.qos.ch/), [Log4j 2](http://logging.apache.org/log4j/2.x/index.html), [SLF4j-simple](http://www.slf4j.org/manual.html), or [Apache Commons Logging](https://commons.apache.org/proper/commons-logging/).

# `BigDecimal` vs. `Double`
It is generally considered bad practice to represent currency values in floating-point data types such as `float` or `double` because it can lead to [rounding errors](https://ta4j.github.io/ta4j-wiki/Num.html) and [precision loss](https://stackoverflow.com/a/3730040/4352701) in calculations. However, using floating-point data types can have significant performance benefits compared to using arbitrary-precision number data types, especially in a quantitative finance and algorithmic trading environment. Because of this, `alpaca-java` uses the `Double` data type (the `double` boxed type) when using the Market Data APIs and the `BigDecimal` data type when using the Trading or Broker APIs. The thinking behind this is that exact decimal quantities are important when placing real trades with real money, but less important when performing calculations of financial indicators. The best solution would be to use the [TA4j](https://github.com/ta4j/ta4j) `Num` interface so that you can decide what data type to use based on your use case, but that is on the [TODO list](#todo) for now. By the way, using `BigDecimal` wouldn't matter if Alpaca used floating-point data types in their internal systems or in their REST APIs, but the fact that they use `String` data types in some of the REST API JSON responses and their Trading and Broker OpenAPI specifications don't use the `double` data type, this leads me to believe that using `BigDecimal` does actually matter. 

# Examples
Note that the examples below are not exhaustive. Refer to the [Javadoc](https://javadoc.io/doc/net.jacobpeterson.alpaca/alpaca-java) for all classes and method signatures.

## [`AlpacaAPI`](src/main/java/net/jacobpeterson/alpaca/AlpacaAPI.java)
[`AlpacaAPI`](src/main/java/net/jacobpeterson/alpaca/AlpacaAPI.java) is the main class used to interface with the various Alpaca API endpoints. If you are using the Trading or Market Data APIs for a single Alpaca account or if you are using the Broker API, you will generally only need one instance of this class. However, if you are using the Trading API with OAuth to act on behalf of an Alpaca account, this class is optimized so that it can be instantiated quickly, especially when an existing `OkHttpClient` is given in the constructor. Additionally, all API endpoint instances are instantiated lazily. This class is thread-safe.

The Alpaca API documentation is located [here](https://docs.alpaca.markets/) and the [`AlpacaAPI`](src/main/java/net/jacobpeterson/alpaca/AlpacaAPI.java) Javadoc is located [here](https://javadoc.io/doc/net.jacobpeterson.alpaca/alpaca-java/latest/net/jacobpeterson/alpaca/AlpacaAPI.html).

Example usage:

Use this code if you are using the Trading or Market Data APIs for a single Alpaca account:
```java
final String keyID = "<your_key_id>";
final String secretKey = "<your_secret_key>";
final TraderAPIEndpointType endpointType = TraderAPIEndpointType.PAPER; // or 'LIVE'
final MarketDataWebsocketSourceType sourceType = MarketDataWebsocketSourceType.IEX; // or 'SIP'
final AlpacaAPI alpacaAPI = new AlpacaAPI(keyID, secretKey, endpointType, sourceType);
```

Use this code if you are using the Trading API with OAuth to act on behalf of an Alpaca account:
```java
final String oAuthToken = "<an_oauth_token>";
final TraderAPIEndpointType endpointType = TraderAPIEndpointType.PAPER; // or 'LIVE'
final AlpacaAPI alpacaAPI = new AlpacaAPI(oAuthToken, endpointType);
```

Use this code if you are using the Broker API:
```java
final String brokerAPIKey = "<your_broker_api_key>";
final String brokerAPISecret = "<your_broker_api_secret>";
final BrokerAPIEndpointType endpointType = BrokerAPIEndpointType.SANDBOX; // or 'PRODUCTION'
final AlpacaAPI alpacaAPI = new AlpacaAPI(brokerAPIKey, brokerAPISecret, endpointType);
```

Note that this library uses [OkHttp](https://square.github.io/okhttp/) as its HTTP client library which creates background threads to service requests via a connection pool. These threads persist even if the main thread exits, so if you want to destroy these threads when you're done using [`AlpacaAPI`](src/main/java/net/jacobpeterson/alpaca/AlpacaAPI.java), use `alpacaAPI.closeOkHttpClient();`.

## [`Trader API`](src/main/java/net/jacobpeterson/alpaca/rest/trader/AlpacaTraderAPI.java)
The [`Trader API`](src/main/java/net/jacobpeterson/alpaca/rest/trader/AlpacaTraderAPI.java) is used for placing trades, updating account details, getting open positions, and more. Refer to the [Javadoc](https://javadoc.io/doc/net.jacobpeterson.alpaca/alpaca-java/latest/net/jacobpeterson/alpaca/rest/trader/AlpacaTraderAPI.html) for a list of all available method signatures.

Example usage:
```java
// Place a market order to buy one share of Apple
final Order openingOrder = alpacaAPI.trader().orders()
        .postOrder(new PostOrderRequest()
                .symbol("AAPL")
                .qty("1")
                .side(OrderSide.BUY)
                .type(OrderType.MARKET)
                .timeInForce(TimeInForce.GTC));
System.out.println("Opening Apple order: " + openingOrder);

// Wait for massive gains
Thread.sleep(10_000);

// Close the Apple position
final Order closingOrder = alpacaAPI.trader().positions()
        .deleteOpenPosition("AAPL", null, new BigDecimal("100"));
System.out.println("Closing Apple order: " + openingOrder);

// Wait for closing trade to fill
Thread.sleep(10_000);

// Print out PnL
final String openFillPrice = alpacaAPI.trader().orders()
        .getOrderByOrderID(UUID.fromString(openingOrder.getId()), false)
        .getFilledAvgPrice();
final String closeFillPrice = alpacaAPI.trader().orders()
        .getOrderByOrderID(UUID.fromString(closingOrder.getId()), false)
        .getFilledAvgPrice();
System.out.println("PnL from Apple trade: " +
        new BigDecimal(closeFillPrice).subtract(new BigDecimal(openFillPrice)));
```

## [`Market Data API`](src/main/java/net/jacobpeterson/alpaca/rest/marketdata/AlpacaMarketDataAPI.java)
The [`Market Data API`](src/main/java/net/jacobpeterson/alpaca/rest/marketdata/AlpacaMarketDataAPI.java) is used for getting market data for stocks, cryptocurrencies, options, and more. Refer to the [Javadoc](https://javadoc.io/doc/net.jacobpeterson.alpaca/alpaca-java/latest/net/jacobpeterson/alpaca/rest/marketdata/AlpacaMarketDataAPI.html) for a list of all available method signatures.

Example usage:
```java
// Print out the latest Tesla trade
final StockTrade latestTSLATrade = alpacaAPI.marketData().stock()
        .stockLatestTradeSingle("TSLA", StockFeed.IEX, null).getTrade();
System.out.printf("Latest TSLA trade: price=%s, size=%s\n",
        latestTSLATrade.getP(), latestTSLATrade.getS());

// Print out the highest Bitcoin ask price on the order book
final CryptoOrderbook latestBitcoinOrderBooks = alpacaAPI.marketData().crypto()
        .cryptoLatestOrderbooks(CryptoLoc.US, "BTC/USD").getOrderbooks().get("BTC/USD");
final Double highestBitcoinAskPrice = latestBitcoinOrderBooks.getA().stream()
        .map(CryptoOrderbookEntry::getP)
        .max(Double::compare)
        .orElse(null);
System.out.println("Bitcoin highest ask price: " + highestBitcoinAskPrice);

// Print out the latest Microsoft option trade
final String latestMSFTOptionTrade = alpacaAPI.marketData().option()
        .optionChain("MSFT").getSnapshots().entrySet().stream()
        .filter(entry -> entry.getValue().getLatestTrade() != null)
        .map(entry -> Map.entry(entry.getKey(), entry.getValue().getLatestTrade()))
        .max(Comparator.comparing(entry -> entry.getValue().getT()))
        .map(entry -> String.format("ticker=%s, time=%s, price=%s",
                entry.getKey(), entry.getValue().getT(), entry.getValue().getP()))
        .orElse(null);
System.out.println("Latest Microsoft option trade: " + latestMSFTOptionTrade);
```

## [`Broker API`](src/main/java/net/jacobpeterson/alpaca/rest/broker/AlpacaBrokerAPI.java)
The [`Broker API`](src/main/java/net/jacobpeterson/alpaca/rest/broker/AlpacaBrokerAPI.java) is used for creating new Alpaca accounts for your end users, funding their accounts, placing trades, and more. Refer to the [Javadoc](https://javadoc.io/doc/net.jacobpeterson.alpaca/alpaca-java/latest/net/jacobpeterson/alpaca/rest/broker/AlpacaBrokerAPI.html) for a list of all available method signatures.

Example usage:
```java
// Listen to the trade events (SSE) and print them out
final SSERequest sseRequest = alpacaAPI.broker().events()
        .subscribeToTradeV2(null, null, null, null, new SSEListenerAdapter<>());

// Wait for SSE channel to be ready
Thread.sleep(2000);

// Buy one share of GME for an account
alpacaAPI.broker().trading()
        .createOrderForAccount(UUID.fromString("<some_account_uuid>"),
                new CreateOrderRequest()
                        .symbol("GME")
                        .qty(BigDecimal.ONE)
                        .side(OrderSide.SELL)
                        .timeInForce(TimeInForce.GTC)
                        .type(OrderType.MARKET));

// Wait to be filled
Thread.sleep(2000);

// Close the SSE stream and the OkHttpClient to exit cleanly
sseRequest.close();
alpacaAPI.closeOkHttpClient();
```

## [`Updates Stream`](src/main/java/net/jacobpeterson/alpaca/websocket/updates/UpdatesWebsocket.java)
The [`Updates Stream`](src/main/java/net/jacobpeterson/alpaca/websocket/updates/UpdatesWebsocket.java) is used for listening to trade updates in realtime. Refer to the [Javadoc](https://javadoc.io/doc/net.jacobpeterson.alpaca/alpaca-java/latest/net/jacobpeterson/alpaca/websocket/updates/UpdatesWebsocketInterface.html) for a list of all available method signatures.

Example usage:
```java
// Connect to the 'updates' stream and wait until it's authorized
alpacaAPI.updatesStream().connect();
if (!alpacaAPI.updatesStream().waitForAuthorization(5, TimeUnit.SECONDS)) {
    throw new RuntimeException();
}

// Print out trade updates
alpacaAPI.updatesStream().setListener(System.out::println);
alpacaAPI.updatesStream().subscribeToTradeUpdates(true);

// Place a trade
alpacaAPI.trader().orders().postOrder(new PostOrderRequest()
        .symbol("AAPL")
        .qty("1")
        .side(OrderSide.BUY)
        .type(OrderType.MARKET)
        .timeInForce(TimeInForce.GTC));

// Wait a few seconds
Thread.sleep(5000);

// Close the trade
alpacaAPI.trader().positions().deleteAllOpenPositions(true);

// Wait a few seconds
Thread.sleep(5000);

// Disconnect the 'updates' stream and exit cleanly
alpacaAPI.updatesStream().disconnect();
alpacaAPI.closeOkHttpClient();
```

## [`Market Data Stream`](src/main/java/net/jacobpeterson/alpaca/websocket/marketdata/MarketDataWebsocket.java)
The [`Market Data Stream`](src/main/java/net/jacobpeterson/alpaca/websocket/marketdata/MarketDataWebsocket.java) is used for listening to stock market data, crypto market data, and news data in realtime. Refer to the Javadocs ([stock](https://javadoc.io/doc/net.jacobpeterson.alpaca/alpaca-java/latest/net/jacobpeterson/alpaca/websocket/marketdata/streams/stock/StockMarketDataWebsocketInterface.html), [crypto](https://javadoc.io/doc/net.jacobpeterson.alpaca/alpaca-java/latest/net/jacobpeterson/alpaca/websocket/marketdata/streams/crypto/CryptoMarketDataWebsocketInterface.html), [news](https://javadoc.io/doc/net.jacobpeterson.alpaca/alpaca-java/latest/net/jacobpeterson/alpaca/websocket/marketdata/streams/news/NewsMarketDataWebsocketInterface.html)) for a list of all available method signatures.

Example usage:
```java
// Connect to the 'stock market data' stream and wait until it's authorized
alpacaAPI.stockMarketDataStream().connect();
if (!alpacaAPI.stockMarketDataStream().waitForAuthorization(5, TimeUnit.SECONDS)) {
    throw new RuntimeException();
}

// Print out trade messages
alpacaAPI.stockMarketDataStream().setListener(new StockMarketDataListenerAdapter() {
    @Override
    public void onTrade(StockTradeMessage trade) {
        System.out.println("Received trade: " + trade);
    }
});

// Subscribe to AAPL trades
alpacaAPI.stockMarketDataStream().setTradeSubscriptions(Set.of("AAPL"));
System.out.println("Subscribed to Apple trades.");

// Wait a few seconds
Thread.sleep(5000);

// Unsubscribe from AAPL and subscribe to TSLA and MSFT
alpacaAPI.stockMarketDataStream().setTradeSubscriptions(Set.of("TSLA", "MSFT"));
System.out.println("Subscribed to Tesla and Microsoft trades.");

// Wait a few seconds
Thread.sleep(5000);

// Disconnect the 'stock market data' stream and exit cleanly
alpacaAPI.stockMarketDataStream().disconnect();
alpacaAPI.closeOkHttpClient();
```

# Building
To build this project yourself, clone this repository and run:
```
./gradlew build
```

To install the built artifacts to your local Maven repository on your machine (the `~/.m2/` directory), run:
```
./gradlew publishToMavenLocal
```

# TODO
- Implement better reconnect logic for Websockets and SSE streaming.
- Implement Unit Testing for REST API and Websocket streaming (both live and mocked).
- Use [TA4j](https://github.com/ta4j/ta4j) `Num` interface instead of `Double` or `BigDecimal` for number data types so that users can use either `Double` or `BigDecimal` for performance or precision in price data.

# Contributing
Contributions are welcome!

Do the following before starting your pull request:
1. Create a new branch in your forked repository for your feature or bug fix instead of committing directly to the `master` branch in your fork.
2. Use the `dev` branch as the base branch in your pull request.
