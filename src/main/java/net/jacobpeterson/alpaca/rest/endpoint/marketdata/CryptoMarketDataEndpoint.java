package net.jacobpeterson.alpaca.rest.endpoint.marketdata;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.bar.Bar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.bar.BarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.enums.Exchange;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.quote.LatestQuoteResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.quote.Quote;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.quote.QuotesResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.trade.LatestTradeResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.trade.Trade;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.trade.TradesResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.xbbo.XbboResponse;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.AlpacaEndpoint;
import net.jacobpeterson.alpaca.util.format.FormatUtil;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AlpacaEndpoint} for
 * <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-crypto-data/historical/">Historical
 * Crypto Market Data API</a>.
 */
public class CryptoMarketDataEndpoint extends AlpacaEndpoint {

    /**
     * Instantiates a new {@link CryptoMarketDataEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public CryptoMarketDataEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "crypto");
    }

    /**
     * Gets {@link Trade} historical data for the requested crypto symbol.
     *
     * @param symbol    the symbol to query for
     * @param exchanges a {@link Collection} of {@link Exchange}s to filter by. <code>null</code> for all exchanges.
     * @param start     filter data equal to or after this {@link ZonedDateTime}. Fractions of a second are not
     *                  accepted. <code>null</code> for the current day in Central Time.
     * @param end       filter data equal to or before this {@link ZonedDateTime}. Fractions of a second are not
     *                  accepted. <code>null</code> for now.
     * @param limit     number of data points to return. Must be in range 1-10000, defaults to 1000 if <code>null</code>
     *                  is given
     * @param pageToken pagination token to continue from
     *
     * @return the {@link TradesResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public TradesResponse getTrades(String symbol, Collection<Exchange> exchanges, ZonedDateTime start,
            ZonedDateTime end, Integer limit, String pageToken) throws AlpacaClientException {
        checkNotNull(symbol);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("trades");

        if (exchanges != null) {
            urlBuilder.addQueryParameter("exchanges", exchanges.stream()
                    .map(Exchange::value)
                    .collect(Collectors.joining(",")));
        }

        if (start != null) {
            urlBuilder.addQueryParameter("start", FormatUtil.toRFC3339Format(start));
        }

        if (end != null) {
            urlBuilder.addQueryParameter("end", FormatUtil.toRFC3339Format(end));
        }

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
     * Gets the latest {@link Trade} for the requested security.
     *
     * @param symbol   the symbol to query for
     * @param exchange the {@link Exchange} to filter the latest {@link Trade} by.
     *
     * @return the {@link LatestTradeResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestTradeResponse getLatestTrade(String symbol, Exchange exchange) throws AlpacaClientException {
        checkNotNull(symbol);
        checkNotNull(exchange);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("trades")
                .addPathSegment("latest");

        urlBuilder.addQueryParameter("exchange", exchange.toString());

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, LatestTradeResponse.class);
    }

    /**
     * Gets {@link Quote} historical data for the requested crypto symbol.
     *
     * @param symbol    the symbol to query for
     * @param exchanges a {@link Collection} of {@link Exchange}s to filter by. <code>null</code> for all exchanges.
     * @param start     filter data equal to or after this {@link ZonedDateTime}. Fractions of a second are not
     *                  accepted. <code>null</code> for the current day in Central Time.
     * @param end       filter data equal to or before this {@link ZonedDateTime}. Fractions of a second are not
     *                  accepted. <code>null</code> for now.
     * @param limit     number of data points to return. Must be in range 1-10000, defaults to 1000 if <code>null</code>
     *                  is given
     * @param pageToken pagination token to continue from
     *
     * @return the {@link QuotesResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public QuotesResponse getQuotes(String symbol, Collection<Exchange> exchanges, ZonedDateTime start,
            ZonedDateTime end, Integer limit, String pageToken) throws AlpacaClientException {
        checkNotNull(symbol);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("quotes");

        if (exchanges != null) {
            urlBuilder.addQueryParameter("exchanges", exchanges.stream()
                    .map(Exchange::value)
                    .collect(Collectors.joining(",")));
        }

        if (start != null) {
            urlBuilder.addQueryParameter("start", FormatUtil.toRFC3339Format(start));
        }

        if (end != null) {
            urlBuilder.addQueryParameter("end", FormatUtil.toRFC3339Format(end));
        }

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
     * Gets the latest {@link Quote} for the requested security.
     *
     * @param symbol   the symbol to query for
     * @param exchange the {@link Exchange} to filter the latest {@link Quote} by.
     *
     * @return the {@link LatestQuoteResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestQuoteResponse getLatestQuote(String symbol, Exchange exchange) throws AlpacaClientException {
        checkNotNull(symbol);
        checkNotNull(exchange);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("quotes")
                .addPathSegment("latest");

        urlBuilder.addQueryParameter("exchange", exchange.toString());

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, LatestQuoteResponse.class);
    }

    /**
     * Gets {@link Bar} aggregate historical data for the requested crypto.
     *
     * @param symbol                the symbol to query for
     * @param exchanges             a {@link Collection} of {@link Exchange}s to filter by. <code>null</code> for all
     *                              exchanges.
     * @param start                 filter data equal to or after this {@link ZonedDateTime}. Fractions of a second are
     *                              not accepted. <code>null</code> for now.
     * @param limit                 number of data points to return. Must be in range 1-10000, defaults to 1000 if
     *                              <code>null</code> is given
     * @param pageToken             pagination token to continue from
     * @param barTimePeriodDuration the duration for the given <code>barTimePeriod</code> parameter. e.g. for
     *                              <code>15Min</code> bars, you would supply <code>15</code> for this parameter and
     *                              {@link BarTimePeriod#MINUTE} for the <code>barTimePeriod</code> parameter.
     * @param barTimePeriod         the {@link BarTimePeriod} e.g. for <code>15Min</code> bars, you would supply {@link
     *                              BarTimePeriod#MINUTE} for this parameter and <code>15</code> for the
     *                              <code>barTimePeriodDuration</code> parameter.
     *
     * @return the {@link BarsResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public BarsResponse getBars(String symbol, Collection<Exchange> exchanges, ZonedDateTime start, Integer limit,
            String pageToken, int barTimePeriodDuration, BarTimePeriod barTimePeriod) throws AlpacaClientException {
        checkNotNull(symbol);
        checkNotNull(barTimePeriod);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("bars");

        if (exchanges != null) {
            urlBuilder.addQueryParameter("exchanges", exchanges.stream()
                    .map(Exchange::value)
                    .collect(Collectors.joining(",")));
        }

        if (start != null) {
            urlBuilder.addQueryParameter("start", FormatUtil.toRFC3339Format(start));
        }

        if (limit != null) {
            urlBuilder.addQueryParameter("limit", limit.toString());
        }

        if (pageToken != null) {
            urlBuilder.addQueryParameter("page_token", pageToken);
        }

        urlBuilder.addQueryParameter("timeframe", barTimePeriodDuration + barTimePeriod.toString());

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, BarsResponse.class);
    }

    /**
     * Returns the XBBO for a crypto symbol that calculates the Best Bid and Offer across multiple exchanges.
     *
     * @param symbol    the symbol to query for
     * @param exchanges a {@link Collection} of {@link Exchange}s to filter by. If <code>null</code>, then only the
     *                  exchanges that can be traded on Alpaca are included in the calculation.
     *
     * @return the {@link LatestQuoteResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public XbboResponse getXBBO(String symbol, Collection<Exchange> exchanges) throws AlpacaClientException {
        checkNotNull(symbol);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbol)
                .addPathSegment("xbbo")
                .addPathSegment("latest");

        if (exchanges != null) {
            urlBuilder.addQueryParameter("exchanges", exchanges.stream()
                    .map(Exchange::value)
                    .collect(Collectors.joining(",")));
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, XbboResponse.class);
    }
}
