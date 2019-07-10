package io.github.mainstringargs.polygon;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
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
import io.github.mainstringargs.polygon.domain.reference.Market;
import io.github.mainstringargs.polygon.domain.reference.Split;
import io.github.mainstringargs.polygon.domain.reference.Tickers;
import io.github.mainstringargs.polygon.domain.reference.TypesMapping;
import io.github.mainstringargs.polygon.enums.Locale;
import io.github.mainstringargs.polygon.enums.Sort;
import io.github.mainstringargs.polygon.enums.Timespan;
import io.github.mainstringargs.polygon.nats.PolygonNatsClient;
import io.github.mainstringargs.polygon.nats.PolygonStreamListener;
import io.github.mainstringargs.polygon.properties.PolygonProperties;
import io.github.mainstringargs.polygon.rest.PolygonRequest;
import io.github.mainstringargs.polygon.rest.PolygonRequestBuilder;
import io.github.mainstringargs.polygon.rest.exceptions.PolygonAPIException;

/**
 * The Class PolygonAPI.
 */
public class PolygonAPI {

  /** The logger. */
  private static Logger LOGGER = LogManager.getLogger(PolygonAPI.class);

  /** The polygon nats client. */
  private final PolygonNatsClient polygonNatsClient;

  /** The polygon request. */
  private final PolygonRequest polygonRequest;

  /** The base data url. */
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
   * @param keyId the key id
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
   *
   * @param symbol we want the endpoint list for.
   * @return the symbol endpoints
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol</a>
   */
  public SymbolEndpoints getSymbolEndpoints(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT, PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    SymbolEndpoints symbolEndpoints =
        polygonRequest.getResponseObject(response, SymbolEndpoints.class);

    return symbolEndpoints;
  }

  /**
   * Get the details of the symbol company/entity. These are important details which offer an
   * overview of the entity. Things like name, sector, description, logo and similar companies.
   *
   * @param symbol we want details for
   * @return the symbol details
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_company">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_company</a>
   */
  public SymbolDetails getSymbolDetails(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT, PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(PolygonConstants.COMPANY_ENDPOINT);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);


    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    SymbolDetails symbolDetails = polygonRequest.getResponseObject(response, SymbolDetails.class);

