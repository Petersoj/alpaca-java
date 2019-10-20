package io.github.mainstringargs.polygon;

import io.github.mainstringargs.polygon.domain.aggregate.Aggregates;
import io.github.mainstringargs.polygon.domain.aggregate.Result;
import io.github.mainstringargs.polygon.domain.historic.quotes.Quotes;
import io.github.mainstringargs.polygon.domain.historic.trades.Trades;
import io.github.mainstringargs.polygon.domain.meta.Exchange;
import io.github.mainstringargs.polygon.domain.meta.StockDividends;
import io.github.mainstringargs.polygon.domain.meta.StockFinancials;
import io.github.mainstringargs.polygon.domain.meta.SymbolAnalystRatings;
import io.github.mainstringargs.polygon.domain.meta.SymbolEarning;
import io.github.mainstringargs.polygon.domain.meta.SymbolEndpoints;
import io.github.mainstringargs.polygon.domain.meta.TickerDetails;
import io.github.mainstringargs.polygon.domain.meta.TickerNews;
import io.github.mainstringargs.polygon.domain.other.DailyOpenClose;
import io.github.mainstringargs.polygon.domain.other.StockQuote;
import io.github.mainstringargs.polygon.domain.other.StockTrade;
import io.github.mainstringargs.polygon.domain.reference.Markets;
import io.github.mainstringargs.polygon.domain.reference.StockSplits;
import io.github.mainstringargs.polygon.domain.reference.Ticker;
import io.github.mainstringargs.polygon.domain.reference.TickerTypes;
import io.github.mainstringargs.polygon.domain.snapshot.SnapshotAllTickers;
import io.github.mainstringargs.polygon.domain.snapshot.SnapshotGainersLosers;
import io.github.mainstringargs.polygon.domain.snapshot.SnapshotSingleTicker;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.enums.GainersLosers;
import io.github.mainstringargs.polygon.enums.Locale;
import io.github.mainstringargs.polygon.enums.Sort;
import io.github.mainstringargs.polygon.enums.Timespan;
import io.github.mainstringargs.polygon.nats.PolygonStreamListenerAdapter;
import io.github.mainstringargs.polygon.nats.message.ChannelMessage;
import io.github.mainstringargs.polygon.rest.exceptions.PolygonAPIException;

