<br><br>
<div align="center">
    <a href="https://github.com/Petersoj/alpaca-java">
    <img src="https://raw.githubusercontent.com/Petersoj/alpaca-java/refs/heads/main/docs/logo.svg" alt="Logo" width="70%"></a>
    <br><br><br><br>
    <a href="https://github.com/Petersoj/alpaca-java">
    <img src="https://img.shields.io/badge/GitHub_Repository-black?logo=github" alt="GitHub Repository"></a>
    <a href="https://petersoj.github.io/alpaca-java">
    <img src="https://img.shields.io/badge/GitHub_Pages-whitesmoke?logo=github&logoColor=black" alt="GitHub Pages"></a>
    <a href="https://github.com/Petersoj/alpaca-java/discussions">
    <img src="https://img.shields.io/badge/GitHub_Discussions-royalblue?logo=github" alt="GitHub Discussions"></a>
    <br>
    <a href="https://central.sonatype.com/search?namespace=net.jacobpeterson.alpaca-java">
    <img src="https://img.shields.io/badge/Maven_Central-11.0.0-pre.0-blue?logo=apachemaven" alt="Maven Central"></a>
    <a href="https://javadoc.io/doc/net.jacobpeterson.alpaca-java">
    <img src="https://img.shields.io/badge/javadoc-11.0.0-pre.0-brightgreen" alt="javadoc"></a>
    <br>
    <a href="https://openjdk.org/projects/jdk/25">
    <img src="https://img.shields.io/badge/Java_Version-25-orange?logo=java" alt="Java Version"></a>
    <a href="https://github.com/Petersoj/alpaca-java/blob/main/LICENSE.txt">
    <img src="https://img.shields.io/badge/license-MIT-green" alt="License"></a>
</div>
<br>

# Table of Contents

