<p align="center"><img src="https://i.imgur.com/bikpBmA.png"></p>
<p align="center">
<a href="https://travis-ci.org/mainstringargs/alpaca-java"><img src="https://travis-ci.org/mainstringargs/alpaca-java.svg?branch=master" alt="Build Status"></a>
<a href="https://javadoc.io/doc/io.github.mainstringargs/alpaca-java"><img src="https://javadoc.io/badge/io.github.mainstringargs/alpaca-java.svg" alt="Javadocs"></a>
</p>

# Overview
This is a Java implementation for <a href="https://alpaca.markets/">Alpaca</a>. Alpaca API lets you build and trade with real-time market data for free. 

## Table of Contents
1. [Alpaca Java Building](#alpaca-java-building)
2. [Alpaca Java Gradle Integration](#alpaca-java-gradle-integration)
3. [Alpaca Java Maven Integration](#alpaca-java-maven-integration)
4. [Configuration](#configuration)
5. [Simple Alpaca Example](#simple-alpaca-example)
7. [Simple Polygon Example](#simple-polygon-example)

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
	compile "io.github.mainstringargs:alpaca-java:4.0.1"
}
```

## Alpaca Java Maven Integration

Add the following dependency to your pom.xml file:

```
    <dependency>
      <groupId>io.github.mainstringargs</groupId>
      <artifactId>alpaca-java</artifactId>
      <version>4.0.1</version>
      <scope>compile</scope>
    </dependency>
```
## Configuration

If you plan on using the alpaca.properties, generate a secret and a key at Alpaca and set the following properties in an alpaca.properties file on the classpath:

```
api_version = <v1 or v2>
key_id = <YOUR KEY>
secret = <YOUR SECRET>
base_url = https://api.alpaca.markets
```
Similarly, set the following properties in a polygon.properties file on the classpath for using the PolygonAPI:
```
#key_id will default to what is set in alpaca.properties
key_id = <YOUR KEY>
base_url = https://api.polygon.io
nats_urls = wss://alpaca.socket.polygon.io/stocks
```

## Simple Alpaca Example

This example uses the AlpacaAPI class to print out account information, submit a limit order, and print out bars. Click [here](https://docs.alpaca.markets/api-documentation/api-v2/) for the general Alpaca API documentation and click [here](https://javadoc.io/doc/io.github.mainstringargs/alpaca-java/4.0.1/io/github/mainstringargs/alpaca/AlpacaAPI.html) for the `AlpacaAPI` javadoc.

```java
// This logs into Alpaca using the alpaca.properties file on the classpath.
AlpacaAPI alpacaApi = new AlpacaAPI();

// Register explicitly for ACCOUNT_UPDATES and ORDER_UPDATES Messages via stream listener
alpacaApi.addAlpacaStreamListener(new AlpacaStreamListenerAdapter(MessageType.ACCOUNT_UPDATES,
        MessageType.ORDER_UPDATES) {
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
    System.out.println("\tCreated At: " + TimeUtil.fromDateTimeString(alpacaAccount.getCreatedAt()) +
            "\n\tBuying Power: " + alpacaAccount.getBuyingPower() +
            "\n\tPortfolio Value: " + alpacaAccount.getPortfolioValue());
} catch (AlpacaAPIException e) {
    e.printStackTrace();
}

// Get Stock Market Hours
try {
    Clock alpacaClock = alpacaApi.getClock();

    System.out.println("\n\nClock:");
    System.out.println("\tCurrent Time: " +
            TimeUtil.fromDateTimeString(alpacaClock.getTimestamp()) + "\n\tIs Open: " +
            alpacaClock.isIsOpen() + "\n\tMarket Next Open Time: " +
            TimeUtil.fromDateTimeString(alpacaClock.getNextOpen()) + "\n\tMark Next Close Time: " +
            TimeUtil.fromDateTimeString(alpacaClock.getNextClose()));
} catch (AlpacaAPIException e) {
    e.printStackTrace();
}

Order limitOrder = null;
String orderClientId = UUID.randomUUID().toString();

// Request an Order
try {
    // Lets submit a limit order for when AMZN gets down to $10.0!
    limitOrder = alpacaApi.requestNewOrder("AMZN", 1, OrderSide.BUY, OrderType.LIMIT,
            OrderTimeInForce.DAY, 10.0, null, orderClientId);

    System.out.println("\n\nLimit Order Response:");
    System.out.println("\tSymbol: " + limitOrder.getSymbol() +
            "\n\tClient Order Id: " + limitOrder.getClientOrderId() +
            "\n\tQty: " + limitOrder.getQty() +
            "\n\tType: " + limitOrder.getType() +
            "\n\tLimit Price: $" + limitOrder.getLimitPrice() +
            "\n\tCreated At: " + TimeUtil.fromDateTimeString(limitOrder.getCreatedAt()));
} catch (AlpacaAPIException e) {
    e.printStackTrace();
}

// Get an existing Order by Id
try {
    Order limitOrderById = alpacaApi.getOrder(limitOrder.getId());

    System.out.println("\n\nLimit Order By Id Response:");
    System.out.println("\tSymbol: " + limitOrderById.getSymbol() +
            "\n\tClient Order Id: " + limitOrderById.getClientOrderId() +
            "\n\tQty: " + limitOrderById.getQty() +
            "\n\tType: " + limitOrderById.getType() +
            "\n\tLimit Price: $" + limitOrderById.getLimitPrice() +
            "\n\tCreated At: " + TimeUtil.fromDateTimeString(limitOrderById.getCreatedAt()));
} catch (AlpacaAPIException e) {
    e.printStackTrace();
}

// Get an existing Order by Client Id
try {
    Order limitOrderByClientId = alpacaApi.getOrderByClientId(limitOrder.getClientOrderId());

    System.out.println("\n\nLimit Order By Id Response:");
    System.out.println("\tSymbol: " + limitOrderByClientId.getSymbol() +
            "\n\tClient Order Id: " + limitOrderByClientId.getClientOrderId() +
            "\n\tQty: " + limitOrderByClientId.getQty() +
            "\n\tType: " + limitOrderByClientId.getType() +
            "\n\tLimit Price: $" + limitOrderByClientId.getLimitPrice() +
            "\n\tCreated At: " + TimeUtil.fromDateTimeString(limitOrderByClientId.getCreatedAt()));
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

# Simple Polygon Example

This example uses the PolygonAPI class to provide sample calls into the PolygonAPI endpoints. Click [here](https://polygon.io/docs/) for the general Polygon API documentation and click [here](https://javadoc.io/doc/io.github.mainstringargs/alpaca-java/4.0.1/io/github/mainstringargs/polygon/PolygonAPI.html) for the `PolygonAPI` javadoc.

```java
PolygonAPI polygonAPI = new PolygonAPI();

String ticker = "MSFT";

try {
    SymbolEndpoints symbolEndPoints = polygonAPI.getSymbolEndpoints(ticker);

    System.out.println("\n\n" + ticker + " Symbol Endpoints:");
    System.out.println("\t" + symbolEndPoints.getSymbol().getSymbol() + " "
            + symbolEndPoints.getSymbol().getName() + " " + symbolEndPoints.getSymbol().getType()
            + symbolEndPoints.getSymbol().getUrl() + " " + symbolEndPoints.getSymbol().getUpdated());
    System.out.println("\t" + symbolEndPoints.getEndpoints());
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    TickerDetails symbolDetails = polygonAPI.getTickerDetails(ticker);

    System.out.println("\n\n" + ticker + " Symbol Details:");
    System.out.println("\t" + symbolDetails);
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    SymbolAnalystRatings symbolAnalystRatings = polygonAPI.getSymbolAnalystRatings(ticker);

    System.out.println("\n\n" + ticker + " Symbol Analyst Ratings:");
    System.out.println("\t" + symbolAnalystRatings);
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<StockDividend> symbolDividends = polygonAPI.getStockDividends(ticker);

    System.out.println("\n\n" + ticker + " Stock Dividends:");

    for (StockDividend div : symbolDividends) {
        System.out.println("\t" + div);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<SymbolEarning> symbolEarnings = polygonAPI.getSymbolEarnings(ticker);

    System.out.println("\n\n" + ticker + " Symbol Earnings:");

    for (SymbolEarning earning : symbolEarnings) {
        System.out.println("\t" + earning);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<StockFinancial> symbolFinancials = polygonAPI.getStockFinancials(ticker);

    System.out.println("\n\n" + ticker + " Symbol Financials:");
    for (StockFinancial financial : symbolFinancials) {
        System.out.println("\t" + financial);
    }

} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<TickerNews> symbolNews = polygonAPI.getTickerNews(ticker);

    System.out.println("\n\n" + ticker + " Symbol News:");
    for (TickerNews newsItem : symbolNews) {
        System.out.println("\t" + newsItem);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<Ticker> tickers = polygonAPI.getTickers(Sort.TICKER_ASC, null,
            null, Locale.US, "Tech", null, null, null).getTickers();

    System.out.println("\n\n" + "Tech" + " Search US Tickers");
    for (Ticker tickerItem : tickers) {
        System.out.println("\t" + tickerItem);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<Market> markets = polygonAPI.getMarkets();

    System.out.println("\n\n" + " Markets:");
    for (Market market : markets) {
        System.out.println("\t" + market);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<io.github.mainstringargs.domain.polygon.reference.Locale> locales =
            polygonAPI.getLocales();

    System.out.println("\n\n" + " Locales:");
    for (io.github.mainstringargs.domain.polygon.reference.Locale locale : locales) {
        System.out.println("\t" + locale);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    TypesMapping typesMapping = polygonAPI.getTypesMapping();

    System.out.println("\n\n" + "typesMapping");
    System.out.println("\t" + typesMapping.getTypes());
    System.out.println("\t" + typesMapping.getIndexTypes());

} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<StockSplit> splits = polygonAPI.getStockSplits(ticker);

    System.out.println("\n\n" + ticker + " Split:");
    for (StockSplit splitItems : splits) {
        System.out.println("\t" + splitItems);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<Exchange> exchanges = polygonAPI.getExchanges();

    System.out.println("\n\n" + "exchanges");
    for (Exchange exchange : exchanges) {
        System.out.println("\t" + exchange);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    Trades trades = polygonAPI.getHistoricTrades(ticker, LocalDate.of(2019, 5, 7), null, null);

    System.out.println("\n\n" + ticker + " Trades on " + LocalDate.of(2019, 5, 7) + ": ");
    System.out.println("\t" + "map " + trades.getMap());
    System.out.println("\t" + "ticks");
    for (Tick tick : trades.getTicks()) {
        System.out.println("\t" + tick);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    Quotes quotes = polygonAPI.getHistoricQuotes(ticker, LocalDate.of(2019, 5, 7), null, null);

    System.out.println("\n\n" + ticker + " Quotes on " + LocalDate.of(2019, 5, 1) + ": ");
    System.out.println("\t" + "map " + quotes.getMap());
    System.out.println("\t" + "ticks");
    for (io.github.mainstringargs.domain.polygon.historic.quotes.Tick tick : quotes.getTicks()) {
        System.out.println("\t" + tick);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    LastTrade trade = polygonAPI.getLastTrade(ticker);

    System.out.println("\n\n" + ticker + " Last Trade: ");
    System.out.println("\t" + trade);
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    LastQuote quote = polygonAPI.getLastQuote(ticker);

    System.out.println("\n\n" + ticker + " Last Quote: ");
    System.out.println("\t" + quote);
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    DailyOpenClose dailyOpenClose =
            polygonAPI.getDailyOpenClose(ticker, LocalDate.of(2019, 5, 7));

    System.out.println("\n\n" + ticker + " DailyOpenClose on " + LocalDate.of(2019, 5, 1) + ": ");
    System.out.println("\t" + dailyOpenClose);
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    Snapshot snapshot = polygonAPI.getSnapshot(ticker);

    System.out.println("\n\n" + ticker + " Snapshot: ");
    System.out.println("\t" + snapshot);
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<Snapshot> snapshots = polygonAPI.getSnapshotAllTickers();

    System.out.println("\n\n" + "snapshots " + snapshots.size());
    for (Snapshot snapshot : snapshots) {
        System.out.println("\t" + snapshot);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<Snapshot> snapshots = polygonAPI.getSnapshotsGainers();

    System.out.println("\n\n" + "gainers snapshots " + snapshots.size());
    for (Snapshot snapshot : snapshots) {
        System.out.println("\t" + snapshot);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    List<Snapshot> snapshots = polygonAPI.getSnapshotsLosers();

    System.out.println("\n\n" + "losers snapshots " + snapshots.size());
    for (Snapshot snapshot : snapshots) {
        System.out.println("\t" + snapshot);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    Aggregates aggs = polygonAPI.getPreviousClose(ticker, false);

    System.out.println("\n\n" + "previous close aggregates " + aggs.getResultsCount());
    for (Result result : aggs.getResults()) {
        System.out.println("\t" + result);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    Aggregates aggs = polygonAPI.getAggregates(ticker, null, Timespan.Hour,
            LocalDate.of(2019, 4, 23), LocalDate.of(2019, 4, 26), false);

    System.out.println("\n\n" + "Aggs over time " + aggs.getResultsCount());
    for (Result result : aggs.getResults()) {
        System.out.println("\t" + result);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

try {
    Aggregates aggs = polygonAPI.getGroupedDaily(Locale.US,
            io.github.mainstringargs.polygon.enums.Market.STOCKS, LocalDate.of(2019, 4, 26), false);

    System.out.println("\n\n" + "Grouped Daily " + aggs.getResultsCount());
    for (Result result : aggs.getResults()) {
        System.out.println("\t" + result);
    }
} catch (PolygonAPIException e) {
    e.printStackTrace();
}

polygonAPI.addPolygonStreamListener(new PolygonStreamListenerAdapter(ticker, ChannelType.values()) {
    @Override
    public void streamUpdate(String ticker, ChannelType channelType, ChannelMessage message) {
        System.out.println("===> streamUpdate " + ticker + " " + channelType + " " + message);
    }
});

try {
    Thread.sleep(10000L);
} catch (InterruptedException e) {
    e.printStackTrace();
}
```
This code will output the following:

```
MSFT Symbol Endpoints:
	MSFT Microsoft Corporation Common Stock cshttps://api.polygon.io/v1/meta/symbols/MSFT 05/29/2019
	io.github.mainstringargs.polygon.domain.meta.Endpoints@7e70bd39[company=https://api.polygon.io/v1/meta/symbols/MSFT/company,analysts=https://api.polygon.io/v1/meta/symbols/MSFT/analysts,dividends=https://api.polygon.io/v1/meta/symbols/MSFT/dividends,splits=https://api.polygon.io/v1/meta/symbols/MSFT/splits,news=https://api.polygon.io/v1/meta/symbols/MSFT/news]


MSFT Symbol Details:
	io.github.mainstringargs.polygon.domain.meta.SymbolDetails@5c1bd44c[logo=https://s3.polygon.io/logos/msft/logo.png,exchange=Nasdaq Global Select,name=Microsoft Corporation,symbol=MSFT,listdate=1987-09-17,cik=789019,bloomberg=EQ0010174300001000,figi=<null>,lei=INR2EJN1ERAN0W5ZP974,sic=7372,country=usa,industry=Application Software,sector=Technology,marketcap=823504745998,employees=131000,phone=+1 425 882-8080,ceo=Satya Nadella,url=http://www.microsoft.com,description=Microsoft Corp is a technology company. It develop, license, and support a wide range of software products and services. Its business is organized into three segments: Productivity and Business Processes, Intelligent Cloud, and More Personal Computing.,similar=[CSCO, AAPL, SAP, RHT, IBM, ORCL, HPQ, GOOGL, XLK],tags=[Technology, Software - Infrastructure, Application Software],updated=11/16/2018]


MSFT Symbol Analyst Ratings:
	io.github.mainstringargs.polygon.domain.meta.SymbolAnalystRatings@4152d38d[symbol=MSFT,analysts=23,change=0.01,strongBuy=io.github.mainstringargs.polygon.domain.meta.StrongBuy@3591009c[current=21,month1=22,month2=22,month3=22,month4=0,month5=0],buy=io.github.mainstringargs.polygon.domain.meta.Buy@5398edd0[current=0,month1=0,month2=1,month3=1,month4=0,month5=0],hold=io.github.mainstringargs.polygon.domain.meta.Hold@b5cc23a[current=1,month1=1,month2=1,month3=2,month4=0,month5=0],sell=io.github.mainstringargs.polygon.domain.meta.Sell@5cc5b667[current=1,month1=1,month2=1,month3=1,month4=0,month5=0],strongSell=io.github.mainstringargs.polygon.domain.meta.StrongSell@61edc883[current=0,month1=0,month2=0,month3=0,month4=0,month5=0],updated=12/05/2018]


MSFT Symbol Dividends:
	io.github.mainstringargs.polygon.domain.meta.SymbolDividend@6db66836[symbol=MSFT,type=D,exDate=11/14/2018,paymentDate=12/13/2018,recordDate=11/15/2018,declaredDate=09/18/2018,amount=0.46,qualified=Q,flag=<null>]
	io.github.mainstringargs.polygon.domain.meta.SymbolDividend@db44aa2[symbol=MSFT,type=D,exDate=08/15/2018,paymentDate=09/13/2018,recordDate=08/16/2018,declaredDate=06/13/2018,amount=0.42,qualified=Q,flag=<null>]
<Truncated>


MSFT Symbol Earnings:
	io.github.mainstringargs.polygon.domain.meta.SymbolEarning@46ab18da[symbol=MSFT,ePSReportDate=10/24/2018,ePSReportDateStr=<null>,fiscalPeriod=Q1 2019,fiscalEndDate=09/30/2018,actualEPS=1.14,consensusEPS=0.96,estimatedEPS=0.96,announceTime=AMC,numberOfEstimates=15,ePSSurpriseDollar=0.18,yearAgo=0.0,yearAgoChangePercent=0,estimatedChangePercent=0]
	io.github.mainstringargs.polygon.domain.meta.SymbolEarning@790174f2[symbol=MSFT,ePSReportDate=07/19/2018,ePSReportDateStr=<null>,fiscalPeriod=Q4 2018,fiscalEndDate=06/30/2018,actualEPS=1.13,consensusEPS=1.07,estimatedEPS=1.07,announceTime=AMC,numberOfEstimates=14,ePSSurpriseDollar=0.06,yearAgo=0.0,yearAgoChangePercent=0,estimatedChangePercent=0]
<Truncated>



MSFT Symbol Financials:
	io.github.mainstringargs.polygon.domain.meta.SymbolFinancial@37cd92d6[symbol=MSFT,reportDate=2018-09-30T00:00:00.000Z,reportDateStr=2018-09-30,grossProfit=19179000000,costOfRevenue=9905000000,operatingRevenue=29084000000,totalRevenue=29084000000,operatingIncome=9955000000,netIncome=8824000000,researchAndDevelopment=3977000000,operatingExpense=9224000000,currentAssets=164195000000,totalAssets=257619000000,totalLiabilities=0,currentCash=15137000000,currentDebt=6497000000,totalCash=135880000000,totalDebt=76230000000,shareholderEquity=85967000000,cashChange=3320000000,cashFlow=13657000000,operatingGainsLosses=-2681000000]
	io.github.mainstringargs.polygon.domain.meta.SymbolFinancial@5922ae77[symbol=MSFT,reportDate=2018-06-30T00:00:00.000Z,reportDateStr=2018-06-30,grossProfit=20343000000,costOfRevenue=9742000000,operatingRevenue=30085000000,totalRevenue=30085000000,operatingIncome=10379000000,netIncome=8873000000,researchAndDevelopment=3933000000,operatingExpense=9964000000,currentAssets=169662000000,totalAssets=258848000000,totalLiabilities=0,currentCash=11946000000,currentDebt=3998000000,totalCash=133768000000,totalDebt=76240000000,shareholderEquity=82718000000,cashChange=2709000000,cashFlow=11418000000,operatingGainsLosses=8135000000]
<Truncated>



MSFT Symbol News:
	io.github.mainstringargs.polygon.domain.meta.SymbolNews@4108fa66[symbols=[MSFT],title=Microsoft: The Coming June Pullback Is A Great Buying Opportunity,url=https://seekingalpha.com/article/4266816-microsoft-coming-june-pullback-great-buying-opportunity?,source=SeekingAlpha,summary=In the Short Term, Expect Bullishness Microsoft ( MSFT ) has been consolidating, trading sideways, after hitting a high in April, in response to its Q3 earnings report. The up gap after earnings filled, proving itself not to be a breakaway gap , and in its wake, we are now facing two down…,image=<null>,timestamp=2019-05-28T16:59:05.000Z,keywords=[appsoftw, msft, nasdaq01, sof31165134, computing and information technology, wompolix]]
	io.github.mainstringargs.polygon.domain.meta.SymbolNews@1f130eaf[symbols=[MSFT],title=Microsoft: Valuation Update,url=https://seekingalpha.com/article/4266760-microsoft-valuation-update,source=SeekingAlpha,summary=Microsoft's stock price has been growing at a faster than exponential rate. It means a very unstable state. Microsoft is overvalued in terms of analysis of inte,image=https://static1.seekingalpha.com/uploads/2019/5/24/saupload_Microsoft.jpg,timestamp=2019-05-28T14:50:33.000Z,keywords=[appsoftw, msft, nasdaq01, sof31165134, computing and information technology]]
<Truncated>



Tech Search US Tickers
	io.github.mainstringargs.polygon.domain.reference.Ticker@352c308[ticker=TECH,name=Bio-Techne Corp,market=STOCKS,locale=US,currency=USD,active=true,primaryExch=NGS,updated=2019-05-29,url=https://api.polygon.io/v2/tickers/TECH]
	io.github.mainstringargs.polygon.domain.reference.Ticker@7d373bcf[ticker=LHTCF,name=Launch Tech,market=STOCKS,locale=US,currency=USD,active=true,primaryExch=GREY,updated=2019-05-29,url=https://api.polygon.io/v2/tickers/LHTCF]
<Truncated>


 Markets:
	io.github.mainstringargs.polygon.domain.reference.Market@aec50a1[market=STOCKS,desc=Stocks / Equities / ETFs]
	io.github.mainstringargs.polygon.domain.reference.Market@2555fff0[market=INDICES,desc=Indices]
	io.github.mainstringargs.polygon.domain.reference.Market@70d2e40b[market=MF,desc=Mutual Funds]
	io.github.mainstringargs.polygon.domain.reference.Market@120f38e6[market=MMF,desc=Money Market Funds]
	io.github.mainstringargs.polygon.domain.reference.Market@7a0e1b5e[market=CRYPTO,desc=Crypto]
	io.github.mainstringargs.polygon.domain.reference.Market@702ed190[market=FX,desc=Forex / Currencies]
	io.github.mainstringargs.polygon.domain.reference.Market@173b9122[market=BONDS,desc=Bonds]


 Locales:
	io.github.mainstringargs.polygon.domain.reference.Locale@42a9a63e[locale=G,name=Global]
	io.github.mainstringargs.polygon.domain.reference.Locale@62da83ed[locale=US,name=United States of America]
	io.github.mainstringargs.polygon.domain.reference.Locale@5d8445d7[locale=GB,name=Great Britain]
	io.github.mainstringargs.polygon.domain.reference.Locale@37d80fe7[locale=CA,name=Canada]
	io.github.mainstringargs.polygon.domain.reference.Locale@384fc774[locale=NL,name=Netherlands]
	io.github.mainstringargs.polygon.domain.reference.Locale@e3cee7b[locale=GR,name=Greece]
	io.github.mainstringargs.polygon.domain.reference.Locale@71e9a896[locale=SP,name=Spain]
	io.github.mainstringargs.polygon.domain.reference.Locale@6b9267b[locale=DE,name=Germany]
	io.github.mainstringargs.polygon.domain.reference.Locale@408b35bf[locale=BE,name=Belgium]
	io.github.mainstringargs.polygon.domain.reference.Locale@29ad44e3[locale=DK,name=Denmark]
	io.github.mainstringargs.polygon.domain.reference.Locale@15bcf458[locale=FI,name=Finland]
	io.github.mainstringargs.polygon.domain.reference.Locale@5af9926a[locale=IE,name=Ireland]
	io.github.mainstringargs.polygon.domain.reference.Locale@43c67247[locale=PT,name=Portugal]
	io.github.mainstringargs.polygon.domain.reference.Locale@fac80[locale=IN,name=India]
	io.github.mainstringargs.polygon.domain.reference.Locale@726386ed[locale=MX,name=Mexico]
	io.github.mainstringargs.polygon.domain.reference.Locale@649f2009[locale=FR,name=France]
	io.github.mainstringargs.polygon.domain.reference.Locale@14bb2297[locale=CN,name=China]
	io.github.mainstringargs.polygon.domain.reference.Locale@69adf72c[locale=CH,name=Switzerland]
	io.github.mainstringargs.polygon.domain.reference.Locale@797501a[locale=SE,name=Sweden]


typesMapping
	io.github.mainstringargs.polygon.domain.reference.Types@241a53ef[cs=Common Stock,adr=American Depository Receipt,nvdr=Non-Voting Depository Receipt,gdr=Global Depositary Receipt,sdr=Special Drawing Right,cef=Closed-End Fund,etp=Exchange Traded Product/Fund,reit=Real Estate Investment Trust,mlp=Master Limited Partnership,wrt=Equity WRT,pub=Public,nyrs=New York Registry Shares,unit=Unit,right=Right,trak=Tracking stock or targeted stock,ltdp=Limited Partnership,rylt=Royalty Trust,mf=Mutual Fund,pfd=Preferred Stock,fdr=Foreign Ordinary Shares,ost=Other Security Type,fund=Fund,sp=Structured Product,si=Secondary Issue]
	io.github.mainstringargs.polygon.domain.reference.IndexTypes@344344fa[index=Index,etf=Exchange Traded Fund (ETF),etn=Exchange Traded Note (ETN),etmf=Exchange Traded Managed Fund (ETMF),settlement=Settlement,spot=Spot,subprod=Subordinated product,wc=World Currency,alphaindex=Alpha Index]


MSFT Split:
	io.github.mainstringargs.polygon.domain.reference.Split@1827a871[ticker=MSFT,exDate=2003-02-17,paymentDate=2003-02-17,ratio=0.5,tofactor=2,forfactor=1,recordDate=<null>,declaredDate=<null>]
	io.github.mainstringargs.polygon.domain.reference.Split@48e64352[ticker=MSFT,exDate=1999-03-28,paymentDate=1999-03-28,ratio=0.5,tofactor=2,forfactor=1,recordDate=<null>,declaredDate=<null>]
<Truncated>


exchanges
	io.github.mainstringargs.polygon.domain.meta.Exchange@7957dc72[id=0,type=TRF,market=equities,mic=TFF,name=Multiple,tape=-,code=<null>]
	io.github.mainstringargs.polygon.domain.meta.Exchange@6ab72419[id=1,type=exchange,market=equities,mic=XASE,name=NYSE American (AMEX),tape=A,code=AMX]
<Truncated>



MSFT Trades on 2019-05-07: 
	map io.github.mainstringargs.polygon.domain.historic.trades.Map@2024293c[c1=condition1,c2=condition2,c3=condition3,c4=condition4,e=exchange,p=price,s=size,t=timestamp]
	ticks
	io.github.mainstringargs.polygon.domain.historic.trades.Tick@7048f722[c1=12,c2=37,c3=0,c4=0,e=11,p=127.7,s=20,t=1557216000019]
	io.github.mainstringargs.polygon.domain.historic.trades.Tick@c074c0c[c1=12,c2=37,c3=0,c4=0,e=11,p=127.7,s=71,t=1557216000019]
<Truncated>



MSFT Quotes on 2019-05-01: 
	map io.github.mainstringargs.polygon.domain.historic.quotes.Map@444f44c5[aE=askexchange,aP=askprice,aS=asksize,bE=bidexchange,bP=bidprice,bS=bidsize,c=condition,t=timestamp]
	ticks
	io.github.mainstringargs.polygon.domain.historic.quotes.Tick@303f1234[c=34,bE=12,aE=0,bP=80.0,aP=0.0,bS=1,aS=0,t=1557216000006]
	io.github.mainstringargs.polygon.domain.historic.quotes.Tick@24d61e4[c=34,bE=11,aE=0,bP=111.5,aP=0.0,bS=1,aS=0,t=1557216000080]
<Truncated>

MSFT Last Trade: 
	io.github.mainstringargs.polygon.domain.Trade@1b30a54e[price=124.83,size=100,exchange=11,cond1=0,cond2=0,cond3=0,cond4=0,timestamp=1559143430433]


MSFT Last Quote: 
	io.github.mainstringargs.polygon.domain.Quote@3111631d[askprice=124.86,asksize=2,askexchange=12,bidprice=124.85,bidsize=5,bidexchange=12,timestamp=1559143421774]


MSFT DailyOpenClose on 2019-05-01: 
	io.github.mainstringargs.polygon.domain.DailyOpenClose@74123110[from=2019-05-06T03:30:00-04:00,symbol=MSFT,open=126.39,high=128.56,low=126.11,close=128.15,afterHours=127.45,volume=20623892]


MSFT Snapshot: 
	io.github.mainstringargs.polygon.domain.Snapshot@3e6748ae[ticker=MSFT,day=io.github.mainstringargs.polygon.domain.Day@28f154cc[c=125.01,h=125.39,l=124.42,o=125.38,v=6888688.0],lastTrade=io.github.mainstringargs.polygon.domain.LastTrade@3030836d[c1=0,c2=0,c3=0,c4=0,e=0,p=124.83,s=100,t=1559143431348842520],min=io.github.mainstringargs.polygon.domain.Min@6af78a48[c=124.97,h=125.06,l=124.97,o=125.01,v=38357.0],prevDay=io.github.mainstringargs.polygon.domain.PrevDay@6ed18d80[c=126.16,h=128.0,l=126.05,o=126.94,v=2.7264316E7],todaysChange=-1.33,todaysChangePerc=-1.054,updated=1559143431348842520]


snapshots 8184
	io.github.mainstringargs.polygon.domain.Snapshot@134a8ead[ticker=HYI,day=io.github.mainstringargs.polygon.domain.Day@427308f8[c=14.69,h=14.72,l=14.68,o=14.72,v=23173.0],lastTrade=io.github.mainstringargs.polygon.domain.LastTrade@54247647[c1=0,c2=0,c3=0,c4=0,e=0,p=14.67,s=100,t=1559143403555015978],min=io.github.mainstringargs.polygon.domain.Min@4975dda1[c=14.69,h=14.69,l=14.69,o=14.69,v=1400.0],prevDay=io.github.mainstringargs.polygon.domain.PrevDay@e0d1dc4[c=14.72,h=14.9,l=14.67,o=14.9,v=83521.0],todaysChange=-0.05,todaysChangePerc=-0.34,updated=1559143403555015978]
	io.github.mainstringargs.polygon.domain.Snapshot@5463f035[ticker=JFKKW,day=io.github.mainstringargs.polygon.domain.Day@230232b0[c=0.145,h=0.155,l=0.145,o=0.155,v=8060.0],lastTrade=io.github.mainstringargs.polygon.domain.LastTrade@44fd7ba4[c1=0,c2=0,c3=0,c4=0,e=0,p=0.145,s=2700,t=1559136994025600423],min=io.github.mainstringargs.polygon.domain.Min@22f8adc2[c=0.145,h=0.155,l=0.145,o=0.155,v=8060.0],prevDay=io.github.mainstringargs.polygon.domain.PrevDay@69d103f0[c=0.195,h=0.195,l=0.13,o=0.14,v=121680.0],todaysChange=-0.05,todaysChangePerc=-25.641,updated=1559136994025600423]
<Truncated>

gainers snapshots 21
	io.github.mainstringargs.polygon.domain.Snapshot@7fb7be26[ticker=ELTK,day=io.github.mainstringargs.polygon.domain.Day@7da1bf14[c=5.74,h=6.86,l=2.56,o=2.84,v=1.9447144E7],lastTrade=io.github.mainstringargs.polygon.domain.LastTrade@32eb46d7[c1=0,c2=0,c3=0,c4=0,e=0,p=5.73,s=400,t=1559143436430388080],min=io.github.mainstringargs.polygon.domain.Min@7d352874[c=5.7,h=5.92,l=5.56,o=5.6,v=141122.0],prevDay=io.github.mainstringargs.polygon.domain.PrevDay@3f31ff7a[c=1.64,h=1.73,l=1.5377,o=1.72,v=137562.0],todaysChange=4.09,todaysChangePerc=249.39,updated=1559143436430388080]
	io.github.mainstringargs.polygon.domain.Snapshot@4f98ae97[ticker=IMACW,day=io.github.mainstringargs.polygon.domain.Day@27ad844d[c=0.9,h=0.9001,l=0.55,o=0.55,v=62045.0],lastTrade=io.github.mainstringargs.polygon.domain.LastTrade@738f6e44[c1=0,c2=0,c3=0,c4=0,e=0,p=0.9,s=1743,t=1559143413704446538],min=io.github.mainstringargs.polygon.domain.Min@43759560[c=0.9,h=0.9,l=0.9,o=0.9,v=3838.0],prevDay=io.github.mainstringargs.polygon.domain.PrevDay@4913778f[c=0.57,h=0.57,l=0.57,o=0.57,v=2328.0],todaysChange=0.33,todaysChangePerc=57.895,updated=1559143413704446538]
<Truncated>


losers snapshots 21
	io.github.mainstringargs.polygon.domain.Snapshot@396d5c73[ticker=CLRBW,day=io.github.mainstringargs.polygon.domain.Day@5ac30b75[c=0.0212,h=0.0412,l=0.021,o=0.041,v=10070.0],lastTrade=io.github.mainstringargs.polygon.domain.LastTrade@14cc127b[c1=0,c2=0,c3=0,c4=0,e=0,p=0.0212,s=700,t=1559137813312035203],min=io.github.mainstringargs.polygon.domain.Min@10da7696[c=0.0212,h=0.0412,l=0.021,o=0.041,v=10070.0],prevDay=io.github.mainstringargs.polygon.domain.PrevDay@43072e3a[c=0.19,h=0.19,l=0.1868,o=0.1868,v=3000.0],todaysChange=-0.169,todaysChangePerc=-88.842,updated=1559137813312035203]
	io.github.mainstringargs.polygon.domain.Snapshot@15d0d8c[ticker=EYEGW,day=io.github.mainstringargs.polygon.domain.Day@75f69816[c=0.012507,h=0.012507,l=0.0124,o=0.0124,v=3456.0],lastTrade=io.github.mainstringargs.polygon.domain.LastTrade@36db2ef8[c1=0,c2=0,c3=0,c4=0,e=0,p=0.012507,s=1300,t=1559137898974747384],min=io.github.mainstringargs.polygon.domain.Min@4c57b0e7[c=0.012507,h=0.012507,l=0.0124,o=0.0124,v=3456.0],prevDay=io.github.mainstringargs.polygon.domain.PrevDay@258fe58c[c=0.0229,h=0.0229,l=0.0229,o=0.0229,v=8056.0],todaysChange=-0.01,todaysChangePerc=-45.384,updated=1559137898974747384]
<Truncated>


previous close aggregates 1
	io.github.mainstringargs.polygon.domain.aggregate.Result@52ed0d8e[c=126.16,ticker=MSFT,t=1559073600000,v=2.7264316E7,h=128.0,l=126.05,o=126.94]


Aggs over time 48
	io.github.mainstringargs.polygon.domain.aggregate.Result@13a4e21[c=123.98,ticker=MSFT,t=1556006400000,v=2396.0,h=123.99,l=123.85,o=123.85]
	io.github.mainstringargs.polygon.domain.aggregate.Result@317e9159[c=123.97,ticker=MSFT,t=1556010000000,v=1346.0,h=123.98,l=123.9,o=123.98]
<Truncated>

Grouped Daily 8433
	io.github.mainstringargs.polygon.domain.aggregate.Result@259ec563[c=100.04,ticker=IRBT,t=1556308800000,v=1422819.0,h=100.4516,l=96.5,o=99.3]
	io.github.mainstringargs.polygon.domain.aggregate.Result@61f5ee9d[c=35.75,ticker=BXMT,t=1556308800000,v=1198948.0,h=35.79,l=35.58,o=35.64]
<Truncated>

[INFO ] 2019-05-29 10:23:58.463 [main] PolygonNatsClient - Connecting...
[INFO ] 2019-05-29 10:23:58.899 [main] PolygonNatsClient - Connected.
[INFO ] 2019-05-29 10:23:58.927 [main] PolygonNatsClient - Subscribing to AM.MSFT
[INFO ] 2019-05-29 10:23:58.928 [main] PolygonNatsClient - Subscribing to A.MSFT
[INFO ] 2019-05-29 10:23:58.928 [main] PolygonNatsClient - Subscribing to T.MSFT
[INFO ] 2019-05-29 10:23:58.928 [main] PolygonNatsClient - Subscribing to Q.MSFT
[INFO ] 2019-05-29 10:23:58.928 [main] PolygonNatsClient - Subscriptions updated to {MSFT=[AGGREGATE_PER_MINUTE, AGGREGATE_PER_SECOND, TRADES, QUOTES]}
===> streamUpdate MSFT QUOTES QuotesMessage [ticker=MSFT, channelType=QUOTES, stockQuote=io.github.mainstringargs.polygon.domain.StockQuote@4d5b710e[ev=<null>,sym=MSFT,bx=12,bp=124.86,bs=6,ax=12,ap=124.87,as=5,c=1,t=1559143439610], timestamp=2019-05-29T10:23:59.610]
===> streamUpdate MSFT QUOTES QuotesMessage [ticker=MSFT, channelType=QUOTES, stockQuote=io.github.mainstringargs.polygon.domain.StockQuote@16b79976[ev=<null>,sym=MSFT,bx=12,bp=124.86,bs=4,ax=12,ap=124.87,as=5,c=1,t=1559143439712], timestamp=2019-05-29T10:23:59.712]
===> streamUpdate MSFT TRADES TradesMessage [ticker=MSFT, channelType=TRADES, stockTrade=io.github.mainstringargs.polygon.domain.StockTrade@32b4aa49[ev=<null>,sym=MSFT,x=4,p=124.86,s=100,c=[],t=1559143439772], timestamp=2019-05-29T10:23:59.772]
===> streamUpdate MSFT AGGREGATE_PER_SECOND AggregateMessage [ticker=MSFT, channelType=AGGREGATE_PER_SECOND, stockAggregate=io.github.mainstringargs.polygon.domain.StockAggregate@5ace47f7[ev=<null>,sym=MSFT,v=1131,av=6602041,op=125.5,vw=124.9778,o=124.8665,c=124.86,h=124.8665,l=124.86,a=124.8636,s=1559143439000,e=1559143440000], start=2019-05-29T10:23:59, end=2019-05-29T10:24]
```
