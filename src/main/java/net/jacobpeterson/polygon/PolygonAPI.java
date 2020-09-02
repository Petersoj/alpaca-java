package net.jacobpeterson.polygon;

import com.google.common.base.Preconditions;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import net.jacobpeterson.domain.polygon.aggregates.AggregatesResponse;
import net.jacobpeterson.domain.polygon.conditionsmapping.ConditionsMapping;
import net.jacobpeterson.domain.polygon.dailyopenclose.DailyOpenCloseResponse;
import net.jacobpeterson.domain.polygon.exchanges.Exchange;
import net.jacobpeterson.domain.polygon.groupeddaily.GroupedDailyResponse;
import net.jacobpeterson.domain.polygon.historicquotes.HistoricQuotesResponse;
import net.jacobpeterson.domain.polygon.historictrades.HistoricTradesResponse;
import net.jacobpeterson.domain.polygon.lastquote.LastQuoteResponse;
import net.jacobpeterson.domain.polygon.lasttrade.LastTradeResponse;
import net.jacobpeterson.domain.polygon.locales.LocalesResponse;
import net.jacobpeterson.domain.polygon.marketholidays.MarketHoliday;
import net.jacobpeterson.domain.polygon.markets.MarketsResponse;
import net.jacobpeterson.domain.polygon.marketstatus.MarketStatus;
import net.jacobpeterson.domain.polygon.previousclose.PreviousCloseResponse;
import net.jacobpeterson.domain.polygon.snapshot.SnapshotAllTickersResponse;
import net.jacobpeterson.domain.polygon.snapshot.SnapshotGainersLosersResponse;
import net.jacobpeterson.domain.polygon.snapshot.SnapshotSingleTickerResponse;
import net.jacobpeterson.domain.polygon.snapshot.SnapshotTickerBook;
import net.jacobpeterson.domain.polygon.stockdividends.StockDividendsResponse;
import net.jacobpeterson.domain.polygon.stockfinancials.StockFinancialsResponse;
import net.jacobpeterson.domain.polygon.stocksplits.StockSplitsResponse;
import net.jacobpeterson.domain.polygon.tickerdetails.TickerDetails;
import net.jacobpeterson.domain.polygon.tickernews.TickerNews;
import net.jacobpeterson.domain.polygon.tickers.TickersResponse;
import net.jacobpeterson.domain.polygon.tickertypes.TickerTypes;
import net.jacobpeterson.polygon.enums.ConditionMappingsType;
import net.jacobpeterson.polygon.enums.FinancialReportType;
import net.jacobpeterson.polygon.enums.FinancialSort;
import net.jacobpeterson.polygon.enums.GainersLosersDirection;
import net.jacobpeterson.polygon.enums.Market;
import net.jacobpeterson.polygon.enums.StockType;
import net.jacobpeterson.polygon.enums.TickerSort;
import net.jacobpeterson.polygon.enums.Timespan;
import net.jacobpeterson.polygon.properties.PolygonProperties;
import net.jacobpeterson.polygon.rest.PolygonRequest;
import net.jacobpeterson.polygon.rest.PolygonRequestBuilder;
import net.jacobpeterson.polygon.rest.exception.PolygonAPIRequestException;
import net.jacobpeterson.polygon.websocket.client.PolygonWebsocketClient;
import net.jacobpeterson.polygon.websocket.listener.PolygonStreamListener;
import net.jacobpeterson.util.time.TimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * The Class PolygonAPI.
 */
public class PolygonAPI {

    /** The logger. */
    private static Logger LOGGER = LogManager.getLogger(PolygonAPI.class);

    /** The Polygon websocket client. */
    private final PolygonWebsocketClient polygonWebsocketClient;

    /** The polygon request. */
    private final PolygonRequest polygonRequest;

    /** The base api url. */
    private String baseAPIURL;

    /** The Websocket url. */
    private String websocketURL;

    /** The Key id. */
    private String keyID;

    /**
     * Instantiates a new polygon API.
     */
    public PolygonAPI() {
        this(PolygonProperties.KEY_ID_VALUE);

        LOGGER.debug(PolygonProperties.staticToString());
    }

    /**
     * Instantiates a new polygon API.
     *
     * @param keyId the key id
     */
    public PolygonAPI(String keyId) {
        this(PolygonProperties.BASE_API_URL_VALUE, PolygonProperties.POLYGON_WEB_SOCKET_SERVER_URL_VALUE, keyId);
    }