- [Introduction](#introduction)
- [Logging](#logging)
- [Number Representation for Currency Values](#number-representation-for-currency-values)
- [⚠️ WebSocket Support is Coming Soon ⚠️](#-websocket-support-is-coming-soon-)
- [Modules](#modules)
    - [Trading](#trading)
        - [Installation](#installation)
        - [Example](#example)
    - [Market Data](#market-data)
        - [Installation](#installation-1)
        - [Example](#example-1)
    - [Broker](#broker)
        - [Installation](#installation-2)
        - [Example](#example-2)

# Introduction

This project is a Java client library for the [Alpaca](https://alpaca.markets) API. Alpaca is a brokerage platform that
lets you trade with algorithms, connect with apps, and build services all with a commission-free trading API for stocks,
crypto, options, and more. This library uses the [Alpaca OpenAPI Specifications](https://docs.alpaca.markets/us/openapi)
to generate clients for the REST API and provides a custom implementation for WebSocket and SSE support. This library is
community developed and if you have any questions, please ask them on
[GitHub Discussions](https://github.com/Petersoj/alpaca-java/discussions), the
[Alpaca Slack #dev-alpaca-java channel](https://alpaca.markets/slack), or on the
[Alpaca Forums](https://forum.alpaca.markets/).

Alpaca Java offers three modules: [Trading](#trading), [Market Data](#market-data), and [Broker](#broker).

Some awesome things about Alpaca Java:

- Exhaustive Javadoc documentation
- Uses Java's native `HttpClient` (which also enables you to provide a different HTTP client implementation)
- Lightweight with minimal dependencies (only dependencies are: SLF4j, Guava, Jackson, and MessagePack)
- Releases are built directly from this source repository using GitHub Actions CI
- Kotlin friendly
- MIT licensed

Give this repository a star ⭐ and consider [sponsoring](https://github.com/sponsors/Petersoj) ❤️

# Logging

For logging, this library uses [SLF4j](http://www.slf4j.org/) which serves as an interface for various logging
frameworks. This enables you to use whatever logging framework you would like. However, if you do not add a logging
framework as a dependency in your project, the console will output a message stating that SLF4j is defaulting to a
no-operation (NOP) logger implementation. To enable logging, add a logging framework of your choice as a runtime
dependency to your project such as [Logback](http://logback.qos.ch/),
[Log4j 2](http://logging.apache.org/log4j/2.x/index.html), [SLF4j-simple](http://www.slf4j.org/manual.html), or
[Apache Commons Logging](https://commons.apache.org/proper/commons-logging/).

# Number Representation for Currency Values

When working with currency values in computer memory, you may find yourself going back-and-forth between the `Double`
and `BigDecimal` number representations. This is a common issue. Consider using the
[Num](https://github.com/invision-trading/num) library to abstract this problem away as you develop your algorithmic
trading application.

# ⚠️ WebSocket Support is Coming Soon ⚠️

Version 11 is a pre-release that provides the REST API and SSE support for the latest Alpaca OpenAPI specifications.
WebSocket support for streaming market data and trade updates is coming soon. Previous version had WebSocket support via
OkHttp, but this library has switched to using Java's native `HttpClient` and WebSocket support is still a WIP.

# Modules

## Trading

This module is for the Alpaca brokerage platform [Trading API](https://docs.alpaca.markets/us/docs/trading-api).

### Installation

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.alpaca-java:trading:11.0.0-pre.0")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.alpaca-java:trading:11.0.0-pre.0'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.alpaca-java</groupId>
    <artifactId>trading</artifactId>
    <version>11.0.0-pre.0</version>
</dependency>
```

### Example

```java
import net.jacobpeterson.alpacajava.common.ApiEnvironment;
import net.jacobpeterson.alpacajava.trading.TradingApi;
import net.jacobpeterson.alpacajava.trading.model.OrderSide;
import net.jacobpeterson.alpacajava.trading.model.OrderType;
import net.jacobpeterson.alpacajava.trading.model.PostOrderRequest;
import net.jacobpeterson.alpacajava.trading.model.TimeInForce;

import java.math.BigDecimal;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.requireNonNull;

public class Launcher {

    static void main() throws Exception {
        final var keyID = "<your_key_id>";
        final var secretKey = "<your_secret_key>";
        final var apiEnvironment = ApiEnvironment.DEVELOPMENT; // or `PRODUCTION`
        final var tradingApi = new TradingApi(keyID, secretKey, apiEnvironment);

        // Print out account info
        System.out.println(tradingApi.accounts().getAccount());

        // Place a market order to buy one share of Apple
        final var openingOrder = tradingApi.orders()
                .postOrder(new PostOrderRequest()
                        .symbol("AAPL")
                        .qty("1")
                        .side(OrderSide.BUY)
                        .type(OrderType.MARKET)
                        .timeInForce(TimeInForce.GTC));
        System.out.println("Opening Apple order: " + openingOrder);

        // Wait for massive gains
        sleep(ofSeconds(10));

        // Close the Apple position
        final var closingOrder = tradingApi.positions()
                .deleteOpenPosition("AAPL", null, new BigDecimal("100"));
        System.out.println("Closing Apple order: " + closingOrder);

        // Wait for closing trade to fill. For this example, we're sleeping, but in production,
        // this should be done using polling or listening to the trade updates stream.
        sleep(ofSeconds(10));

        // Print out the PnL of our Apple trade
        final var openFillPrice = tradingApi.orders()
                .getOrderByOrderID(requireNonNull(openingOrder.getId()), false)
                .getFilledAvgPrice();
        final var closeFillPrice = tradingApi.orders()
                .getOrderByOrderID(requireNonNull(closingOrder.getId()), false)
                .getFilledAvgPrice();
        System.out.println("PnL from Apple trade: " + new BigDecimal(requireNonNull(closeFillPrice))
                .subtract(new BigDecimal(requireNonNull(openFillPrice))));
    }
}
```

## Market Data

This module is for the Alpaca brokerage platform
[Market Data API](https://docs.alpaca.markets/us/docs/about-market-data-api).

### Installation

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.alpaca-java:market-data:11.0.0-pre.0")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.alpaca-java:market-data:11.0.0-pre.0'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.alpaca-java</groupId>
    <artifactId>market-data</artifactId>
    <version>11.0.0-pre.0</version>
</dependency>
```

### Example

```java
import net.jacobpeterson.alpacajava.common.ApiEnvironment;
import net.jacobpeterson.alpacajava.marketdata.MarketDataApi;
import net.jacobpeterson.alpacajava.marketdata.model.CryptoLatestLoc;
import net.jacobpeterson.alpacajava.marketdata.model.CryptoOrderbookEntry;
import net.jacobpeterson.alpacajava.marketdata.model.StockLatestFeed;

public class Launcher {

    static void main() throws Exception {
        final var keyID = "<your_key_id>";
        final var secretKey = "<your_secret_key>";
        final var apiEnvironment = ApiEnvironment.PRODUCTION; // or `DEVELOPMENT` for Broker Sandbox
        final var marketDataApi = new MarketDataApi(keyID, secretKey, apiEnvironment);

        // Print out the latest Tesla trade
        System.out.println(marketDataApi.stock()
                .stockLatestTradeSingle("TSLA", StockLatestFeed.IEX, null));

        // Print out the lowest Bitcoin ask price on the order book
        System.out.println("Bitcoin lowest ask price: " + marketDataApi.crypto()
                .cryptoLatestOrderbooks(CryptoLatestLoc.US, "BTC/USD").getOrderbooks().get("BTC/USD")
                .getA().stream()
                .map(CryptoOrderbookEntry::getP)
                .min(Double::compare)
                .orElseThrow());
    }
}
```

## Broker

This module is for the Alpaca brokerage platform [Broker API](https://docs.alpaca.markets/us/docs/about-broker-api).

### Installation

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.alpaca-java:broker:11.0.0-pre.0")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.alpaca-java:broker:11.0.0-pre.0'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.alpaca-java</groupId>
    <artifactId>broker</artifactId>
    <version>11.0.0-pre.0</version>
</dependency>
```

### Example

```java
import net.jacobpeterson.alpacajava.broker.BrokerApi;
import net.jacobpeterson.alpacajava.broker.BrokerApiUtil;
import net.jacobpeterson.alpacajava.broker.model.CreateOrderRequest;
import net.jacobpeterson.alpacajava.broker.model.OrderSide;
import net.jacobpeterson.alpacajava.broker.model.OrderType;
import net.jacobpeterson.alpacajava.broker.model.TimeInForce;
import net.jacobpeterson.alpacajava.common.ApiEnvironment;
import net.jacobpeterson.alpacajava.common.ApiHeader;
import net.jacobpeterson.alpacajava.common.sse.SseListenerAdapter;

import java.math.BigDecimal;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;

public class Launcher {

    static void main() throws Exception {
        final var authorizationToken = BrokerApiUtil.getAuthorizationToken("<your_api_key>", "<your_api_secret>");
        final var apiEnvironment = ApiEnvironment.DEVELOPMENT; // or `PRODUCTION`
        final var brokerApi = new BrokerApi(ApiHeader.AUTHORIZATION_BASIC_PREFIX + authorizationToken, apiEnvironment);

        // Listen to the trade events via SSE and print them out
        final var tradesStream = brokerApi.events()
                .subscribeToTradeV2SSE(null, null, null, null, new SseListenerAdapter<>());

        // Buy one share of GME for an account
        System.out.println(brokerApi.trading()
                .createOrderForAccount(UUID.fromString("<some_account_id>"), new CreateOrderRequest()
                        .symbol("GME")
                        .qty(BigDecimal.ONE)
                        .side(OrderSide.BUY)
                        .timeInForce(TimeInForce.GTC)
                        .type(OrderType.MARKET)));

        // Wait a little bit
        sleep(ofSeconds(10));

        // Close the SSE stream
        tradesStream.close();
    }
}
```