    return symbolDetails;
  }

  /**
   * Get the analyst ratings of the symbol company/entity. Ratings are from current date, up to
   * 5months into the future.
   *
   * @param symbol we want analyst ratings for
   * @return the symbol analyst ratings
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_analysts">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_analysts</a>
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

    SymbolAnalystRatings symbolDetails =
        polygonRequest.getResponseObject(response, SymbolAnalystRatings.class);

    return symbolDetails;
  }

  /**
   * Get the historical dividends for this symbol.
   *
   * @param symbol we want details for
   * @return the symbol dividends
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_dividends">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_dividends</a>
   */
  public List<SymbolDividend> getSymbolDividends(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT, PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(PolygonConstants.DIVIDENDS_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolDividend>>() {}.getType();

    List<SymbolDividend> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }

  /**
   * Get the historical earnings for a company
   *
   * @param symbol we want details for
   * @return the symbol earnings
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_earnings">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_earnings</a>
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

    Type listType = new TypeToken<List<SymbolEarning>>() {}.getType();

    List<SymbolEarning> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }


  /**
   * Get the historical financials for a company
   *
   * @param symbol we want details for
   * @return the symbol financials
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_financials">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_financials</a>
   */
  public List<SymbolFinancial> getSymbolFinancials(String symbol) throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT, PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(PolygonConstants.FINANCIALS_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolFinancial>>() {}.getType();

    List<SymbolFinancial> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }

  /**
   * Get news articles for this symbol.
   *
   * @param symbol the symbol we want details for
   * @return the symbol news
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_news">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_news</a>
   */
  public List<SymbolNews> getSymbolNews(String symbol) throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT, PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(PolygonConstants.NEWS_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolNews>>() {}.getType();


    List<SymbolNews> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }

  /**
   * Get news articles for this symbol.
   *
   * @param symbol the symbol we want details for
   * @param perpage How many items to be on each page during pagination. Max 50
   * @param page Which page of results to return
   * @return the symbol news
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_news">https://polygon.io/docs/#!/Meta-Data/get_v1_meta_symbols_symbol_news</a>
   */
  public List<SymbolNews> getSymbolNews(String symbol, Integer perpage, Integer page)
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

    Type listType = new TypeToken<List<SymbolNews>>() {}.getType();


    List<SymbolNews> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }

  /**
   * Query all ticker symbols which are supported by Polygon.io.
   *
   * @param sort Which field to sort by.
   * @param type If you want the results to only container a certain type.
   * @param market Get tickers for a specific market
   * @param locale Get tickers for a specific region/locale
   * @param search Search the name of tickers
   * @param perpage How many items to be on each page during pagination. Max 50
   * @param page Which page of results to return
   * @param active Filter for only active or inactive symbols
   * @return the tickers
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Reference/get_v2_reference_tickers">https://polygon.io/docs/#!/Reference/get_v2_reference_tickers</a>
   */
  public Tickers getTickers(Sort sort, io.github.mainstringargs.polygon.enums.Type type,
      io.github.mainstringargs.polygon.enums.Market market, Locale locale, String search,
      Integer perpage, Integer page, Boolean active) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.REFERENCE_ENDPOINT, PolygonConstants.TICKERS_ENDPOINT);

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

    Tickers symbolDetails = polygonRequest.getResponseObject(response, Tickers.class);

    return symbolDetails;
  }

  /**
   * Get the list of currently supported markets
   *
   * @return the markets
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Reference/get_v2_reference_markets">https://polygon.io/docs/#!/Reference/get_v2_reference_markets</a>
   */
  public List<Market> getMarkets() throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.REFERENCE_ENDPOINT, PolygonConstants.MARKETS_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    Type listType = new TypeToken<List<Market>>() {}.getType();

    List<Market> markets = polygonRequest.getResponseObject(response, listType);

    return markets;
  }

  /**
   * Get the list of currently supported locales
   *
   * @return the locales
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Reference/get_v2_reference_locales">https://polygon.io/docs/#!/Reference/get_v2_reference_locales</a>
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


    Type listType =
        new TypeToken<List<io.github.mainstringargs.polygon.domain.reference.Locale>>() {}
            .getType();

    List<io.github.mainstringargs.polygon.domain.reference.Locale> locales =
        polygonRequest.getResponseObject(response, listType);

    return locales;
  }

  /**
   * Get the mapping of ticker types to descriptions / long names
   *
   * @return the types mapping
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Reference/get_v2_reference_types">https://polygon.io/docs/#!/Reference/get_v2_reference_types</a>
   */
  public TypesMapping getTypesMapping() throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.REFERENCE_ENDPOINT, PolygonConstants.TYPES_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    TypesMapping typesMapping = polygonRequest.getResponseObject(response, TypesMapping.class);

    return typesMapping;
  }

  /**
   * Get the historical splits for this symbol
   *
   * @param symbol we want details for
   * @return the splits
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Reference/get_v2_reference_splits_symbol">https://polygon.io/docs/#!/Reference/get_v2_reference_splits_symbol</a>
   */
  public List<Split> getSplits(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.REFERENCE_ENDPOINT, PolygonConstants.SPLITS_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

    builder.appendEndpoint(symbol);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<Split>>() {}.getType();


    List<Split> splits = polygonRequest.getResponseObject(response, listType);

    return splits;
  }

  /**
   * List of stock exchanges which are supported by Polygon.io
   *
   * @return the exchange
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v1_meta_exchanges">https://polygon.io/docs/#!/Stocks--Equities/get_v1_meta_exchanges</a>
   */
  public List<Exchange> getExchanges() throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT, PolygonConstants.EXCHANGES_ENDPOINT);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<Exchange>>() {}.getType();


    List<Exchange> exchanges = polygonRequest.getResponseObject(response, listType);

    return exchanges;
  }

  /**
   * Get historic trades for a symbol.
   *
   * @param symbol the symbol of the company to retrieve
   * @param date Date/Day of the historic ticks to retreive
   * @param offset Timestamp offset, used for pagination. This is the offset at which to start the
   *        results. Using the timestamp of the last result as the offset will give you the next
   *        page of results.
   * @param limit Limit the size of response, Max 50000
   * @return the historic trades
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v1_historic_trades_symbol_date">https://polygon.io/docs/#!/Stocks--Equities/get_v1_historic_trades_symbol_date</a>
   */
  public Trades getHistoricTrades(String symbol, LocalDate date, Integer offset, Integer limit)
      throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.HISTORIC_ENDPOINT, PolygonConstants.TRADES_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(Utilities.toDateString(date));
    if (offset != null) {
      builder.appendURLParameter(PolygonConstants.OFFSET_PARAMETER, String.valueOf(offset));
    }

    if (limit != null) {
      builder.appendURLParameter(PolygonConstants.LIMIT_PARAMETER, String.valueOf(limit));
    }

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Trades tradesDetails = polygonRequest.getResponseObject(response, Trades.class);

    return tradesDetails;
  }

  /**
   * Get historic quotes for a symbol.
   *
   * @param symbol the symbol of the company to retrieve
   * @param date Date/Day of the historic ticks to retreive
   * @param offset Timestamp offset, used for pagination. This is the offset at which to start the
   *        results. Using the timestamp of the last result as the offset will give you the next
   *        page of results.
   * @param limit Limit the size of response, Max 50000
   * @return the historic quotes
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v1_historic_quotes_symbol_date">https://polygon.io/docs/#!/Stocks--Equities/get_v1_historic_quotes_symbol_date</a>
   */
  public Quotes getHistoricQuotes(String symbol, LocalDate date, Integer offset, Integer limit)
      throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.HISTORIC_ENDPOINT, PolygonConstants.QUOTES_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(Utilities.toDateString(date));
    if (offset != null) {
      builder.appendURLParameter(PolygonConstants.OFFSET_PARAMETER, String.valueOf(offset));
    }

    if (limit != null) {
      builder.appendURLParameter(PolygonConstants.LIMIT_PARAMETER, String.valueOf(limit));
    }

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Quotes quotesDetails = polygonRequest.getResponseObject(response, Quotes.class);

    return quotesDetails;
  }

  /**
   * Get the last trade for a given stock.
   *
   * @param symbol Symbol of the stock to get
   * @return the last trade
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_stocks_symbol">https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_stocks_symbol</a>
   */
  public Trade getLastTrade(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.LAST_ENDPOINT, PolygonConstants.STOCKS_ENDPOINT);

    builder.appendEndpoint(symbol);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Trade stockTrade = polygonRequest.getResponseObject(response, Trade.class);

    return stockTrade;
  }

  /**
   * Get the last quote tick for a given stock.
   *
   * @param symbol Symbol of the quote to get
   * @return the last quote
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_quote_stocks_symbol">https://polygon.io/docs/#!/Stocks--Equities/get_v1_last_quote_stocks_symbol</a>
   */
  public Quote getLastQuote(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.LAST_QUOTE_ENDPOINT, PolygonConstants.STOCKS_ENDPOINT);

    builder.appendEndpoint(symbol);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Quote stockQuote = polygonRequest.getResponseObject(response, Quote.class);

    return stockQuote;
  }

  /**
   * Get the open, close and afterhours prices of a symbol on a certain date.
   *
   * @param symbol Symbol of the stock to get
   * @param date Date of the requested open/close
   * @return the daily open close
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v1_open_close_symbol_date">https://polygon.io/docs/#!/Stocks--Equities/get_v1_open_close_symbol_date</a>
   */
  public DailyOpenClose getDailyOpenClose(String symbol, LocalDate date)
      throws PolygonAPIException {

    PolygonRequestBuilder builder =
        new PolygonRequestBuilder(baseDataUrl, PolygonConstants.OPEN_CLOSE_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(Utilities.toDateString(date));

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    DailyOpenClose dailyOpenClose =
        polygonRequest.getResponseObject(response, DailyOpenClose.class);

    return dailyOpenClose;
  }

  /**
   * Snapshot allows you to see all tickers current minute aggregate, daily aggregate and last
   * trade. As well as previous days aggregate and calculated change for today.
   *
   * @return the snapshot all tickers
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers">https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers</a>
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

    Type listType = new TypeToken<List<Snapshot>>() {}.getType();

    List<Snapshot> snapshots = polygonRequest.getResponseObject(response, listType);

    return snapshots;
  }

  /**
   * See the current snapshot of a single ticker
   *
   * @param symbol Ticker of the snapshot
   * @return the snapshot
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers_ticker">https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_tickers_ticker</a>
   */
  public Snapshot getSnapshot(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.SNAPSHOT_ENDPOINT, PolygonConstants.LOCALE_ENDPOINT,
        PolygonConstants.US_ENDPOINT, PolygonConstants.MARKETS_ENDPOINT,
        PolygonConstants.STOCKS_ENDPOINT, PolygonConstants.TICKERS_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
    builder.appendEndpoint(symbol);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    Snapshot snapshot = polygonRequest.getResponseObject(response, Snapshot.class);

    return snapshot;
  }

  /**
   * See the current snapshot of the top 20 gainers of the day at the moment.
   *
   * @return the snapshots gainers
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_gainers">https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_gainers</a>
   */
  public List<Snapshot> getSnapshotsGainers() throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.SNAPSHOT_ENDPOINT, PolygonConstants.LOCALE_ENDPOINT,
        PolygonConstants.US_ENDPOINT, PolygonConstants.MARKETS_ENDPOINT,
        PolygonConstants.STOCKS_ENDPOINT, PolygonConstants.GAINERS_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<Snapshot>>() {}.getType();

    List<Snapshot> snapshots = polygonRequest.getResponseObject(response, listType);

    return snapshots;
  }

  /**
   * See the current snapshot of the top 20 losers of the day at the moment.
   *
   * @return the snapshots losers
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_losers">https://polygon.io/docs/#!/Stocks--Equities/get_v2_snapshot_locale_us_markets_stocks_losers</a>
   */
  public List<Snapshot> getSnapshotsLosers() throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.SNAPSHOT_ENDPOINT, PolygonConstants.LOCALE_ENDPOINT,
        PolygonConstants.US_ENDPOINT, PolygonConstants.MARKETS_ENDPOINT,
        PolygonConstants.STOCKS_ENDPOINT, PolygonConstants.LOSERS_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<Snapshot>>() {}.getType();

    List<Snapshot> snapshots = polygonRequest.getResponseObject(response, listType);

    return snapshots;
  }

  /**
   * Get the previous day close for the specified ticker
   *
   * @param ticker Ticker symbol of the request
   * @param unadjusted Set to true if the results should NOT be adjusted for splits.
   * @return the previous close
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_prev">https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_prev</a>
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
   * Get aggregates for a date range, in custom time window sizes
   *
   * @param ticker Ticker symbol of the request
   * @param multiplier Size of the timespan multiplier
   * @param timeSpan Size of the time window
   * @param fromDate the from date
   * @param toDate the to date
   * @param unadjusted Set to true if the results should NOT be adjusted for splits
   * @return the aggregates
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_range_multiplier_timespan_from_to">https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_ticker_ticker_range_multiplier_timespan_from_to</a>
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
   * @param locale Locale of the aggregates ( See 'Locales' API )
   * @param market Market of the aggregates ( See 'Markets' API )
   * @param date to date
   * @param unadjusted Set to true if the results should NOT be adjusted for splits.
   * @return the grouped daily
   * @throws PolygonAPIException the polygon API exception
   * @see <a href=
   *      "https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_grouped_locale_locale_market_market_date">https://polygon.io/docs/#!/Stocks--Equities/get_v2_aggs_grouped_locale_locale_market_market_date</a>
   */
  public Aggregates getGroupedDaily(Locale locale,
      io.github.mainstringargs.polygon.enums.Market market, LocalDate date, Boolean unadjusted)
      throws PolygonAPIException {

    PolygonRequestBuilder builder =
        new PolygonRequestBuilder(baseDataUrl, PolygonConstants.AGGS_ENDPOINT,
            PolygonConstants.GROUPED_ENDPOINT, PolygonConstants.LOCALE_ENDPOINT);

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