    /**
     * Instantiates a new polygon API.
     *
     * @param baseAPIURL   the base api url
     * @param websocketURL the websocket url
     * @param keyID        the key id
     */
    public PolygonAPI(String baseAPIURL, String websocketURL, String keyID) {
        this.baseAPIURL = baseAPIURL;
        this.websocketURL = websocketURL;
        this.keyID = keyID;

        polygonRequest = new PolygonRequest(keyID);
        polygonWebsocketClient = new PolygonWebsocketClient(keyID, websocketURL);

        LOGGER.debug(this.toString());
    }

    /**
     * Query all ticker symbols which are supported by Polygon.io.
     *
     * @param tickerSort Which field to sort by.
     * @param stockType  If you want the results to only container a certain type.
     * @param market     Get tickers for a specific market
     * @param locale     Get tickers for a specific region/locale
     * @param search     Search the name of tickers
     * @param perpage    How many items to be on each page during pagination. Max 50
     * @param page       Which page of results to return
     * @param active     Filter for only active or inactive symbols
     *
     * @return the tickers
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Reference/get_v2_reference_tickers">Tickers</a>
     */
    public TickersResponse getTickers(TickerSort tickerSort, StockType stockType, Market market, String locale,
                                      String search, Integer perpage, Integer page, Boolean active) throws PolygonAPIRequestException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.REFERENCE_ENDPOINT,
                PolygonConstants.TICKERS_ENDPOINT);

        if (tickerSort != null) {
            builder.appendURLParameter(PolygonConstants.SORT_PARAMETER, tickerSort.getAPIName());
        }

        if (stockType != null) {
            builder.appendURLParameter(PolygonConstants.TYPE_PARAMETER, stockType.getAPIName());
        }

        if (market != null) {
            builder.appendURLParameter(PolygonConstants.MARKET_PARAMETER, market.getAPIName());
        }

        if (locale != null) {
            builder.appendURLParameter(PolygonConstants.LOCALE_PARAMETER, locale);
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

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, TickersResponse.class);
    }

    /**
     * Get the mapping of ticker types to descriptions / long names.
     *
     * @return the ticker types
     *
     * @see <a href="https://polygon.io/docs/#!/Reference/get_v2_reference_types">Ticker Types</a>
     */
    public TickerTypes getTickerTypes() throws PolygonAPIRequestException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.REFERENCE_ENDPOINT,
                PolygonConstants.TYPES_ENDPOINT);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, TickersResponse.class);
    }

    /**
     * Get the details of the symbol company/entity. These are important details which offer an overview of the entity.
     * Things like name, sector, description, logo and similar companies.
     *
     * @param symbol symbol we want details for
     *
     * @return the ticker details
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_company">Ticker Details</a>
     */
    public TickerDetails getTickerDetails(String symbol) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(symbol);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_1_ENDPOINT,
                PolygonConstants.META_ENDPOINT,
                PolygonConstants.SYMBOLS_ENDPOINT,
                symbol,
                PolygonConstants.COMPANY_ENDPOINT);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, TickerDetails.class);
    }

    /**
     * Get news articles for this symbol.
     *
     * @param symbol  the symbol we want details for
     * @param perpage How many items to be on each page during pagination. Max 50
     * @param page    Which page of results to return
     *
     * @return the ticker news
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_news">Ticker News</a>
     */
    public ArrayList<TickerNews> getTickerNews(String symbol, Integer perpage, Integer page)
            throws PolygonAPIRequestException {
        Preconditions.checkNotNull(symbol);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_1_ENDPOINT,
                PolygonConstants.META_ENDPOINT,
                PolygonConstants.SYMBOLS_ENDPOINT,
                symbol,
                PolygonConstants.NEWS_ENDPOINT);

        if (perpage != null) {
            builder.appendURLParameter(PolygonConstants.PERPAGE_PARAMETER, String.valueOf(perpage));
        }

        if (page != null) {
            builder.appendURLParameter(PolygonConstants.PAGE_PARAMETER, String.valueOf(page));
        }

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        Type listType = new TypeToken<ArrayList<TickerNews>>() {}.getType();

        return polygonRequest.getResponseObject(response, listType);
    }

    /**
     * Get the list of currently supported markets
     *
     * @return the markets
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Reference/get_v2_reference_markets">Markets</a>
     */
    public MarketsResponse getMarkets() throws PolygonAPIRequestException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.REFERENCE_ENDPOINT,
                PolygonConstants.MARKETS_ENDPOINT);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, MarketsResponse.class);
    }

    /**
     * Get the list of currently supported locales
     *
     * @return the locales
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Reference/get_v2_reference_locales">Locales</a>
     */
    public LocalesResponse getLocales() throws PolygonAPIRequestException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.REFERENCE_ENDPOINT,
                PolygonConstants.LOCALES_ENDPOINT);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, LocalesResponse.class);
    }

    /**
     * Get the historical splits for this symbol
     *
     * @param symbol symbol we want details for
     *
     * @return the stock splits
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Reference/get_v2_reference_splits_symbol">Stock Splits</a>
     */
    public StockSplitsResponse getStockSplits(String symbol) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(symbol);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.REFERENCE_ENDPOINT,
                PolygonConstants.SPLITS_ENDPOINT, symbol);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, StockSplitsResponse.class);
    }

    /**
     * Get the historical dividends for this symbol.
     *
     * @param symbol symbol we want details for
     *
     * @return the stock dividends
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_dividends">Stock Dividends</a>
     */
    public StockDividendsResponse getStockDividends(String symbol) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(symbol);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.REFERENCE_ENDPOINT,
                PolygonConstants.DIVIDENDS_ENDPOINT,
                symbol);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, StockDividendsResponse.class);
    }

    /**
     * Get the historical financials for this ticker.
     *
     * @param symbol              symbol we want details for
     * @param limit               limit the number of results
     * @param financialReportType type of reports
     * @param financialSort       sort direction
     *
     * @return the symbol financials
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Reference/get_v2_reference_financials_symbol">Stock Financials</a>
     */
    public StockFinancialsResponse getStockFinancials(String symbol, Integer limit,
                                                      FinancialReportType financialReportType, FinancialSort financialSort) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(symbol);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.REFERENCE_ENDPOINT,
                PolygonConstants.FINANCIALS_ENDPOINT,
                symbol);

        if (limit != null) {
            builder.appendURLParameter(PolygonConstants.LIMIT_PARAMETER, String.valueOf(limit));
        }

        if (financialReportType != null) {
            builder.appendURLParameter(PolygonConstants.TYPE_PARAMETER, financialReportType.getAPIName());
        }

        if (financialSort != null) {
            builder.appendURLParameter(PolygonConstants.SORT_PARAMETER, financialSort.getAPIName());
        }

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, StockFinancialsResponse.class);
    }

    /**
     * Current status of each market.
     *
     * @return the market status
     *
     * @throws PolygonAPIRequestException the polygon api request exception
     * @see <a href="https://polygon.io/docs/#!/Reference/get_v1_marketstatus_now">Market Status</a>
     */
    public MarketStatus getMarketStatus() throws PolygonAPIRequestException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_1_ENDPOINT,
                PolygonConstants.MARKET_STATUS_ENDPOINT,
                PolygonConstants.NOW_ENDPOINT);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, MarketStatus.class);
    }

    /**
     * Get upcoming market holidays and their open/close times.
     *
     * @return the market holidays
     *
     * @throws PolygonAPIRequestException the polygon api request exception
     * @see <a href="https://polygon.io/docs/#!/Reference/get_v1_marketstatus_upcoming">Market Holidays</a>
     */
    public ArrayList<MarketHoliday> getMarketHolidays() throws PolygonAPIRequestException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_1_ENDPOINT,
                PolygonConstants.MARKET_STATUS_ENDPOINT,
                PolygonConstants.UPCOMING_ENDPOINT);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        Type listType = new TypeToken<ArrayList<MarketHoliday>>() {}.getType();

        return polygonRequest.getResponseObject(response, listType);
    }

    /**
     * List of stock exchanges which are supported by Polygon.io
     *
     * @return the exchanges
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v1_meta_exchanges">Exchanges</a>
     */
    public ArrayList<Exchange> getExchanges() throws PolygonAPIRequestException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_1_ENDPOINT,
                PolygonConstants.META_ENDPOINT,
                PolygonConstants.EXCHANGES_ENDPOINT);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        Type listType = new TypeToken<ArrayList<Exchange>>() {}.getType();

        return polygonRequest.getResponseObject(response, listType);
    }

    /**
     * Get historic trades for a symbol.
     *
     * @param ticker         Ticker symbol we want ticks for
     * @param date           Date/Day of the historic ticks to retrieve
     * @param timestamp      Timestamp offset, used for pagination. This is the offset at which to start the results.
     *                       Using the timestamp of the last result as the offset will give you the next page of
     *                       results.
     * @param timestampLimit Maximum timestamp allowed in the results.
     * @param reverse        Reverse the order of the results. This is useful in combination with timestamp param.
     * @param limit          Limit the size of response, Max 50000
     *
     * @return the historic trades
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v2_ticks_stocks_trades_ticker_date">Historic
     * Trades</a>
     */
    public HistoricTradesResponse getHistoricTrades(String ticker, LocalDate date, Long timestamp, Long timestampLimit,
            Boolean reverse, Integer limit) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(ticker);
        Preconditions.checkNotNull(date);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.TICKS_ENDPOINT,
                PolygonConstants.STOCKS_ENDPOINT,
                PolygonConstants.TRADES_ENDPOINT,
                ticker,
                TimeUtil.toDateString(date));

        if (timestamp != null) {
            builder.appendURLParameter(PolygonConstants.TIMESTAMP_PARAMETER, String.valueOf(timestamp));
        }

        if (timestampLimit != null) {
            builder.appendURLParameter(PolygonConstants.TIMESTAMP_LIMIT_PARAMETER, String.valueOf(timestampLimit));
        }

        if (reverse != null) {
            builder.appendURLParameter(PolygonConstants.REVERSE_PARAMETER, String.valueOf(reverse));
        }

        if (limit != null) {
            builder.appendURLParameter(PolygonConstants.LIMIT_PARAMETER, String.valueOf(limit));
        }

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, HistoricTradesResponse.class);
    }

    /**
     * Get historic NBBO quotes for a ticker.
     *
     * @param ticker         Ticker symbol we want ticks for
     * @param date           Date/Day of the historic ticks to retrieve
     * @param timestamp      Timestamp offset, used for pagination. This is the offset at which to start the results.
     *                       Using the timestamp of the last result as the offset will give you the next page of
     *                       results.
     * @param timestampLimit Maximum timestamp allowed in the results.
     * @param reverse        Reverse the order of the results. This is useful in combination with timestamp param.
     * @param limit          Limit the size of response, Max 50000
     *
     * @return the historic quotes
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v2_ticks_stocks_nbbo_ticker_date">Historic
     * Quotes</a>
     */
    public HistoricQuotesResponse getHistoricQuotes(String ticker, LocalDate date, Long timestamp, Long timestampLimit,
            Boolean reverse, Integer limit) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(ticker);
        Preconditions.checkNotNull(date);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.TICKS_ENDPOINT,
                PolygonConstants.STOCKS_ENDPOINT,
                PolygonConstants.NBBO_ENDPOINT,
                ticker,
                TimeUtil.toDateString(date));

        if (timestamp != null) {
            builder.appendURLParameter(PolygonConstants.TIMESTAMP_PARAMETER, String.valueOf(timestamp));
        }

        if (timestampLimit != null) {
            builder.appendURLParameter(PolygonConstants.TIMESTAMP_LIMIT_PARAMETER, String.valueOf(timestampLimit));
        }

        if (reverse != null) {
            builder.appendURLParameter(PolygonConstants.REVERSE_PARAMETER, String.valueOf(reverse));
        }

        if (limit != null) {
            builder.appendURLParameter(PolygonConstants.LIMIT_PARAMETER, String.valueOf(limit));
        }

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, HistoricQuotesResponse.class);
    }

    /**
     * Get the last trade for a given stock.
     *
     * @param symbol Symbol of the stock to get
     *
     * @return the last trade
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_stocks_symbol">Last Trade</a>
     */
    public LastTradeResponse getLastTrade(String symbol) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(symbol);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_1_ENDPOINT,
                PolygonConstants.LAST_ENDPOINT,
                PolygonConstants.STOCKS_ENDPOINT,
                symbol);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, LastTradeResponse.class);
    }

    /**
     * Get the last quote for a given stock.
     *
     * @param symbol Symbol of the stock to get
     *
     * @return the last quote
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_quote_stocks_symbol">Last Quote</a>
     */
    public LastQuoteResponse getLastQuote(String symbol) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(symbol);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_1_ENDPOINT,
                PolygonConstants.LAST_QUOTE_ENDPOINT,
                PolygonConstants.STOCKS_ENDPOINT,
                symbol);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, LastQuoteResponse.class);
    }

    /**
     * Get the open, close and afterhours prices of a symbol on a certain date.
     *
     * @param symbol Symbol of the stock to get
     * @param date   Date of the requested open/close
     *
     * @return the daily open close
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v1_open_close_symbol_date">Daily Open/Close</a>
     */
    public DailyOpenCloseResponse getDailyOpenClose(String symbol, LocalDate date) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(symbol);
        Preconditions.checkNotNull(date);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_1_ENDPOINT,
                PolygonConstants.OPEN_CLOSE_ENDPOINT,
                symbol,
                TimeUtil.toDateString(date));

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, DailyOpenCloseResponse.class);
    }

    /**
     * The mappings for conditions on trades and quotes.
     *
     * @param conditionMappingsType Ticker type we want mappings for
     *
     * @return the conditions mapping
     *
     * @throws PolygonAPIRequestException the polygon api request exception
     * @see <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v1_meta_conditions_ticktype">Condition Mappings</a>
     */
    public ConditionsMapping getConditionsMapping(ConditionMappingsType conditionMappingsType)
            throws PolygonAPIRequestException {
        Preconditions.checkNotNull(conditionMappingsType);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_1_ENDPOINT,
                PolygonConstants.META_ENDPOINT,
                PolygonConstants.CONDITIONS_ENDPOINT,
                conditionMappingsType.getAPIName());

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, ConditionsMapping.class);
    }

    /**
     * Snapshot allows you to see all tickers current minute aggregate, daily aggregate and last trade. As well as
     * previous days aggregate and calculated change for today.
     *
     * @return the snapshot all tickers
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see
     * <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers">Snapshot
     * - All Tickers</a>
     */
    public SnapshotAllTickersResponse getSnapshotAllTickers() throws PolygonAPIRequestException {
        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.SNAPSHOT_ENDPOINT,
                PolygonConstants.LOCALE_ENDPOINT,
                PolygonConstants.US_ENDPOINT,
                PolygonConstants.MARKETS_ENDPOINT,
                PolygonConstants.STOCKS_ENDPOINT,
                PolygonConstants.TICKERS_ENDPOINT);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, SnapshotAllTickersResponse.class);
    }

    /**
     * See the current snapshot of a single ticker
     *
     * @param ticker Ticker of the snapshot
     *
     * @return the snapshot single ticker
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see
     * <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers_ticker">Snapshot
     * - Single Ticker</a>
     */
    public SnapshotSingleTickerResponse getSnapshotSingleTicker(String ticker) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(ticker);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.SNAPSHOT_ENDPOINT,
                PolygonConstants.LOCALE_ENDPOINT,
                PolygonConstants.US_ENDPOINT,
                PolygonConstants.MARKETS_ENDPOINT,
                PolygonConstants.STOCKS_ENDPOINT,
                PolygonConstants.TICKERS_ENDPOINT,
                ticker);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, SnapshotSingleTickerResponse.class);
    }

    /**
     * See the current snapshot of the top 20 gainers of the day at the moment.
     *
     * @param gainersLosersDirection the gainers losers direction
     *
     * @return the snapshots gainers
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see
     * <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_gainers">Snapshot
     * - Gainers / Losers</a>
     */
    public SnapshotGainersLosersResponse getSnapshotsGainersLosers(GainersLosersDirection gainersLosersDirection)
            throws PolygonAPIRequestException {
        Preconditions.checkNotNull(gainersLosersDirection);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.SNAPSHOT_ENDPOINT,
                PolygonConstants.LOCALE_ENDPOINT,
                PolygonConstants.US_ENDPOINT,
                PolygonConstants.MARKETS_ENDPOINT,
                PolygonConstants.STOCKS_ENDPOINT,
                gainersLosersDirection.getAPIName());

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, SnapshotGainersLosersResponse.class);
    }

    /**
     * See the current snapshot of Level II data on IEX for the given ticker.
     *
     * @param locale the locale
     * @param market the market
     * @param ticker the ticker
     *
     * @return the snapshot ticker book
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="">Docs not public yet</a>
     */
    public SnapshotTickerBook getSnapshotTickerBook(String locale, Market market, String ticker)
            throws PolygonAPIRequestException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(market);
        Preconditions.checkNotNull(ticker);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.SNAPSHOT_ENDPOINT,
                PolygonConstants.LOCALE_ENDPOINT,
                locale,
                PolygonConstants.MARKETS_ENDPOINT,
                market.getAPIName(),
                PolygonConstants.TICKERS_ENDPOINT,
                ticker,
                PolygonConstants.BOOK_ENDPOINT);

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, SnapshotTickerBook.class);
    }

    /**
     * Get the previous day close for the specified ticker
     *
     * @param ticker     Ticker symbol of the request
     * @param unadjusted Set to true if the results should NOT be adjusted for splits.
     *
     * @return the previous close
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_prev">Previous Close</a>
     */
    public PreviousCloseResponse getPreviousClose(String ticker, Boolean unadjusted) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(ticker);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.AGGS_ENDPOINT,
                PolygonConstants.TICKER_ENDPOINT,
                ticker,
                PolygonConstants.PREV_ENDPOINT);

        if (unadjusted != null) {
            builder.appendURLParameter(PolygonConstants.UNADJUSTED_PARAMETER, unadjusted.toString());
        }

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, PreviousCloseResponse.class);
    }

    /**
     * Get aggregates for a date range, in custom time window sizes
     *
     * @param ticker     Ticker symbol of the request
     * @param multiplier Size of the timespan multiplier
     * @param timeSpan   Size of the time window
     * @param fromDate   From date
     * @param toDate     To date
     * @param unadjusted Set to true if the results should NOT be adjusted for splits
     *
     * @return the aggregates
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see
     * <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_range_multiplier_timespan_from_to">Aggregates</a>
     */
    public AggregatesResponse getAggregates(String ticker, Integer multiplier, Timespan timeSpan, LocalDate fromDate,
                                            LocalDate toDate, Boolean unadjusted) throws PolygonAPIRequestException {
        Preconditions.checkNotNull(ticker);
        Preconditions.checkNotNull(timeSpan);
        Preconditions.checkNotNull(fromDate);
        Preconditions.checkNotNull(toDate);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.AGGS_ENDPOINT,
                PolygonConstants.TICKER_ENDPOINT,
                ticker,
                PolygonConstants.RANGE_ENDPOINT,
                Integer.toString((multiplier == null) ? 1 : multiplier),
                timeSpan.getAPIName(),
                TimeUtil.toDateString(fromDate),
                TimeUtil.toDateString(toDate));

        if (unadjusted != null) {
            builder.appendURLParameter(PolygonConstants.UNADJUSTED_PARAMETER, unadjusted.toString());
        }

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, AggregatesResponse.class);
    }

    /**
     * Get the daily OHLC for entire markets.
     *
     * @param locale     Locale of the aggregates ( See 'Locales' API )
     * @param market     Market of the aggregates ( See 'Markets' API )
     * @param date       To date
     * @param unadjusted Set to true if the results should NOT be adjusted for splits.
     *
     * @return the grouped daily
     *
     * @throws PolygonAPIRequestException the polygon API exception
     * @see
     * <a href="https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_grouped_locale_locale_market_market_date">Grouped
     * Daily</a>
     */
    public GroupedDailyResponse getGroupedDaily(String locale, Market market, LocalDate date, Boolean unadjusted)
            throws PolygonAPIRequestException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(market);
        Preconditions.checkNotNull(date);

        PolygonRequestBuilder builder = new PolygonRequestBuilder(baseAPIURL, PolygonConstants.VERSION_2_ENDPOINT,
                PolygonConstants.AGGS_ENDPOINT,
                PolygonConstants.GROUPED_ENDPOINT,
                PolygonConstants.LOCALE_ENDPOINT,
                locale,
                PolygonConstants.MARKET_ENDPOINT,
                market.getAPIName(),
                TimeUtil.toDateString(date));

        if (unadjusted != null) {
            builder.appendURLParameter(PolygonConstants.UNADJUSTED_PARAMETER, unadjusted.toString());
        }

        HttpResponse<InputStream> response = polygonRequest.invokeGet(builder);

        if (response.getStatus() != 200) {
            throw new PolygonAPIRequestException(response);
        }

        return polygonRequest.getResponseObject(response, GroupedDailyResponse.class);
    }

    /**
     * Adds the polygon stream listener.
     *
     * @param streamListener the stream listener
     */
    public void addPolygonStreamListener(PolygonStreamListener streamListener) {
        polygonWebsocketClient.addListener(streamListener);
    }

    /**
     * Removes the polygon stream listener.
     *
     * @param streamListener the stream listener
     */
    public void removePolygonStreamListener(PolygonStreamListener streamListener) {
        polygonWebsocketClient.removeListener(streamListener);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("baseAPIURL = " + baseAPIURL)
                .add("keyID = " + keyID)
                .add("websocketURL = " + websocketURL)
                .toString();
    }
}
