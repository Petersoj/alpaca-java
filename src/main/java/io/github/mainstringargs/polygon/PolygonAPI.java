package io.github.mainstringargs.polygon;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import io.github.mainstringargs.alpaca.Utilities;
import io.github.mainstringargs.polygon.domain.DailyOpenClose;
import io.github.mainstringargs.polygon.domain.Quote;
import io.github.mainstringargs.polygon.domain.Snapshot;
import io.github.mainstringargs.polygon.domain.Trade;
import io.github.mainstringargs.polygon.domain.aggregate.Aggregates;
import io.github.mainstringargs.polygon.domain.historic.quotes.Quotes;
import io.github.mainstringargs.polygon.domain.historic.trades.Trades;
import io.github.mainstringargs.polygon.domain.meta.Exchange;
import io.github.mainstringargs.polygon.domain.meta.SymbolAnalystRatings;
import io.github.mainstringargs.polygon.domain.meta.SymbolDetails;
import io.github.mainstringargs.polygon.domain.meta.SymbolDividend;
import io.github.mainstringargs.polygon.domain.meta.SymbolEarning;
import io.github.mainstringargs.polygon.domain.meta.SymbolEndpoints;
import io.github.mainstringargs.polygon.domain.meta.SymbolFinancial;
import io.github.mainstringargs.polygon.domain.meta.SymbolNews;
import io.github.mainstringargs.polygon.domain.meta.TickerDetails;
import io.github.mainstringargs.polygon.domain.reference.Market;
import io.github.mainstringargs.polygon.domain.reference.Markets;
import io.github.mainstringargs.polygon.domain.reference.Split;
import io.github.mainstringargs.polygon.domain.reference.Tickers;
import io.github.mainstringargs.polygon.domain.reference.TypesMapping;
import io.github.mainstringargs.polygon.enums.GainersLosers;
import io.github.mainstringargs.polygon.enums.Locale;
import io.github.mainstringargs.polygon.enums.Sort;
import io.github.mainstringargs.polygon.enums.Timespan;
import io.github.mainstringargs.polygon.nats.PolygonNatsClient;
import io.github.mainstringargs.polygon.nats.PolygonStreamListener;
import io.github.mainstringargs.polygon.properties.PolygonProperties;
import io.github.mainstringargs.polygon.rest.PolygonRequest;
import io.github.mainstringargs.polygon.rest.PolygonRequestBuilder;
import io.github.mainstringargs.polygon.rest.exceptions.PolygonAPIException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

/**
 * The Class PolygonAPI.
 */
public class PolygonAPI {

    /**
     * The logger.
     */
    private static Logger LOGGER = LogManager.getLogger(PolygonAPI.class);

    /**
     * The polygon nats client.
     */
    private final PolygonNatsClient polygonNatsClient;

    /**
     * The polygon request.
     */
    private final PolygonRequest polygonRequest;

    /**
     * The base data url.
     */
    private String baseDataUrl;

    /**
     * Instantiates a new polygon API.
     */
    public PolygonAPI() {
        this(PolygonProperties.KEY_ID_VALUE);
    }

    /**
     * Instantiates a new polygon API.
     *
     * @param keyId the key id
     */
    public PolygonAPI(String keyId) {
        this(keyId, PolygonProperties.POLYGON_NATS_SERVERS_VALUE);
    }

    /**
     * Instantiates a new polygon API.
     *
     * @param keyId              the key id
     * @param polygonNatsServers the polygon nats servers
     */
    public PolygonAPI(String keyId, String... polygonNatsServers) {
        polygonRequest = new PolygonRequest(keyId);
        polygonNatsClient = new PolygonNatsClient(keyId, polygonNatsServers);
        baseDataUrl = PolygonProperties.BASE_DATA_URL_VALUE;
    }

