package io.github.mainstringargs.polygon;

/**
 * The Class Example.
 */
public class PolygonExample {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        /*
        PolygonAPI polygonAPI = new PolygonAPI();

        String ticker = "MSFT";

        try {
            SymbolEndpoints symbolEndPoints = polygonAPI.getSymbolEndpoints(ticker);

            System.out.println("\n\n" + ticker + " Symbol Endpoints:");
            System.out.println("\t" + symbolEndPoints.getSymbol().getSymbol() + " "
                    + symbolEndPoints.getSymbol().getName() + " " + symbolEndPoints.getSymbol().getType()
                    + symbolEndPoints.getSymbol().getUrl() + " " + symbolEndPoints.getSymbol().getUpdated());
            System.out.println("\t" + symbolEndPoints.getEndpoints());
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            TickerDetails symbolDetails = polygonAPI.getTickerDetails(ticker);

            System.out.println("\n\n" + ticker + " Symbol Details:");
            System.out.println("\t" + symbolDetails);
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            SymbolAnalystRatings symbolAnalystRatings = polygonAPI.getSymbolAnalystRatings(ticker);

            System.out.println("\n\n" + ticker + " Symbol Analyst Ratings:");
            System.out.println("\t" + symbolAnalystRatings);
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<StockDividend> symbolDividends = polygonAPI.getStockDividends(ticker);

            System.out.println("\n\n" + ticker + " Stock Dividends:");

            for (StockDividend div : symbolDividends) {
                System.out.println("\t" + div);
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<SymbolEarning> symbolEarnings = polygonAPI.getSymbolEarnings(ticker);

            System.out.println("\n\n" + ticker + " Symbol Earnings:");

            for (SymbolEarning earning : symbolEarnings) {
                System.out.println("\t" + earning);
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<StockFinancial> symbolFinancials = polygonAPI.getStockFinancial(ticker);

            System.out.println("\n\n" + ticker + " Symbol Financials:");
            for (StockFinancial financial : symbolFinancials) {
                System.out.println("\t" + financial);
            }

        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<TickerNews> symbolNews = polygonAPI.getTickerNews(ticker);

            System.out.println("\n\n" + ticker + " Symbol News:");
            for (TickerNews newsItem : symbolNews) {
                System.out.println("\t" + newsItem);
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<Ticker> tickers = polygonAPI.getTickers(TickerSort.TICKER_ASC, null,
                    null, Locale.US, "Tech", null, null, null).getTickers();

            System.out.println("\n\n" + "Tech" + " Search US Tickers");
            for (Ticker tickerItem : tickers) {
                System.out.println("\t" + tickerItem);
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<Market> markets = polygonAPI.getMarkets();

            System.out.println("\n\n" + " Markets:");
            for (Market market : markets) {
                System.out.println("\t" + market);
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<io.github.mainstringargs.domain.polygon.reference.Locale> locales =
                    polygonAPI.getLocales();

            System.out.println("\n\n" + " Locales:");
            for (io.github.mainstringargs.domain.polygon.reference.Locale locale : locales) {
                System.out.println("\t" + locale);
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            TypesMapping typesMapping = polygonAPI.getTypesMapping();

            System.out.println("\n\n" + "typesMapping");
            System.out.println("\t" + typesMapping.getTypes());
            System.out.println("\t" + typesMapping.getIndexTypes());

        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<StockSplit> splits = polygonAPI.getStockSplits(ticker);

            System.out.println("\n\n" + ticker + " Split:");
            for (StockSplit splitItems : splits) {
                System.out.println("\t" + splitItems);
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<Exchange> exchanges = polygonAPI.getExchanges();

            System.out.println("\n\n" + "exchanges");
            for (Exchange exchange : exchanges) {
                System.out.println("\t" + exchange);
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            Trades trades = polygonAPI.getHistoricTrades(ticker, LocalDate.of(2019, 5, 7), null, null);

            System.out.println("\n\n" + ticker + " Trades on " + LocalDate.of(2019, 5, 7) + ": ");
            // System.out.println("\t" + "map " + trades.getMap());
            System.out.println("\t" + "ticks");
            // for (Tick tick : trades.getTicks()) {
            //     System.out.println("\t" + tick);
            // }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            Quotes quotes = polygonAPI.getHistoricQuotes(ticker, LocalDate.of(2019, 5, 7), null, null);

            System.out.println("\n\n" + ticker + " Quotes on " + LocalDate.of(2019, 5, 1) + ": ");
            // System.out.println("\t" + "map " + quotes.getMap());
            System.out.println("\t" + "ticks");
            // for (io.github.mainstringargs.domain.polygon.historic.quotes.Tick tick : quotes.getTicks()) {
            //     System.out.println("\t" + tick);
            // }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            LastTrade trade = polygonAPI.getLastTrade(ticker);

            System.out.println("\n\n" + ticker + " Last Trade: ");
            System.out.println("\t" + trade);
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            LastQuote quote = polygonAPI.getLastQuote(ticker);

            System.out.println("\n\n" + ticker + " Last Quote: ");
            System.out.println("\t" + quote);
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            DailyOpenClose dailyOpenClose =
                    polygonAPI.getDailyOpenClose(ticker, LocalDate.of(2019, 5, 7));

            System.out.println("\n\n" + ticker + " DailyOpenClose on " + LocalDate.of(2019, 5, 1) + ": ");
            System.out.println("\t" + dailyOpenClose);
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            Snapshot snapshot = polygonAPI.getSnapshotSingleTicker(ticker);

            System.out.println("\n\n" + ticker + " Snapshot: ");
            System.out.println("\t" + snapshot);
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<Snapshot> snapshots = polygonAPI.getSnapshotAllTickers();

            System.out.println("\n\n" + "snapshots " + snapshots.size());
            for (Snapshot snapshot : snapshots) {
                System.out.println("\t" + snapshot);
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<Snapshot> snapshots = polygonAPI.getSnapshotsGainers();

            System.out.println("\n\n" + "gainers snapshots " + snapshots.size());
            for (Snapshot snapshot : snapshots) {
                System.out.println("\t" + snapshot);
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            List<Snapshot> snapshots = polygonAPI.getSnapshotsLosers();

            System.out.println("\n\n" + "losers snapshots " + snapshots.size());
            for (Snapshot snapshot : snapshots) {
                System.out.println("\t" + snapshot);
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            Aggregates aggs = polygonAPI.getPreviousClose(ticker, false);

            System.out.println("\n\n" + "previous close aggregates " + aggs.getResultsCount());
            // for (Result result : aggs.getResults()) {
            //     System.out.println("\t" + result);
            // }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            Aggregates aggs = polygonAPI.getAggregates(ticker, null, Timespan.HOUR,
                    LocalDate.of(2019, 4, 23), LocalDate.of(2019, 4, 26), false);

            System.out.println("\n\n" + "Aggs over time " + aggs.getResultsCount());
            // for (Result result : aggs.getResults()) {
            //     System.out.println("\t" + result);
            // }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            Aggregates aggs = polygonAPI.getGroupedDaily(Locale.US,
                    io.github.mainstringargs.polygon.enums.Market.STOCKS, LocalDate.of(2019, 4, 26), false);

            System.out.println("\n\n" + "Grouped Daily " + aggs.getResultsCount());
            // for (Result result : aggs.getResults()) {
            //     System.out.println("\t" + result);
            // }
        } catch (PolygonAPIRequestException e) {
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

        System.exit(0); */
    }
}
