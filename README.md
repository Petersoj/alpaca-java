# Yahoo Finance Scraper
Yahoo Finance discontinued their official API, but data can still be accessed.  This project exposes that data as a Java project.  There is an Example included which is intended to show how to access data relevant to a specific company.

To get started, clone the project and run:

```
./gradlew build
```

To try it out, run this command

```
./gradlew run
```

It will output Yahoo's recommendation mean for a couple of stocks.

The Example for code looks like this:

```java
    for (String symbol : args) {

      YahooFinanceUrlBuilder builder =
          new YahooFinanceUrlBuilder().modules(YahooFinanceModules.values()).symbol(symbol);

      YahooFinanceRequest request = new YahooFinanceRequest();

      YahooFinanceData financeData = request.getFinanceData(request.invoke(builder));


      if (financeData.getFinancialData() != null)
        System.out.println(symbol + ": recMean "
            + financeData.getFinancialData().getRecommendationMean().getRaw());
    }
```
Which will output something like this:

```
fb: recMean 1.8
amzn: recMean 1.7
googl: recMean 1.7
nvda: recMean 2.2
```
