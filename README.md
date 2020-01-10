<p align="center"><a href="https://mainstringargs.github.io/alpaca-java/" target="_blank"><img src="https://i.imgur.com/mQcuK61.jpg"></a></p>
<p align="center">
<a href="https://search.maven.org/artifact/io.github.mainstringargs/alpaca-java" target="_blank"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.github.mainstringargs/alpaca-java"></a> <a href="https://javadoc.io/doc/io.github.mainstringargs/alpaca-java" target="_blank"><img src="https://javadoc.io/badge/io.github.mainstringargs/alpaca-java.svg" alt="Javadocs"></a> <a href="https://travis-ci.org/mainstringargs/alpaca-java" target="_blank"><img src="https://travis-ci.org/mainstringargs/alpaca-java.svg?branch=master" alt="Build Status"></a> <a href="https://codecov.io/gh/mainstringargs/alpaca-java" target="_blank"><img src="https://codecov.io/gh/mainstringargs/alpaca-java/branch/unittesting/graph/badge.svg" />
</a> <a href="https://opensource.org/licenses/MIT" target="_blank"><img alt="GitHub" src="https://img.shields.io/github/license/mainstringargs/alpaca-java"></a>    
</p>

# Overview
This is a Java implementation for <a href="https://alpaca.markets/">Alpaca</a>. Alpaca API lets you build and trade with real-time market data for free. This library is community developed.

