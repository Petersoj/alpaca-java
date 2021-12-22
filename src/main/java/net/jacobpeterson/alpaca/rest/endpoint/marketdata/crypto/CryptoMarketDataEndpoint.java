package net.jacobpeterson.alpaca.rest.endpoint.marketdata.crypto;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.common.enums.Exchange;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.bar.CryptoBar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.bar.CryptoBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.quote.CryptoQuote;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.quote.CryptoQuotesResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.quote.LatestCryptoQuoteResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.trade.CryptoTrade;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.trade.CryptoTradesResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.trade.LatestCryptoTradeResponse;
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
     * Gets {@link CryptoTrade} historical data for the requested crypto symbol.
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
     * @return the {@link CryptoTradesResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public CryptoTradesResponse getTrades(String symbol, Collection<Exchange> exchanges, ZonedDateTime start,
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
        return alpacaClient.requestObject(request, CryptoTradesResponse.class);
    }

    /**
     * Gets the latest {@link CryptoTrade} for the requested security.
     *
     * @param symbol   the symbol to query for
     * @param exchange the {@link Exchange} to filter the latest {@link CryptoTrade} by.
     *
     * @return the {@link LatestCryptoTradeResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestCryptoTradeResponse getLatestTrade(String symbol, Exchange exchange) throws AlpacaClientException {
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
        return alpacaClient.requestObject(request, LatestCryptoTradeResponse.class);
    }

    /**
     * Gets {@link CryptoQuote} historical data for the requested crypto symbol.
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
     * @return the {@link CryptoQuotesResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public CryptoQuotesResponse getQuotes(String symbol, Collection<Exchange> exchanges, ZonedDateTime start,
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
        return alpacaClient.requestObject(request, CryptoQuotesResponse.class);
    }

    /**
     * Gets the latest {@link CryptoQuote} for the requested security.
     *
     * @param symbol   the symbol to query for
     * @param exchange the {@link Exchange} to filter the latest {@link CryptoQuote} by.
     *
     * @return the {@link LatestCryptoQuoteResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestCryptoQuoteResponse getLatestQuote(String symbol, Exchange exchange) throws AlpacaClientException {
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
        return alpacaClient.requestObject(request, LatestCryptoQuoteResponse.class);
    }

    /**
     * Gets {@link CryptoBar} aggregate historical data for the requested crypto.
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
     * @return the {@link CryptoBarsResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public CryptoBarsResponse getBars(String symbol, Collection<Exchange> exchanges, ZonedDateTime start, Integer limit,
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
        return alpacaClient.requestObject(request, CryptoBarsResponse.class);
    }

    /**
     * Returns the XBBO for a crypto symbol that calculates the Best Bid and Offer across multiple exchanges.
     *
     * @param symbol    the symbol to query for
     * @param exchanges a {@link Collection} of {@link Exchange}s to filter by. If <code>null</code>, then only the
     *                  exchanges that can be traded on Alpaca are included in the calculation.
     *
     * @return the {@link XbboResponse}
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
