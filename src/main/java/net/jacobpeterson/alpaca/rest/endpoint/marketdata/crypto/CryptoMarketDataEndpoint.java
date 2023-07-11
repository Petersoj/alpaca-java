package net.jacobpeterson.alpaca.rest.endpoint.marketdata.crypto;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.bar.CryptoBar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.bar.CryptoBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.bar.LatestCryptoBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.orderbook.CryptoOrderbook;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.orderbook.LatestCryptoOrderbooksResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.quote.CryptoQuote;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.quote.CryptoQuotesResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.quote.LatestCryptoQuotesResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.snapshot.CryptoSnapshot;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.snapshot.CryptoSnapshotsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.trade.CryptoTrade;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.trade.CryptoTradesResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.trade.LatestCryptoTradesResponse;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.AlpacaEndpoint;
import net.jacobpeterson.alpaca.util.format.FormatUtil;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.time.ZonedDateTime;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AlpacaEndpoint} for
 * <a href="https://docs.alpaca.markets/docs/crypto-pricing-data">Historical
 * Crypto Market Data API</a>.
 */
public class CryptoMarketDataEndpoint extends AlpacaEndpoint {
    public static final String LOCALE = "us";

    /**
     * Instantiates a new {@link CryptoMarketDataEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public CryptoMarketDataEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "crypto");
    }

    /**
     * Gets {@link CryptoTrade} historical data for the requested crypto symbols.
     *
     * @param symbols   a {@link Collection} of symbols to query for
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
    public CryptoTradesResponse getTrades(Collection<String> symbols, ZonedDateTime start,
            ZonedDateTime end, Integer limit, String pageToken) throws AlpacaClientException {
        checkNotNull(symbols);
        if (symbols.isEmpty()) {
            throw new AlpacaClientException("Collection symbols must not be empty.");
        }

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(LOCALE)
                .addPathSegment("trades");

        urlBuilder.addQueryParameter("symbols", String.join(",", symbols));

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
     * Gets the latest {@link CryptoTrade}s for the requested securities.
     *
     * @param symbols   a {@link Collection} of symbols to query for
     *
     * @return the {@link LatestCryptoTradesResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestCryptoTradesResponse getLatestTrades(Collection<String> symbols) throws AlpacaClientException {
        checkNotNull(symbols);
        if (symbols.isEmpty()) {
            throw new AlpacaClientException("Collection symbols must not be empty.");
        }

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(LOCALE)
                .addPathSegment("latest")
                .addPathSegment("trades");

        urlBuilder.addQueryParameter("symbols", String.join(",", symbols));

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, LatestCryptoTradesResponse.class);
    }

    /**
     * Gets the latest {@link CryptoBar}s for the requested securities.
     *
     * @param symbols   a {@link Collection} of symbols to query for
     *
     * @return the {@link LatestCryptoBarsResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestCryptoBarsResponse getLatestBars(Collection<String> symbols) throws AlpacaClientException {
        checkNotNull(symbols);
        if (symbols.isEmpty()) {
            throw new AlpacaClientException("Collection symbols must not be empty.");
        }

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(LOCALE)
                .addPathSegment("latest")
                .addPathSegment("bars");

        urlBuilder.addQueryParameter("symbols", String.join(",", symbols));

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, LatestCryptoBarsResponse.class);
    }

    /**
     * Gets {@link CryptoQuote} historical data for the requested crypto symbols.
     *
     * @param symbols   a {@link Collection} of symbols to query for
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
    public CryptoQuotesResponse getQuotes(Collection<String> symbols, ZonedDateTime start,
            ZonedDateTime end, Integer limit, String pageToken) throws AlpacaClientException {
        checkNotNull(symbols);
        if (symbols.isEmpty()) {
            throw new AlpacaClientException("Collection symbols must not be empty.");
        }

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(LOCALE)
                .addPathSegment("quotes");

        urlBuilder.addQueryParameter("symbols", String.join(",", symbols));

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
     * Gets the latest {@link CryptoQuote}s for the requested securities.
     *
     * @param symbols   a {@link Collection} of symbols to query for
     *
     * @return the {@link LatestCryptoQuotesResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestCryptoQuotesResponse getLatestQuotes(Collection<String> symbols) throws AlpacaClientException {
        checkNotNull(symbols);
        if (symbols.isEmpty()) {
            throw new AlpacaClientException("Collection symbols must not be empty.");
        }

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(LOCALE)
                .addPathSegment("latest")
                .addPathSegment("quotes");

        urlBuilder.addQueryParameter("symbols", String.join(",", symbols));

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, LatestCryptoQuotesResponse.class);
    }

    /**
     * Gets the latest {@link CryptoSnapshot} for the requested securities.
     *
     * @param symbols   a {@link Collection} of symbols to query for
     *
     * @return the {@link CryptoSnapshotsResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public CryptoSnapshotsResponse getSnapshots(Collection<String> symbols) throws AlpacaClientException {
        checkNotNull(symbols);
        if (symbols.isEmpty()) {
            throw new AlpacaClientException("Collection symbols must not be empty.");
        }

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(LOCALE)
                .addPathSegment("snapshots");

        urlBuilder.addQueryParameter("symbols", String.join(",", symbols));

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, CryptoSnapshotsResponse.class);
    }

    /**
     * Gets the latest {@link CryptoOrderbook}s for the requested securities.
     *
     * @param symbols   a {@link Collection} of symbols to query for
     *
     * @return the {@link LatestCryptoOrderbooksResponse}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public LatestCryptoOrderbooksResponse getLatestOrderbooks(Collection<String> symbols) throws AlpacaClientException {
        checkNotNull(symbols);
        if (symbols.isEmpty()) {
            throw new AlpacaClientException("Collection symbols must not be empty.");
        }

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(LOCALE)
                .addPathSegment("latest")
                .addPathSegment("orderbooks");

        urlBuilder.addQueryParameter("symbols", String.join(",", symbols));

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, LatestCryptoOrderbooksResponse.class);
    }

    /**
     * Gets {@link CryptoBar} aggregate historical data for the requested crypto.
     *
     * @param symbols               a {@link Collection} of symbols to query for
     * @param start                 filter data equal to or after this {@link ZonedDateTime}. Fractions of a second are
     *                              not accepted. <code>null</code> for now.
     * @param end                   filter data equal to or before this {@link ZonedDateTime}. Fractions of a second are
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
    public CryptoBarsResponse getBars(Collection<String> symbols, ZonedDateTime start, ZonedDateTime end, Integer limit,
            String pageToken, int barTimePeriodDuration, BarTimePeriod barTimePeriod) throws AlpacaClientException {
        checkNotNull(symbols);
        if (symbols.isEmpty()) {
            throw new AlpacaClientException("Collection symbols must not be empty.");
        }
        checkNotNull(barTimePeriod);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(LOCALE)
                .addPathSegment("bars");

        urlBuilder.addQueryParameter("symbols", String.join(",", symbols));

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

        urlBuilder.addQueryParameter("timeframe", barTimePeriodDuration + barTimePeriod.toString());

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, CryptoBarsResponse.class);
    }
}
