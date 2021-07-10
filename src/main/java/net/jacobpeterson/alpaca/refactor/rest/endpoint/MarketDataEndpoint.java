package net.jacobpeterson.alpaca.refactor.rest.endpoint;

import net.jacobpeterson.alpaca.refactor.rest.AlpacaClient;

/**
 * {@link AbstractEndpoint} for
 * <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/">Market
 * Data API v2</a>.
 */
public class MarketDataEndpoint extends AbstractEndpoint {

    /**
     * Instantiates a new {@link MarketDataEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public MarketDataEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, null);
    }

    // TODO
    // /**
    //  * Gets {@link Trade} historical data for the requested security.
    //  *
    //  * @param symbol    the symbol to query for
    //  * @param start     filter data equal to or after this {@link ZonedDateTime}. Fractions of a second are not
    //  *                  accepted
    //  * @param end       filter data equal to or before this {@link ZonedDateTime}. Fractions of a second are not
    //  *                  accepted
    //  * @param limit     number of data points to return. Must be in range 1-10000, defaults to 1000 if null is given
    //  * @param pageToken pagination token to continue from
    //  *
    //  * @return the {@link TradesResponse}
    //  *
    //  * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
    //  * @see <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/historical/">
    //  * Historical Market Data</a>
    //  */
    // public TradesResponse getTrades(String symbol, ZonedDateTime start, ZonedDateTime end, Integer limit,
    //         String pageToken) throws AlpacaAPIRequestException {
    //     checkNotNull(symbol);
    //     checkNotNull(start);
    //     checkNotNull(end);
    //
    //     AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(URLs.DATA, Endpoints.VERSION_2,
    //             Endpoints.STOCKS,
    //             symbol,
    //             Endpoints.TRADES);
    //
    //     urlBuilder.appendURLParameter(Parameters.START, FormatUtil.toRFC3339Format(start));
    //     urlBuilder.appendURLParameter(Parameters.END, FormatUtil.toRFC3339Format(end));
    //
    //     if (limit != null) {
    //         urlBuilder.appendURLParameter(Parameters.LIMIT, limit.toString());
    //     }
    //
    //     if (pageToken != null) {
    //         urlBuilder.appendURLParameter(Parameters.PAGE_TOKEN, pageToken);
    //     }
    //
    //     return alpacaRequest.get(urlBuilder, TradesResponse.class);
    // }
    //
    // /**
    //  * The Latest trade API provides the latest {@link Trade} data for a given ticker symbol.
    //  *
    //  * @param symbol the symbol to query for
    //  *
    //  * @return the {@link LatestTradeResponse}
    //  *
    //  * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
    //  * @see <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/historical/">
    //  * Historical Market Data</a>
    //  */
    // public LatestTradeResponse getLatestTrade(String symbol) throws AlpacaAPIRequestException {
    //     checkNotNull(symbol);
    //
    //     AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(URLs.DATA, Endpoints.VERSION_2,
    //             Endpoints.STOCKS,
    //             symbol,
    //             Endpoints.TRADES,
    //             Endpoints.LATEST);
    //
    //     return alpacaRequest.get(urlBuilder, LatestTradeResponse.class);
    // }
    //
    // /**
    //  * Gets {@link Quote} (NBBO or National Best Bid and Offer) historical data for the requested security.
    //  *
    //  * @param symbol    the symbol to query for
    //  * @param start     filter data equal to or after this {@link ZonedDateTime}. Fractions of a second are not
    //  *                  accepted
    //  * @param end       filter data equal to or before this {@link ZonedDateTime}. Fractions of a second are not
    //  *                  accepted
    //  * @param limit     number of data points to return. Must be in range 1-10000, defaults to 1000 if null is given
    //  * @param pageToken pagination token to continue from
    //  *
    //  * @return the {@link QuotesResponse}
    //  *
    //  * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
    //  * @see <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/historical/">
    //  * Historical Market Data</a>
    //  */
    // public QuotesResponse getQuotes(String symbol, ZonedDateTime start, ZonedDateTime end, Integer limit,
    //         String pageToken) throws AlpacaAPIRequestException {
    //     checkNotNull(symbol);
    //     checkNotNull(start);
    //     checkNotNull(end);
    //
    //     AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(URLs.DATA, Endpoints.VERSION_2,
    //             Endpoints.STOCKS,
    //             symbol,
    //             Endpoints.QUOTES);
    //
    //     urlBuilder.appendURLParameter(Parameters.START, FormatUtil.toRFC3339Format(start));
    //     urlBuilder.appendURLParameter(Parameters.END, FormatUtil.toRFC3339Format(end));
    //
    //     if (limit != null) {
    //         urlBuilder.appendURLParameter(Parameters.LIMIT, limit.toString());
    //     }
    //
    //     if (pageToken != null) {
    //         urlBuilder.appendURLParameter(Parameters.PAGE_TOKEN, pageToken);
    //     }
    //
    //     return alpacaRequest.get(urlBuilder, QuotesResponse.class);
    // }
    //
    // /**
    //  * The Latest quote API provides the latest quote data for a given ticker symbol.
    //  *
    //  * @param symbol the symbol to query for
    //  *
    //  * @return the {@link LatestQuoteResponse}
    //  *
    //  * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
    //  * @see <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/historical/">
    //  * Historical Market Data</a>
    //  */
    // public LatestQuoteResponse getLatestQuote(String symbol) throws AlpacaAPIRequestException {
    //     checkNotNull(symbol);
    //
    //     AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(URLs.DATA, Endpoints.VERSION_2,
    //             Endpoints.STOCKS,
    //             symbol,
    //             Endpoints.QUOTES,
    //             Endpoints.LATEST);
    //
    //     return alpacaRequest.get(urlBuilder, LatestQuoteResponse.class);
    // }
    //
    // /**
    //  * Gets {@link Bar} aggregate historical data for the requested security.
    //  *
    //  * @param symbol    the symbol to query for
    //  * @param start     filter data equal to or after this {@link ZonedDateTime}. Fractions of a second are not
    //  *                  accepted
    //  * @param end       filter data equal to or before this {@link ZonedDateTime}. Fractions of a second are not
    //  *                  accepted
    //  * @param limit     number of data points to return. Must be in range 1-10000, defaults to 1000 if null is given
    //  * @param pageToken pagination token to continue from
    //  * @param timeFrame the {@link BarsTimeFrame} for the aggregation
    //  *
    //  * @return the {@link BarsResponse}
    //  *
    //  * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
    //  * @see <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/historical/">
    //  * Historical Market Data</a>
    //  */
    // public BarsResponse getBars(String symbol, ZonedDateTime start, ZonedDateTime end, Integer limit,
    //         String pageToken, BarsTimeFrame timeFrame) throws AlpacaAPIRequestException {
    //     checkNotNull(symbol);
    //     checkNotNull(start);
    //     checkNotNull(end);
    //     checkNotNull(timeFrame);
    //
    //     AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(URLs.DATA, Endpoints.VERSION_2,
    //             Endpoints.STOCKS,
    //             symbol,
    //             Endpoints.BARS);
    //
    //     urlBuilder.appendURLParameter(Parameters.START, FormatUtil.toRFC3339Format(start));
    //     urlBuilder.appendURLParameter(Parameters.END, FormatUtil.toRFC3339Format(end));
    //     urlBuilder.appendURLParameter(Parameters.TIMEFRAME, timeFrame.getAPIName());
    //
    //     if (limit != null) {
    //         urlBuilder.appendURLParameter(Parameters.LIMIT, limit.toString());
    //     }
    //
    //     if (pageToken != null) {
    //         urlBuilder.appendURLParameter(Parameters.PAGE_TOKEN, pageToken);
    //     }
    //
    //     return alpacaRequest.get(urlBuilder, BarsResponse.class);
    // }
    //
    // /**
    //  * The Snapshot API for one ticker provides the latest trade, latest quote, minute bar daily bar and previous
    //  daily
    //  * bar data for a given ticker symbol
    //  *
    //  * @param symbols the symbols to query for
    //  *
    //  * @return a {@link Map} with they keys being the symbol and the value being the {@link Snapshot}
    //  *
    //  * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
    //  * @see <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/historical/">
    //  * Historical Market Data</a>
    //  */
    // public Map<String, Snapshot> getSnapshots(List<String> symbols) throws AlpacaAPIRequestException {
    //     checkNotNull(symbols);
    //     Preconditions.checkState(!symbols.isEmpty());
    //
    //     AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(URLs.DATA, Endpoints.VERSION_2,
    //             Endpoints.STOCKS,
    //             Endpoints.SNAPSHOTS);
    //
    //     urlBuilder.appendURLParameter(Parameters.SYMBOLS, String.join(",", symbols));
    //
    //     HttpResponse<InputStream> response;
    //     try {
    //         response = alpacaRequest.invokeGet(urlBuilder);
    //     } catch (UnirestException exception) {
    //         throw new AlpacaAPIRequestException(exception);
    //     }
    //
    //     if (response.getStatus() != 200) {
    //         throw new AlpacaAPIRequestException(response);
    //     }
    //
    //     JsonElement responseJsonElement = alpacaRequest.getResponseJSON(response);
    //     HashMap<String, Snapshot> snapshotsOfSymbols = new HashMap<>();
    //
    //     if (responseJsonElement instanceof JsonObject) {
    //         JsonObject responseJsonObject = (JsonObject) responseJsonElement;
    //
    //         for (String symbol : responseJsonObject.keySet()) { // Loop through response object keys
    //             JsonElement jsonObjectElement = responseJsonObject.get(symbol);
    //
    //             if (jsonObjectElement instanceof JsonObject) {
    //                 snapshotsOfSymbols.put(symbol, GsonUtil.GSON.fromJson(jsonObjectElement, Snapshot.class));
    //             } else {
    //                 throw new IllegalStateException("The response object must only contain objects!");
    //             }
    //         }
    //         return snapshotsOfSymbols;
    //     } else {
    //         throw new IllegalStateException("The response must be an object!");
    //     }
    // }
    //
    // /**
    //  * The Snapshot API for one ticker provides the latest trade, latest quote, minute bar daily bar and previous
    //  daily
    //  * bar data for a given ticker symbol
    //  *
    //  * @param symbol the symbol to query for
    //  *
    //  * @return the {@link Snapshot}
    //  *
    //  * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
    //  * @see <a href="https://alpaca.markets/docs/api-documentation/api-v2/market-data/alpaca-data-api-v2/historical/">
    //  * Historical Market Data</a>
    //  */
    // public Snapshot getSnapshot(String symbol) throws AlpacaAPIRequestException {
    //     checkNotNull(symbol);
    //
    //     AlpacaRequestBuilder urlBuilder = new AlpacaRequestBuilder(URLs.DATA, Endpoints.VERSION_2,
    //             Endpoints.STOCKS,
    //             symbol,
    //             Endpoints.SNAPSHOT);
    //
    //     return alpacaRequest.get(urlBuilder, Snapshot.class);
    // }
}