import java.time.LocalDate;
import java.util.List;

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
            TickerDetails tickerDetails = polygonAPI.getTickerDetails(ticker);

            System.out.println("\n\n" + ticker + " Symbol Details:");
            System.out.println("\t" + tickerDetails);
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
            StockDividends stockDividends = polygonAPI.getStockDividends(ticker);

            System.out.println("\n\n" + ticker + " Stock Dividends:");
            System.out.println("\t" + stockDividends);
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
            StockFinancials stockFinancials = polygonAPI.getStockFinancials(ticker);

            System.out.println("\n\n" + ticker + " Stock Financials:");
            System.out.println("\t" + stockFinancials);
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            TickerNews tickerNews = polygonAPI.getTickerNews(ticker);

            System.out.println("\n\n" + ticker + " Symbol News:");
            System.out.println("\t" + tickerNews);
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            List<Ticker> tickers =
                    polygonAPI.getTickers(Sort.TICKER_ASC, null, null, Locale.US, "Tech", null, null, null)
                            .getTickers();

            System.out.println("\n\n" + "Tech" + " Search US Tickers");
            for (Ticker tickerItem : tickers) {
                System.out.println("\t" + tickerItem);
            }
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            Markets markets = polygonAPI.getMarkets();

            System.out.println("\n\n" + " Markets:");
            System.out.println("\t" + markets);
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            List<io.github.mainstringargs.polygon.domain.reference.Locale> locales =
                    polygonAPI.getLocales();

            System.out.println("\n\n" + " Locales:");
            for (io.github.mainstringargs.polygon.domain.reference.Locale locale : locales) {
                System.out.println("\t" + locale);
            }
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            TickerTypes tickerTypes = polygonAPI.getTickerTypes();

            System.out.println("\n\n" + "typesMapping");
            System.out.println("\t" + tickerTypes.getResults().getTypes());
            System.out.println("\t" + tickerTypes.getResults().getIndexTypes());
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            StockSplits stockSplits = polygonAPI.getStockSplits(ticker);

            System.out.println("\n\n" + ticker + " Split:");
            System.out.println("\t" + stockSplits);
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
            Trades trades = polygonAPI.getHistoricTrades(ticker, LocalDate.of(2019, 5, 7), null, null, false, null);

            System.out.println("\n\n" + ticker + " Trades on " + LocalDate.of(2019, 5, 7) + ": ");
            System.out.println("\t" + "map " + trades.getmap());
            System.out.println("\t" + "ticks");
            for (io.github.mainstringargs.polygon.domain.historic.trades.Result result : trades.getresults()) {
                System.out.println("\t" + result);
            }
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            Quotes quotes = polygonAPI.getHistoricQuotes(ticker, LocalDate.of(2019, 5, 7), null, null, false, null);

            System.out.println("\n\n" + ticker + " Quotes on " + LocalDate.of(2019, 5, 1) + ": ");
            System.out.println("\t" + "map " + quotes.getmap());
            System.out.println("\t" + "ticks");
            for (io.github.mainstringargs.polygon.domain.historic.quotes.Result result : quotes.getresults()) {
                System.out.println("\t" + result);
            }
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            StockTrade trade = polygonAPI.getLastTrade(ticker);

            System.out.println("\n\n" + ticker + " Last Trade: ");
            System.out.println("\t" + trade);
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            StockQuote quote = polygonAPI.getLastQuote(ticker);

            System.out.println("\n\n" + ticker + " Last Quote: ");
            System.out.println("\t" + quote);
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            DailyOpenClose dailyOpenClose = polygonAPI.getDailyOpenClose(ticker,
                    LocalDate.of(2019, 5, 7));

            System.out.println("\n\n" + ticker + " DailyOpenClose on " + LocalDate.of(2019, 5, 1) + ": ");
            System.out.println("\t" + dailyOpenClose);
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            SnapshotSingleTicker snapshot = polygonAPI.getSnapshot(ticker);

            System.out.println("\n\n" + ticker + " Snapshot: ");
            System.out.println("\t" + snapshot);
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            SnapshotAllTickers snapshots = polygonAPI.getSnapshotAllTickers();

            System.out.println("\n\n" + "snapshots " + snapshots.gettickers().size());
            for (io.github.mainstringargs.polygon.domain.snapshot.Ticker snapshot : snapshots.gettickers()) {
                System.out.println("\t" + snapshot);
            }
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            SnapshotGainersLosers snapshots = polygonAPI.getSnapshotGainersLosers(GainersLosers.GAINERS);

            System.out.println("\n\n" + "Gainers snapshots " + snapshots.gettickers().size());
            for (io.github.mainstringargs.polygon.domain.snapshot.Ticker__1 snapshot : snapshots.gettickers()) {
                System.out.println("\t" + snapshot);
            }
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            SnapshotGainersLosers snapshots = polygonAPI.getSnapshotGainersLosers(GainersLosers.LOSERS);

            System.out.println("\n\n" + "Losers snapshots " + snapshots.gettickers().size());
            for (io.github.mainstringargs.polygon.domain.snapshot.Ticker__1 snapshot : snapshots.gettickers()) {
                System.out.println("\t" + snapshot);
            }
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            Aggregates aggs = polygonAPI.getPreviousClose(ticker, false);

            System.out.println("\n\n" + "previous close aggregates " + aggs.getresultsCount());
            for (Result result : aggs.getresults()) {
                System.out.println("\t" + result);
            }
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            Aggregates aggs = polygonAPI.getAggregates(ticker, null, Timespan.Hour,
                    LocalDate.of(2019, 4, 23), LocalDate.of(2019, 4, 26), false);

            System.out.println("\n\n" + "Aggs over time " + aggs.getresultsCount());
            for (Result result : aggs.getresults()) {
                System.out.println("\t" + result);
            }
        } catch (PolygonAPIException e) {
            e.printStackTrace();
        }

        try {
            Aggregates aggs = polygonAPI.getGroupedDaily(Locale.US,
                    io.github.mainstringargs.polygon.enums.Market.STOCKS, LocalDate.of(2019, 4, 26), false);

            System.out.println("\n\n" + "Grouped Daily " + aggs.getresultsCount());
            for (Result result : aggs.getresults()) {
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

        System.exit(0);
    }
}
