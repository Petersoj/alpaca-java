package net.jacobpeterson.alpaca.refactor.rest.endpoint;

import com.google.gson.reflect.TypeToken;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.bar.Bar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.bar.BarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.bar.enums.BarsTimeFrame;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.quote.LatestQuoteResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.quote.Quote;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.quote.QuotesResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.snapshot.Snapshot;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.trade.LatestTradeResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.trade.Trade;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.historical.trade.TradesResponse;
import net.jacobpeterson.alpaca.refactor.rest.AlpacaClient;
import net.jacobpeterson.alpaca.refactor.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.refactor.util.format.FormatUtil;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AbstractEndpoint} for
 * <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/historical/">Historical
 * Market Data API v2</a>.
 */
public class MarketDataEndpoint extends AbstractEndpoint {

    /**
     * Instantiates a new {@link MarketDataEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public MarketDataEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "stocks");
    }

    /**
     * Gets {@link Trade} historical data for the requested security.
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
     * @return the {@link TradesResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public TradesResponse getTrades(String symbol, ZonedDateTime start, ZonedDateTime end, Integer limit,
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
        return alpacaClient.requestObject(request, TradesResponse.class);
    }

    /**
     * The Latest Trade API provides the latest {@link Trade} data for a given ticker symbol.
     *
     * @param symbol the symbol to query for
     *
     * @return the {@link LatestTradeResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestTradeResponse getLatestTrade(String symbol) throws AlpacaClientException {
        checkNotNull(symbol);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("trades")
                .addPathSegment("latest");

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, LatestTradeResponse.class);
    }

    /**
     * Gets {@link Quote} (NBBO or National Best Bid and Offer) historical data for the requested security.
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
     * @return the {@link QuotesResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public QuotesResponse getQuotes(String symbol, ZonedDateTime start, ZonedDateTime end, Integer limit,
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
        return alpacaClient.requestObject(request, QuotesResponse.class);
    }

    /**
     * The Latest Quote API provides the latest quote data for a given ticker symbol.
     *
     * @param symbol the symbol to query for
     *
     * @return the {@link LatestQuoteResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestQuoteResponse getLatestQuote(String symbol) throws AlpacaClientException {
        checkNotNull(symbol);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("quotes")
                .addPathSegment("latest");

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, LatestQuoteResponse.class);
    }

    /**
     * Gets {@link Bar} aggregate historical data for the requested security.
     *
     * @param symbol    the symbol to query for
     * @param start     filter data equal to or after this {@link ZonedDateTime}. Fractions of a second are not
     *                  accepted.
     * @param end       filter data equal to or before this {@link ZonedDateTime}. Fractions of a second are not
     *                  accepted.
     * @param limit     number of data points to return. Must be in range 1-10000, defaults to 1000 if <code>null</code>
     *                  is given
     * @param pageToken pagination token to continue from
     * @param timeFrame the {@link BarsTimeFrame} for the aggregation
     *
     * @return the {@link BarsResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public BarsResponse getBars(String symbol, ZonedDateTime start, ZonedDateTime end, Integer limit,
            String pageToken, BarsTimeFrame timeFrame) throws AlpacaClientException {
        checkNotNull(symbol);
        checkNotNull(start);
        checkNotNull(end);
        checkNotNull(timeFrame);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("bars");

        urlBuilder.addQueryParameter("start", FormatUtil.toRFC3339Format(start));
        urlBuilder.addQueryParameter("end", FormatUtil.toRFC3339Format(end));
        urlBuilder.addQueryParameter("timeframe", timeFrame.toString());

        if (limit != null) {
            urlBuilder.addQueryParameter("limit", limit.toString());
        }

        if (pageToken != null) {
            urlBuilder.addQueryParameter("page_token", pageToken);
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, BarsResponse.class);
    }

    /**
     * The Snapshot API for one ticker provides the latest trade, latest quote, minute bar daily bar, and previous daily
     * bar data for a given ticker symbol
     *
     * @param symbols a {@link List} of symbols to query for
     *
     * @return a {@link Map} with they keys being the symbol and their values being the {@link Snapshot}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Map<String, Snapshot> getSnapshots(List<String> symbols) throws AlpacaClientException {
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
     * The Snapshot API for one ticker provides the latest trade, latest quote, minute bar daily bar and previous daily
     * bar data for a given ticker symbol
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
                .addPathSegment("shapshot");

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, Snapshot.class);
    }
}