    /**
     * Get gets the endpoints that are supported for a symbol. Note: The endpoints object is
     * key/values of the endpoint name and url. These will almost always be the same of all symbols.
     * NOTE: This endpoint is now deprecated and has been removed from the docs.
     *
     * @param symbol we want the endpoint list for.
     * @return the symbol endpoints
     * @throws PolygonAPIException the polygon API exception
     */
    public SymbolEndpoints getSymbolEndpoints(String symbol) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.META_ENDPOINT, PolygonConstants.SYMBOLS_ENDPOINT);
        builder.appendEndpoint(symbol);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, SymbolEndpoints.class);
    }

    /**
     * Get the details of the symbol company/entity. These are important details which offer an
     * overview of the entity. Things like name, sector, description, logo and similar companies.
     *
     * @param symbol we want details for
     * @return the symbol details
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_company">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_company</a>
     */
    public SymbolDetails getTickerDetails(String symbol) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.META_ENDPOINT, PolygonConstants.SYMBOLS_ENDPOINT);
        builder.appendEndpoint(symbol);
        builder.appendEndpoint(PolygonConstants.COMPANY_ENDPOINT);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, TickerDetails.class);
    }

    /**
     * Get the analyst ratings of the symbol company/entity. Ratings are from current date, up to
     * 5 months into the future.
     * NOTE: This endpoint is now deprecated and has been removed from the docs.
     *
     * @param symbol we want analyst ratings for
     * @return the symbol analyst ratings
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_analysts">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_analysts</a>
     */
    public SymbolAnalystRatings getSymbolAnalystRatings(String symbol) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.META_ENDPOINT, PolygonConstants.SYMBOLS_ENDPOINT);
        builder.appendEndpoint(symbol);
        builder.appendEndpoint(PolygonConstants.ANALYSTS_ENDPOINT);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, SymbolAnalystRatings.class);
    }

    /**
     * Get the historical dividends for this ticker.
     *
     * @param symbol we want details for
     * @return the symbol dividends
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Reference/get_v2_reference_dividends_symbol">https://polygon.io/docs/#!/Reference/get_v2_reference_dividends_symbol</a>
     */
    public List<SymbolDividend> getStockDividends(String symbol) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.REFERENCE_ENDPOINT, PolygonConstants.DIVIDENDS_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
        builder.appendEndpoint(symbol);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        Type listType = new TypeToken<List<StockDividend>>() {
        }.getType();

        return polygonRequest.getResponseObject(response, listType);
    }

    /**
     * Get the historical earnings for a company.
     * NOTE: This endpoint is now deprecated and has been removed from the docs.
     *
     * @param symbol we want details for
     * @return the symbol earnings
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_earnings">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_earnings</a>
     */
    public List<SymbolEarning> getSymbolEarnings(String symbol) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.META_ENDPOINT, PolygonConstants.SYMBOLS_ENDPOINT);
        builder.appendEndpoint(symbol);
        builder.appendEndpoint(PolygonConstants.EARNINGS_ENDPOINT);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        Type listType = new TypeToken<List<SymbolEarning>>() {
        }.getType();

        return polygonRequest.getResponseObject(response, listType);
    }


    /**
     * Get the historical financials for this ticker.
     *
     * @param symbol we want details for
     * @return the symbol financials
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Reference/get_v2_reference_financials_symbol">https://polygon.io/docs/#!/Reference/get_v2_reference_financials_symbol</a>
     */
    public List<SymbolFinancial> getStockFinancials(String symbol) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.REFERENCE_ENDPOINT, PolygonConstants.FINANCIALS_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
        builder.appendEndpoint(symbol);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        Type listType = new TypeToken<List<StockFinancial>>() {
        }.getType();

        return polygonRequest.getResponseObject(response, listType);
    }

    /**
     * Get news articles for this ticker.
     *
     * @param symbol the symbol we want details for
     * @return the symbol news
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_news">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_news</a>
     */
    public List<SymbolNews> getTickerNews(String symbol) throws PolygonAPIException {
        return this.getTickerNews(symbol, null, null);
    }

    /**
     * Get news articles for this symbol.
     *
     * @param symbol  the symbol we want details for
     * @param perpage How many items to be on each page during pagination. Max 50
     * @param page    Which page of results to return
     * @return the symbol news
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_news">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_news</a>
     */
    public List<SymbolNews> getTickerNews(String symbol, Integer perpage, Integer page)
            throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.META_ENDPOINT, PolygonConstants.SYMBOLS_ENDPOINT);
        builder.appendEndpoint(symbol);
        builder.appendEndpoint(PolygonConstants.NEWS_ENDPOINT);
        if (perpage != null) {
            builder.appendURLParameter(PolygonConstants.PERPAGE_PARAMETER, String.valueOf(perpage));
        }

        if (page != null) {
            builder.appendURLParameter(PolygonConstants.PAGE_PARAMETER, String.valueOf(page));
        }

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        Type listType = new TypeToken<List<TickerNews>>() {
        }.getType();

        return polygonRequest.getResponseObject(response, listType);
    }

    /**
     * Query all ticker symbols which are supported by Polygon.io.
     * This API includes Indices, Crypto, FX, and Stocks/Equities.
     *
     * @param sort    Which field to sort by.
     * @param type    If you want the results to only container a certain type.
     * @param market  Get tickers for a specific market
     * @param locale  Get tickers for a specific region/locale
     * @param search  Search the name of tickers
     * @param perpage How many items to be on each page during pagination. Max 50
     * @param page    Which page of results to return
     * @param active  Filter for only active or inactive symbols
     * @return the tickers
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Reference/get_v2_reference_tickers">https://polygon.io/docs/#!/Reference/get_v2_reference_tickers</a>
     */
    public Tickers getTickers(Sort sort, io.github.mainstringargs.polygon.enums.Type type,
                              io.github.mainstringargs.polygon.enums.Market market, Locale locale, String search,
                              Integer perpage, Integer page, Boolean active) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.REFERENCE_ENDPOINT, PolygonConstants.TICKER_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

        if (sort != null) {
            builder.appendURLParameter(PolygonConstants.SORT_PARAMETER, sort.getAPIName());
        }

        if (type != null) {
            builder.appendURLParameter(PolygonConstants.TYPE_PARAMETER, type.getAPIName());
        }

        if (market != null) {
            builder.appendURLParameter(PolygonConstants.MARKET_PARAMETER, market.getAPIName());
        }

        if (locale != null) {
            builder.appendURLParameter(PolygonConstants.LOCALE_PARAMETER, locale.getAPIName());
        }

        if (search != null) {
            builder.appendURLParameter(PolygonConstants.SEARCH_PARAMETER, search);
        }

        if (perpage != null) {
            builder.appendURLParameter(PolygonConstants.PERPAGE_PARAMETER, String.valueOf(perpage));
        }

        if (page != null) {
            builder.appendURLParameter(PolygonConstants.PAGE_PARAMETER, String.valueOf(page));
        }

        if (active != null) {
            builder.appendURLParameter(PolygonConstants.ACTIVE_PARAMETER, String.valueOf(active));
        }

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, Tickers.class);
    }

    /**
     * Get the list of currently supported markets.
     *
     * @return the markets
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Reference/get_v2_reference_markets">https://polygon.io/docs/#!/Reference/get_v2_reference_markets</a>
     */
    public List<Market> getMarkets() throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.REFERENCE_ENDPOINT, PolygonConstants.MARKETS_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        Type listType = new TypeToken<List<Markets>>() {
        }.getType();

        return polygonRequest.getResponseObject(response, listType);
    }

    /**
     * Get the list of currently supported locales.
     *
     * @return the locales
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Reference/get_v2_reference_locales">https://polygon.io/docs/#!/Reference/get_v2_reference_locales</a>
     */
    public List<io.github.mainstringargs.polygon.domain.reference.Locale> getLocales()
            throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.REFERENCE_ENDPOINT, PolygonConstants.LOCALES_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        Type listType = new TypeToken<List<io.github.mainstringargs.polygon.domain.reference.Locale>>() {
        }.getType();

        return polygonRequest.getResponseObject(response, listType);
    }

    /**
     * Get the mapping of ticker types to descriptions / long names.
     *
     * @return the types mapping
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Reference/get_v2_reference_types">https://polygon.io/docs/#!/Reference/get_v2_reference_types</a>
     */
    public TypesMapping getTypesMapping() throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.REFERENCE_ENDPOINT, PolygonConstants.TYPES_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, TickerTypes.class);
    }

    /**
     * Get the historical splits for this symbol.
     *
     * @param symbol we want details for
     * @return the splits
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Reference/get_v2_reference_splits_symbol">https://polygon.io/docs/#!/Reference/get_v2_reference_splits_symbol</a>
     */
    public List<Split> getStockSplits(String symbol) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.REFERENCE_ENDPOINT, PolygonConstants.SPLITS_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
        builder.appendEndpoint(symbol);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        Type listType = new TypeToken<List<StockSplit>>() {
        }.getType();

        return polygonRequest.getResponseObject(response, listType);
    }

    /**
     * List of stock exchanges which are supported by Polygon.io.
     *
     * @return the exchange
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Stocks--Equities/get_v1_meta_exchanges">https://polygon.io/docs/#!/Stocks--Equities/get_v1_meta_exchanges</a>
     */
    public List<Exchange> getExchanges() throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.META_ENDPOINT, PolygonConstants.EXCHANGES_ENDPOINT);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        Type listType = new TypeToken<List<Exchange>>() {
        }.getType();

        return polygonRequest.getResponseObject(response, listType);
    }

    /**
     * Get historic trades for a ticker.
     *
     * @param symbol          Ticker symbol we want ticks for
     * @param date            Date/Day of the historic ticks to retrieve ( YYYY-MM-DD )
     * @param timestampOffset Timestamp offset, used for pagination. This is the offset at which to
     *                        start the results. Using the timestamp of the last result as the
     *                        offset will give you the next page of results.
     * @param timestampLimit  Maximum timestamp allowed in the results.
     * @param reverse         Reverse the order of the results. This is useful in combination with timestamp param.
     * @param limit           Limit the size of response, Max 50000
     * @return the historic trades
     * @throws PolygonAPIException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v2_ticks_stocks_trades_ticker_date">https://polygon.io/docs/#!/Stocks--Equities/get_v2_ticks_stocks_trades_ticker_date</a>
     */
    public Trades getHistoricTrades(String symbol, LocalDate date, Long timestampOffset,
                                    Long timestampLimit, Boolean reverse, Integer limit)
            throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.TICKS_ENDPOINT, PolygonConstants.STOCKS_ENDPOINT,
                PolygonConstants.TRADES_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
        builder.appendEndpoint(symbol);
        builder.appendEndpoint(Utilities.toDateString(date));

        if (timestampOffset != null) {
            builder.appendURLParameter(PolygonConstants.TIMESTAMP_OFFSET_PARAMETER,
                    String.valueOf(timestampOffset));
        }

        if (timestampLimit != null) {
            builder.appendURLParameter(PolygonConstants.TIMESTAMP_LIMIT_PARAMETER,
                    String.valueOf(timestampLimit));
        }

        if (reverse != null) {
            builder.appendURLParameter(PolygonConstants.REVERSE_ENDPOINT, String.valueOf(reverse));
        }

        if (limit != null) {
            builder.appendURLParameter(PolygonConstants.LIMIT_PARAMETER, String.valueOf(limit));
        }

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, Trades.class);
    }

    /**
     * Get historic NBBO quotes for a ticker.
     *
     * @param symbol          Ticker symbol we want ticks for
     * @param date            Date/Day of the historic ticks to retrieve ( YYYY-MM-DD )
     * @param timestampOffset Timestamp offset, used for pagination. This is the offset at which to
     *                        start the results. Using the timestamp of the last result as the
     *                        offset will give you the next page of results.
     * @param timestampLimit  Maximum timestamp allowed in the results.
     * @param reverse         Reverse the order of the results. This is useful in combination with timestamp param.
     * @param limit           Limit the size of response, Max 50000
     * @return the historic quotes
     * @throws PolygonAPIException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v2_ticks_stocks_nbbo_ticker_date">https://polygon.io/docs/#!/Stocks--Equities/get_v2_ticks_stocks_nbbo_ticker_date</a>
     */
    public Quotes getHistoricQuotes(String symbol, LocalDate date, Long timestampOffset,
                                    Long timestampLimit, Boolean reverse, Integer limit)
            throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.TICKS_ENDPOINT, PolygonConstants.STOCKS_ENDPOINT,
                PolygonConstants.NBBO_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
        builder.appendEndpoint(symbol);
        builder.appendEndpoint(Utilities.toDateString(date));

        if (timestampOffset != null) {
            builder.appendURLParameter(PolygonConstants.TIMESTAMP_OFFSET_PARAMETER,
                    String.valueOf(timestampOffset));
        }

        if (timestampLimit != null) {
            builder.appendURLParameter(PolygonConstants.TIMESTAMP_LIMIT_PARAMETER,
                    String.valueOf(timestampLimit));
        }

        if (reverse != null) {
            builder.appendURLParameter(PolygonConstants.REVERSE_ENDPOINT, String.valueOf(reverse));
        }

        if (limit != null) {
            builder.appendURLParameter(PolygonConstants.LIMIT_PARAMETER, String.valueOf(limit));
        }

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, Quotes.class);
    }

    /**
     * Get the last trade for a given stock.
     *
     * @param symbol Symbol of the stock to get
     * @return the last trade
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_stocks_symbol">https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_stocks_symbol</a>
     */
    public Trade getLastTrade(String symbol) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.LAST_ENDPOINT, PolygonConstants.STOCKS_ENDPOINT);
        builder.appendEndpoint(symbol);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, LastTrade.class);
    }

    /**
     * Get the last quote tick for a given stock.
     *
     * @param symbol Symbol of the quote to get
     * @return the last quote
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_quote_stocks_symbol">https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_quote_stocks_symbol</a>
     */
    public Quote getLastQuote(String symbol) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.LAST_QUOTE_ENDPOINT, PolygonConstants.STOCKS_ENDPOINT);
        builder.appendEndpoint(symbol);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, LastQuote.class);
    }

    /**
     * Get the open, close, and afterhours prices of a symbol on a certain date.
     *
     * @param symbol Symbol of the stock to get
     * @param date   Date of the requested open/close
     * @return the daily open close
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Stocks--Equities/get_v1_open_close_symbol_date">https://polygon.io/docs/#!/Stocks--Equities/get_v1_open_close_symbol_date</a>
     */
    public DailyOpenClose getDailyOpenClose(String symbol, LocalDate date) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.OPEN_CLOSE_ENDPOINT);
        builder.appendEndpoint(symbol);
        builder.appendEndpoint(Utilities.toDateString(date));

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, DailyOpenClose.class);
    }

    /**
     * Snapshot allows you to see all tickers current minute aggregate, daily aggregate and last
     * trade. As well as previous days aggregate and calculated change for today.
     *
     * @return the snapshot all tickers
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers">https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers</a>
     */
    public List<Snapshot> getSnapshotAllTickers() throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.SNAPSHOT_ENDPOINT, PolygonConstants.LOCALE_ENDPOINT,
                PolygonConstants.US_ENDPOINT, PolygonConstants.MARKETS_ENDPOINT,
                PolygonConstants.STOCKS_ENDPOINT, PolygonConstants.TICKERS_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, SnapshotAllTickers.class);
    }

    /**
     * See the current snapshot of a single ticker.
     *
     * @param symbol Ticker of the snapshot
     * @return the snapshot
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers_ticker">https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers_ticker</a>
     */
    public Snapshot getSnapshot(String symbol) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.SNAPSHOT_ENDPOINT, PolygonConstants.LOCALE_ENDPOINT,
                PolygonConstants.US_ENDPOINT, PolygonConstants.MARKETS_ENDPOINT,
                PolygonConstants.STOCKS_ENDPOINT, PolygonConstants.TICKERS_ENDPOINT);
        builder.appendEndpoint(symbol);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, SnapshotSingleTicker.class);
    }

    /**
     * See the current snapshot of the top 20 gainers or losers of the day at the moment.
     *
     * @param gainersLosers gainers or losers
     * @return the snapshot gainers/losers
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_direction">https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_direction</a>
     */
    public List<Snapshot> getSnapshotGainersLosers(GainersLosers gainersLosers) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.SNAPSHOT_ENDPOINT, PolygonConstants.LOCALE_ENDPOINT,
                PolygonConstants.US_ENDPOINT, PolygonConstants.MARKETS_ENDPOINT,
                PolygonConstants.STOCKS_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
        builder.appendEndpoint(gainersLosers.getAPIName());

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        return polygonRequest.getResponseObject(response, SnapshotGainersLosers.class);
    }

    /**
     * Get the previous day close for the specified ticker.
     *
     * @param ticker     Ticker symbol of the request
     * @param unadjusted Set to true if the results should NOT be adjusted for splits.
     * @return the previous close
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_prev">https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_prev</a>
     */
    public Aggregates getPreviousClose(String ticker, Boolean unadjusted) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.AGGS_ENDPOINT, PolygonConstants.TICKER_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
        builder.appendEndpoint(ticker);
        builder.appendEndpoint(PolygonConstants.PREV_ENDPOINT);

        if (unadjusted != null) {
            builder.appendURLParameter(PolygonConstants.UNADJUSTED_PARAMETER, unadjusted.toString());
        }

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        Aggregates aggregates = polygonRequest.getResponseObject(response, Aggregates.class);

        if (aggregates.getResultsCount() == 1) {
            aggregates.getResults().get(0).setTicker(ticker);
        }

        return aggregates;
    }

    /**
     * Get aggregates for a date range, in custom time window sizes.
     *
     * @param ticker     Ticker symbol of the request
     * @param multiplier Size of the timespan multiplier
     * @param timeSpan   Size of the time window
     * @param fromDate   the from date
     * @param toDate     the to date
     * @param unadjusted Set to true if the results should NOT be adjusted for splits
     * @return the aggregates
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_range_multiplier_timespan_from_to">https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_range_multiplier_timespan_from_to</a>
     */
    public Aggregates getAggregates(String ticker, Integer multiplier, Timespan timeSpan,
                                    LocalDate fromDate, LocalDate toDate, Boolean unadjusted) throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.AGGS_ENDPOINT, PolygonConstants.TICKER_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
        builder.appendEndpoint(ticker);
        builder.appendEndpoint(PolygonConstants.RANGE_ENDPOINT);
        builder.appendEndpoint(Integer.toString((multiplier != null) ? multiplier : 1));
        builder.appendEndpoint(timeSpan.getAPIName());
        builder.appendEndpoint(Utilities.toDateString(fromDate));
        builder.appendEndpoint(Utilities.toDateString(toDate));

        if (unadjusted != null) {
            builder.appendURLParameter(PolygonConstants.UNADJUSTED_PARAMETER, unadjusted.toString());
        }

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        Aggregates aggregates = polygonRequest.getResponseObject(response, Aggregates.class);

        if (aggregates.getResultsCount() != 0) {
            List<io.github.mainstringargs.polygon.domain.aggregate.Result> results =
                    aggregates.getResults();
            for (io.github.mainstringargs.polygon.domain.aggregate.Result result : results) {
                result.setTicker(ticker);
            }
        }

        return aggregates;
    }

    /**
     * Get the daily OHLC for entire markets.
     *
     * @param locale     Locale of the aggregates ( See 'Locales' API )
     * @param market     Market of the aggregates ( See 'Markets' API )
     * @param date       to date
     * @param unadjusted Set to true if the results should NOT be adjusted for splits.
     * @return the grouped daily
     * @throws PolygonAPIException the polygon API exception
     * @see <a href= "https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_grouped_locale_locale_market_market_date">https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_grouped_locale_locale_market_market_date</a>
     */
    public Aggregates getGroupedDaily(Locale locale, io.github.mainstringargs.polygon.enums.Market market,
                                      LocalDate date, Boolean unadjusted)
            throws PolygonAPIException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
                PolygonConstants.AGGS_ENDPOINT, PolygonConstants.GROUPED_ENDPOINT,
                PolygonConstants.LOCALE_ENDPOINT);
        builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
        builder.appendEndpoint(locale.getAPIName());
        builder.appendEndpoint(PolygonConstants.MARKET_ENDPOINT);
        builder.appendEndpoint(market.getAPIName());
        builder.appendEndpoint(Utilities.toDateString(date));

        if (unadjusted != null) {
            builder.appendURLParameter(PolygonConstants.UNADJUSTED_PARAMETER, unadjusted.toString());
        }

        HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIException(response);
        }

        Aggregates aggregates = polygonRequest.getResponseObject(response, Aggregates.class);

        if (aggregates.getResultsCount() != 0) {
            final String tickerKey = "T";
            final String resultsKey = "results";

            List<io.github.mainstringargs.polygon.domain.aggregate.Result> results =
                    aggregates.getResults();
            JsonNode responseJson = response.getBody();
            JSONArray resultsArr = (JSONArray) responseJson.getObject().get(resultsKey);

            for (int i = 0; i < results.size(); i++) {
                io.github.mainstringargs.polygon.domain.aggregate.Result result = results.get(i);
                JSONObject jsonObj = (JSONObject) resultsArr.get(i);
                result.setTicker(jsonObj.get(tickerKey).toString());
            }
        }

        return aggregates;
    }

    /**
     * Adds the polygon stream listener.
     *
     * @param streamListener the stream listener
     */
    public void addPolygonStreamListener(PolygonStreamListener streamListener) {
        polygonNatsClient.addListener(streamListener);
    }


    /**
     * Removes the polygon stream listener.
     *
     * @param streamListener the stream listener
     */
    public void removePolygonStreamListener(PolygonStreamListener streamListener) {
        polygonNatsClient.removeListener(streamListener);
    }
}
