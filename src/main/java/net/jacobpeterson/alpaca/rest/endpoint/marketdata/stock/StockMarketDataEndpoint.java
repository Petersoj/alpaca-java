package net.jacobpeterson.alpaca.rest.endpoint.marketdata.stock;

import com.google.gson.reflect.TypeToken;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarAdjustment;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.quote.LatestStockQuoteResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.quote.StockQuote;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.quote.StockQuotesResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.snapshot.Snapshot;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.trade.LatestStockTradeResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.trade.StockTrade;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.trade.StockTradesResponse;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.AlpacaEndpoint;
import net.jacobpeterson.alpaca.util.format.FormatUtil;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AlpacaEndpoint} for
 * <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/historical/">Historical
 * Market Data API v2</a>.
 */
public class StockMarketDataEndpoint extends AlpacaEndpoint {

    /**
     * Instantiates a new {@link StockMarketDataEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public StockMarketDataEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "stocks");
    }

    /**
     * Gets {@link StockTrade} historical data for the requested security.
     *
     * @param symbol    the symbol to query for
     * @param start     filter data equal to or after this {@link ZonedDateTime}. Fractions of a second are not
     *                  accepted.
     * @param end       filter data equal to or before this {@link ZonedDateTime}. Fractions of a second are not
     *                  accepted.
     * @param limit     number of data points to return. Must be in range 1-10000, defaults to 1000 if <code>null</code>
     *                  is given
     * @param pageToken pagination token to continue from
     *
     * @return the {@link StockTradesResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public StockTradesResponse getTrades(String symbol, ZonedDateTime start, ZonedDateTime end, Integer limit,
            String pageToken) throws AlpacaClientException {
        checkNotNull(symbol);
        checkNotNull(start);
        checkNotNull(end);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("trades");

        urlBuilder.addQueryParameter("start", FormatUtil.toRFC3339Format(start));
        urlBuilder.addQueryParameter("end", FormatUtil.toRFC3339Format(end));

        if (limit != null) {
            urlBuilder.addQueryParameter("limit", limit.toString());
        }

        if (pageToken != null) {
            urlBuilder.addQueryParameter("page_token", pageToken);
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, StockTradesResponse.class);
    }

    /**
     * Gets the latest {@link StockTrade} for the requested security.
     *
     * @param symbol the symbol to query for
     *
     * @return the {@link LatestStockTradeResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestStockTradeResponse getLatestTrade(String symbol) throws AlpacaClientException {
        checkNotNull(symbol);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("trades")
                .addPathSegment("latest");

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, LatestStockTradeResponse.class);
    }

    /**
     * Gets {@link StockQuote} (NBBO or National Best Bid and Offer) historical data for the requested security.
     *
     * @param symbol    the symbol to query for
     * @param start     filter data equal to or after this {@link ZonedDateTime}. Fractions of a second are not
     *                  accepted.
     * @param end       filter data equal to or before this {@link ZonedDateTime}. Fractions of a second are not
     *                  accepted.
     * @param limit     number of data points to return. Must be in range 1-10000, defaults to 1000 if <code>null</code>
     *                  is given
     * @param pageToken pagination token to continue from
     *
     * @return the {@link StockQuotesResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public StockQuotesResponse getQuotes(String symbol, ZonedDateTime start, ZonedDateTime end, Integer limit,
            String pageToken) throws AlpacaClientException {
        checkNotNull(symbol);
        checkNotNull(start);
        checkNotNull(end);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("quotes");

        urlBuilder.addQueryParameter("start", FormatUtil.toRFC3339Format(start));
        urlBuilder.addQueryParameter("end", FormatUtil.toRFC3339Format(end));

        if (limit != null) {
            urlBuilder.addQueryParameter("limit", limit.toString());
        }

        if (pageToken != null) {
            urlBuilder.addQueryParameter("page_token", pageToken);
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, StockQuotesResponse.class);
    }

    /**
     * Gets the latest {@link StockQuote} for the requested security.
     *
     * @param symbol the symbol to query for
     *
     * @return the {@link LatestStockQuoteResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestStockQuoteResponse getLatestQuote(String symbol) throws AlpacaClientException {
        checkNotNull(symbol);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("quotes")
                .addPathSegment("latest");

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, LatestStockQuoteResponse.class);
    }

    /**
     * Gets {@link StockBar} aggregate historical data for the requested security.
     *
     * @param symbol                the symbol to query for
     * @param start                 filter data equal to or after this {@link ZonedDateTime}. Fractions of a second are
     *                              not accepted.
     * @param end                   filter data equal to or before this {@link ZonedDateTime}. Fractions of a second are
     *                              not accepted.
     * @param limit                 number of data points to return. Must be in range 1-10000, defaults to 1000 if
     *                              <code>null</code> is given
     * @param pageToken             pagination token to continue from
     * @param barTimePeriodDuration the duration for the given <code>barTimePeriod</code> parameter. e.g. for
     *                              <code>15Min</code> bars, you would supply <code>15</code> for this parameter and
     *                              {@link BarTimePeriod#MINUTE} for the <code>barTimePeriod</code> parameter.
     * @param barTimePeriod         the {@link BarTimePeriod} e.g. for <code>15Min</code> bars, you would supply {@link
     *                              BarTimePeriod#MINUTE} for this parameter and <code>15</code> for the
     *                              <code>barTimePeriodDuration</code> parameter.
     * @param barAdjustment         specifies the corporate action adjustment for the stocks. Default value is {@link
     *                              BarAdjustment#RAW}.
     *
     * @return the {@link StockBarsResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public StockBarsResponse getBars(String symbol, ZonedDateTime start, ZonedDateTime end, Integer limit,
            String pageToken, int barTimePeriodDuration, BarTimePeriod barTimePeriod, BarAdjustment barAdjustment)
            throws AlpacaClientException {
        checkNotNull(symbol);
        checkNotNull(start);
        checkNotNull(end);
        checkNotNull(barTimePeriod);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("bars");

        urlBuilder.addQueryParameter("start", FormatUtil.toRFC3339Format(start));
        urlBuilder.addQueryParameter("end", FormatUtil.toRFC3339Format(end));

        if (limit != null) {
            urlBuilder.addQueryParameter("limit", limit.toString());
        }

        if (pageToken != null) {
            urlBuilder.addQueryParameter("page_token", pageToken);
        }

        urlBuilder.addQueryParameter("timeframe", barTimePeriodDuration + barTimePeriod.toString());

        if (barAdjustment != null) {
            urlBuilder.addQueryParameter("adjustment", barAdjustment.toString());
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, StockBarsResponse.class);
    }

    /**
     * Gets {@link Snapshot}s of the requested securities.
     *
     * @param symbols a {@link Collection} of symbols to query for
     *
     * @return a {@link Map} with they keys being the symbol and their values being the {@link Snapshot}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Map<String, Snapshot> getSnapshots(Collection<String> symbols) throws AlpacaClientException {
        checkNotNull(symbols);
        checkArgument(!symbols.isEmpty(), "'symbols' cannot be empty!");

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment("snapshots");

        urlBuilder.addQueryParameter("symbols", String.join(",", symbols));

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, new TypeToken<HashMap<String, Snapshot>>() {}.getType());
    }

    /**
     * Gets a {@link Snapshot} of the requested security.
     *
     * @param symbol the symbol to query for
     *
     * @return the {@link Snapshot}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Snapshot getSnapshot(String symbol) throws AlpacaClientException {
        checkNotNull(symbol);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("snapshot");

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, Snapshot.class);
    }
}