## Table of Contents
1. [Alpaca Java Building](#alpaca-java-building)
2. [Alpaca Java Gradle Integration](#alpaca-java-gradle-integration)
3. [Alpaca Java Maven Integration](#alpaca-java-maven-integration)
4. [Configuration](#configuration)
5. [AlpacaAPI Example](#alpacaapi-example)
7. [PolygonAPI Example](#polygonapi-example)

## Alpaca Java Building

This project exposes that data as a Java project.  

To build this project yourself, clone the project and run:

```
./gradlew build
```

## Alpaca Java Gradle Integration

Add the following dependency to your build.gradle file:

```
dependencies {
	compile "io.github.mainstringargs:alpaca-java:5.0.3"
}
```

## Alpaca Java Maven Integration

Add the following dependency to your pom.xml file:

```
<dependency>
    <groupId>io.github.mainstringargs</groupId>
    <artifactId>alpaca-java</artifactId>
    <version>5.0.3</version>
    <scope>compile</scope>
</dependency>
```
## Configuration

If you plan on using the alpaca.properties, set the following properties in an alpaca.properties file on the classpath:
```
api_version = <v1 or v2>
key_id = <YOUR KEY>
secret = <YOUR SECRET>
base_api_url = https://paper-api.alpaca.markets
base_data_url = https://data.alpaca.markets
user_agent=a_user_agent
```
The default values for `alpaca.properties` can be found [here](https://github.com/mainstringargs/alpaca-java/tree/master/src/main/resources).

Similarly, set the following properties in a polygon.properties file on the classpath for using the PolygonAPI:
```
#key_id will default to what is set in alpaca.properties
key_id = <INSERT HERE>
base_api_url = https://api.polygon.io
web_socket_server_url = wss://alpaca.socket.polygon.io/stocks
user_agent=a_user_agent
```
The default values for `polygon.properties` can be found [here](https://github.com/mainstringargs/alpaca-java/tree/master/src/main/resources).

## AlpacaAPI Example

This example uses the `AlpacaAPI` class to subscribe to the Account and Trade Updates stream, print out the account information, submit a limit order, create a watchlist, and print out bars data. Click [here](https://docs.alpaca.markets/api-documentation/api-v2/) for the general Alpaca API documentation and click [here](https://javadoc.io/doc/io.github.mainstringargs/alpaca-java/5.0.0/io/github/mainstringargs/alpaca/AlpacaAPI.html) for the `AlpacaAPI` javadoc.

```java
// This logs into Alpaca using the alpaca.properties file on the classpath.
AlpacaAPI alpacaAPI = new AlpacaAPI();

// Register explicitly for ACCOUNT_UPDATES and ORDER_UPDATES Messages via stream listener
alpacaAPI.addAlpacaStreamListener(new AlpacaStreamListenerAdapter(
        AlpacaStreamMessageType.ACCOUNT_UPDATES,
        AlpacaStreamMessageType.TRADE_UPDATES) {
    @Override
    public void onStreamUpdate(AlpacaStreamMessageType streamMessageType, AlpacaStreamMessage streamMessage) {
        switch (streamMessageType) {
            case ACCOUNT_UPDATES:
                AccountUpdateMessage accountUpdateMessage = (AccountUpdateMessage) streamMessage;
                System.out.println("\nReceived Account Update: \n\t" +
                        accountUpdateMessage.toString().replace(",", ",\n\t"));
                break;
            case TRADE_UPDATES:
                TradeUpdateMessage tradeUpdateMessage = (TradeUpdateMessage) streamMessage;
                System.out.println("\nReceived Order Update: \n\t" +
                        tradeUpdateMessage.toString().replace(",", ",\n\t"));
                break;
        }
    }
});

// Get Account Information
try {
    Account alpacaAccount = alpacaAPI.getAccount();

    System.out.println("\n\nAccount Information:");
    System.out.println("\t" + alpacaAccount.toString().replace(",", ",\n\t"));
} catch (AlpacaAPIRequestException e) {
    e.printStackTrace();
}

// Request an Order
try {
    Order aaplLimitOrder = alpacaAPI.requestNewLimitOrder("AAPL", 1, OrderSide.BUY, OrderTimeInForce.DAY,
            201.30, true, null);

    System.out.println("\n\nNew AAPL Order:");
    System.out.println("\t" + aaplLimitOrder.toString().replace(",", ",\n\t"));
} catch (AlpacaAPIRequestException e) {
    e.printStackTrace();
}

// Create watchlist
try {
    Watchlist dayTradeWatchlist = alpacaAPI.createWatchlist("Day Trade", "AAPL");

    System.out.println("\n\nDay Trade Watchlist:");
    System.out.println("\t" + dayTradeWatchlist.toString().replace(",", ",\n\t"));
} catch (AlpacaAPIRequestException e) {
    e.printStackTrace();
}

// Get bars
try {
    ZonedDateTime start = ZonedDateTime.of(2019, 11, 18, 0, 0, 0, 0, ZoneId.of("America/New_York"));
    ZonedDateTime end = ZonedDateTime.of(2019, 11, 22, 23, 59, 0, 0, ZoneId.of("America/New_York"));

    Map<String, ArrayList<Bar>> bars = alpacaAPI.getBars(BarsTimeFrame.DAY, "AAPL", null, start, end,
            null, null);

    System.out.println("\n\nBars response:");
    for (Bar bar : bars.get("AAPL")) {
        System.out.println("\t==========");
        System.out.println("\tUnix Time " + ZonedDateTime.ofInstant(Instant.ofEpochSecond(bar.getT()),
                ZoneOffset.UTC));
        System.out.println("\tOpen: $" + bar.getO());
        System.out.println("\tHigh: $" + bar.getH());
        System.out.println("\tLow: $" + bar.getL());
        System.out.println("\tClose: $" + bar.getC());
        System.out.println("\tVolume: " + bar.getV());
    }
} catch (AlpacaAPIRequestException e) {
    e.printStackTrace();
}

// Keep the Alpaca websocket stream open for 5 seconds
try {
    Thread.sleep(5000);
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

This code will output the following:

```
Account Information:
        io.github.mainstringargs.domain.alpaca.account.Account@626abbd0[id=<account id>,
        accountNumber=<account number>,
        status=ACTIVE,
        currency=USD,
        cash=6982.96,
        portfolioValue=99717.04,
        patternDayTrader=false,
        tradeSuspendedByUser=false,
        tradingBlocked=false,
        transfersBlocked=false,
        accountBlocked=false,
        createdAt=2019-08-11T22:19:17.959522Z,
        shortingEnabled=true,
        longMarketValue=106700,
        shortMarketValue=0,
        equity=99717.04,
        lastEquity=97753.04,
        multiplier=4,
        buyingPower=265328.96,
        initialMargin=53350,
        maintenanceMargin=32010,
        sma=0,
        daytradeCount=0,
        lastMaintenanceMargin=31420.8,
        daytradingBuyingPower=265328.96,
        regtBuyingPower=92734.08]

New AAPL Order:
        io.github.mainstringargs.domain.alpaca.order.Order@37313c65[id=<order id>,
        clientOrderId=929e412f-32b6-4ddf-9729-d77a59c19ec8,
        createdAt=2019-11-26T03:41:10.475959154Z,
        updatedAt=2019-11-26T03:41:10.481465924Z,
        submittedAt=2019-11-26T03:41:10.464752859Z,
        filledAt=<null>,
        expiredAt=<null>,
        canceledAt=<null>,
        failedAt=<null>,
        assetId=b0b6dd9d-8b9b-48a9-ba46-b9d54906e415,
        symbol=AAPL,
        assetClass=us_equity,
        qty=1,
        filledQty=0,
        type=limit,
        side=buy,
        timeInForce=day,
        limitPrice=201.3,
        stopPrice=<null>,
        filledAvgPrice=<null>,
        status=new,
        extendedHours=true]

Received Order Update: 
	 OrderUpdateMessage [event=NEW,
            price=null,
            timestamp=null,
            order=io.github.mainstringargs.alpaca.domain.Order@5c728415[id=<order id>,
            clientOrderId=e23ca503-ebec-4754-b65a-ee42806fc67d,
            createdAt=2019-11-23T14:41:05.503352Z,
            updatedAt=2019-11-23T14:41:05.599835871Z,
            submittedAt=2019-11-23T14:41:05.445148Z,
            filledAt=<null>,
            expiredAt=<null>,
            canceledAt=<null>,
            failedAt=<null>,
            assetId=b0b6dd9d-8b9b-48a9-ba46-b9d54906e415,
            symbol=AAPL,
            exchange=<null>,
            assetClass=us_equity,
            qty=1,
            filledQty=0,
            type=limit,
            side=buy,
            timeInForce=day,
            limitPrice=100,
            stopPrice=<null>,
            filledAvgPrice=<null>,
            status=new]]


Day Trade Watchlist:
        io.github.mainstringargs.domain.alpaca.watchlist.Watchlist@34bde49d[id=7c414350-79d8-4527-8892-f1667faa712a,
        createdAt=2019-11-26T03:20:31.953679Z,
        updatedAt=2019-11-26T03:20:31.953679Z,
        name=Day Trade,
        accountId=<account id>,
        assets=[io.github.mainstringargs.domain.alpaca.asset.Asset@1b1cfb87[id=b0b6dd9d-8b9b-48a9-ba46-b9d54906e415,
        _class=us_equity,
        exchange=NASDAQ,
        symbol=AAPL,
        status=active,
        tradable=true,
        marginable=true,
        shortable=true,
        easyToBorrow=true]]]

Bars response:
        ==========
        Unix Time 2019-11-18T05:00Z
        Open: $265.8
        High: $267.43
        Low: $264.23
        Close: $267.12
        Volume: 1.8045658E7
        ==========
        Unix Time 2019-11-19T05:00Z
        Open: $267.9
        High: $268.0
        Low: $265.3926
        Close: $266.29
        Volume: 1.4685205E7
        ==========
        Unix Time 2019-11-20T05:00Z
        Open: $265.54
        High: $266.083
        Low: $260.4
        Close: $263.22
        Volume: 2.2181038E7
        ==========
        Unix Time 2019-11-21T05:00Z
        Open: $263.71
        High: $264.005
        Low: $261.18
        Close: $262.01
        Volume: 2.5411879E7
        ==========
        Unix Time 2019-11-22T05:00Z
        Open: $262.59
        High: $263.18
        Low: $260.84
        Close: $261.84
        Volume: 1.4021695E7
```

# PolygonAPI Example

This example uses the `PolygonAPI` class to subscribe to the Polygon websocket stream, get stocks splits, and get aggregates. Click [here](https://polygon.io/docs/) for the general Polygon API documentation and click [here](https://javadoc.io/doc/io.github.mainstringargs/alpaca-java/5.0.0/io/github/mainstringargs/polygon/PolygonAPI.html) for the `PolygonAPI` javadoc.

```java
// This will use the key_id in the alpaca.properties file by default
PolygonAPI polygonAPI = new PolygonAPI();

String aaplTicker = "AAPL";

// Add a Polygon stream listener to listen to "T.AAPL", "Q.AAPL", "A.AAPL", "AM.AAPL", and status messages
polygonAPI.addPolygonStreamListener(new PolygonStreamListenerAdapter(aaplTicker,
        PolygonStreamMessageType.values()) {
    @Override
    public void onStreamUpdate(PolygonStreamMessageType streamMessageType, PolygonStreamMessage streamMessage) {
        System.out.println("===> streamUpdate " + streamMessageType + " " + streamMessage);
    }
});

// Sleep the current thread for 2 seconds so we can see some trade/quote/aggregates updates on the stream!
try {
    Thread.sleep(2000);
} catch (InterruptedException e) {
    e.printStackTrace();
}

try {
    StockSplitsResponse stockSplitsResponse = polygonAPI.getStockSplits(aaplTicker);

    System.out.println(aaplTicker + " Stock Split Response:");
    System.out.println("\tStatus: " + stockSplitsResponse.getStatus());
    System.out.println("\tCount: " + stockSplitsResponse.getCount());
    for (StockSplit stockSplit : stockSplitsResponse.getResults()) {
        System.out.println("\t" + stockSplit.toString().replace(",", ",\n\t"));
    }
} catch (PolygonAPIRequestException e) {
    e.printStackTrace();
}

try {
    AggregatesResponse aggregatesResponse = polygonAPI.getAggregates(aaplTicker, 1, Timespan.DAY,
            LocalDate.of(2019, 11, 18), LocalDate.of(2019, 11, 25), false);

    System.out.println("Aggregate Response:");
    System.out.println("\tStatus: " + aggregatesResponse.getStatus());
    System.out.println("\tCount: " + aggregatesResponse.getResultsCount());
    for (Aggregate aggregate : aggregatesResponse.getResults()) {
        System.out.println("\t" + aggregate.toString().replace(",", ",\n\t"));
    }
} catch (PolygonAPIRequestException e) {
    e.printStackTrace();
}
```
This code will output the following:

```
[INFO ] 2019-11-25 21:33:20.456 [main] PolygonWebsocketClient - Connected.
[DEBUG] 2019-11-25 21:33:20.485 [main] PolygonWebsocketClientEndpoint - sendMessage {"action":"auth","params":"<my key ID>"}
[INFO ] 2019-11-25 21:33:20.489 [main] PolygonWebsocketClient - Subscribing to A.AAPL
[INFO ] 2019-11-25 21:33:20.489 [main] PolygonWebsocketClient - Subscribing to T.AAPL
[INFO ] 2019-11-25 21:33:20.489 [main] PolygonWebsocketClient - Subscribing to AM.AAPL
[INFO ] 2019-11-25 21:33:20.489 [main] PolygonWebsocketClient - Subscribing to Q.AAPL
[DEBUG] 2019-11-25 21:33:20.491 [main] PolygonWebsocketClientEndpoint - sendMessage {"action":"subscribe","params":"A.AAPL,T.AAPL,AM.AAPL,Q.AAPL"}
[INFO ] 2019-11-25 21:33:20.492 [main] PolygonWebsocketClient - Subscriptions updated to {AAPL=[AGGREGATE_PER_SECOND, TRADES, AGGREGATE_PER_MINUTE, QUOTES]}
[INFO] 2019-11-25 21:33:20.508 [PolygonWebsocketThread] PolygonWebsocketClient - Channel status: io.github.mainstringargs.domain.polygon.websocket.ChannelStatus@78428bcb[ev=status,status=connected,message=Connected Successfully]
[INFO] 2019-11-25 21:33:20.667 [PolygonWebsocketThread] PolygonWebsocketClient - Channel status: io.github.mainstringargs.domain.polygon.websocket.ChannelStatus@629dc521[ev=status,status=auth_success,message=authenticated]
[INFO] 2019-11-25 21:33:20.735 [PolygonWebsocketThread] PolygonWebsocketClient - Channel status: io.github.mainstringargs.domain.polygon.websocket.ChannelStatus@7f4d9c50[ev=status,status=success,message=subscribed to: A.AAPL]
[INFO] 2019-11-25 21:33:20.737 [PolygonWebsocketThread] PolygonWebsocketClient - Channel status: io.github.mainstringargs.domain.polygon.websocket.ChannelStatus@2aa9728c[ev=status,status=success,message=subscribed to: T.AAPL]
[INFO] 2019-11-25 21:33:20.738 [PolygonWebsocketThread] PolygonWebsocketClient - Channel status: io.github.mainstringargs.domain.polygon.websocket.ChannelStatus@4659b8c1[ev=status,status=success,message=subscribed to: AM.AAPL]
[INFO] 2019-11-25 21:33:20.738 [PolygonWebsocketThread] PolygonWebsocketClient - Channel status: io.github.mainstringargs.domain.polygon.websocket.ChannelStatus@4a61d9e6[ev=status,status=success,message=subscribed to: Q.AAPL]
===> streamUpdate QUOTES QuotesMessage [ticker=AAPL, channelType=QUOTES, stockQuote=io.github.mainstringargs.polygon.domain.StockQuote@4d5b710e[ev=<null>,sym=AAPL,bx=12,bp=124.86,bs=6,ax=12,ap=124.87,as=5,c=1,t=1559143439610], timestamp=2019-05-29T10:23:59.610]
===> streamUpdate QUOTES QuotesMessage [ticker=AAPL, channelType=QUOTES, stockQuote=io.github.mainstringargs.polygon.domain.StockQuote@16b79976[ev=<null>,sym=AAPL,bx=12,bp=124.86,bs=4,ax=12,ap=124.87,as=5,c=1,t=1559143439712], timestamp=2019-05-29T10:23:59.712]
===> streamUpdate TRADES TradesMessage [ticker=AAPL, channelType=TRADES, stockTrade=io.github.mainstringargs.polygon.domain.StockTrade@32b4aa49[ev=<null>,sym=AAPL,x=4,p=124.86,s=100,c=[],t=1559143439772], timestamp=2019-05-29T10:23:59.772]
===> streamUpdate AGGREGATE_PER_SECOND AggregateMessage [ticker=AAPL, channelType=AGGREGATE_PER_SECOND, stockAggregate=io.github.mainstringargs.polygon.domain.StockAggregate@5ace47f7[ev=<null>,sym=AAPL,v=1131,av=6602041,op=125.5,vw=124.9778,o=124.8665,c=124.86,h=124.8665,l=124.86,a=124.8636,s=1559143439000,e=1559143440000], start=2019-05-29T10:23:59, end=2019-05-29T10:24]

AAPL Stock Split Response:
        Status: OK
        Count: 3
        io.github.mainstringargs.domain.polygon.stocksplits.StockSplit@6b09fb41[ticker=AAPL,
        exDate=2014-06-09,
        paymentDate=2014-06-09,
        recordDate=<null>,
        declaredDate=<null>,
        ratio=0.14285714285714285,
        tofactor=<null>,
        forfactor=<null>]
        ...

Aggregate Response:
        Status: OK
        Count: 5.0
        io.github.mainstringargs.domain.polygon.aggregates.Aggregate@6cd24612[ticker=<null>,
        v=23984783,
        o=265.8,
        c=267.1,
        h=267.43,
        l=264.23,
        t=1574053200000,
        n=1]
        io.github.mainstringargs.domain.polygon.aggregates.Aggregate@5dafbe45[ticker=<null>,
        v=21208039,
        o=267.9,
        c=266.29,
        h=268.0,
        l=265.3926,
        t=1574139600000,
        n=1]
        ...
```
