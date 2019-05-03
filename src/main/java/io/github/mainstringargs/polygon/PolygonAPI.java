package io.github.mainstringargs.polygon;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
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
    this(PolygonProperties.KEY_ID_VALUE, PolygonProperties.POLYGON_NATS_SERVERS_VALUE);

  }

  /**
   * Instantiates a new polygon API.
   *
   * @param keyId the key id
   * @param polygonNatsServers the polygon nats servers
   */
  public PolygonAPI(String keyId, String... polygonNatsServers) {

    LOGGER.info("PolygonAPI is using the following properties: \nkeyId: " + keyId
        + "\npolygonNatsServers " + Arrays.toString(polygonNatsServers));

    polygonRequest = new PolygonRequest(keyId);
    polygonNatsClient = new PolygonNatsClient(keyId, polygonNatsServers);
    baseDataUrl = PolygonProperties.BASE_DATA_URL_VALUE;

  }

  /**
   * Gets the symbol endpoints.
   *
   * @param symbol the symbol
   * @return the symbol endpoints
   * @throws PolygonAPIException the polygon API exception
   */
  public SymbolEndpoints getSymbolEndpoints(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "meta/symbols");

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
   * Gets the symbol details.
   *
   * @param symbol the symbol
   * @return the symbol details
   * @throws PolygonAPIException the polygon API exception
   */
  public SymbolDetails getSymbolDetails(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "meta/symbols");

    builder.appendEndpoint(symbol);
    builder.appendEndpoint("company");

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);


    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    SymbolDetails symbolDetails = polygonRequest.getResponseObject(response, SymbolDetails.class);

    return symbolDetails;
  }

  /**
   * Gets the symbol analyst ratings.
   *
   * @param symbol the symbol
   * @return the symbol analyst ratings
   * @throws PolygonAPIException the polygon API exception
   */
  public SymbolAnalystRatings getSymbolAnalystRatings(String symbol) throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "meta/symbols");

    builder.appendEndpoint(symbol);
    builder.appendEndpoint("analysts");


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    SymbolAnalystRatings symbolDetails =
        polygonRequest.getResponseObject(response, SymbolAnalystRatings.class);

    return symbolDetails;
  }

  /**
   * Gets the symbol dividends.
   *
   * @param symbol the symbol
   * @return the symbol dividends
   * @throws PolygonAPIException the polygon API exception
   */
  public List<SymbolDividend> getSymbolDividends(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "meta/symbols");

    builder.appendEndpoint(symbol);
    builder.appendEndpoint("dividends");


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolDividend>>() {}.getType();

    List<SymbolDividend> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }

  /**
   * Gets the symbol earnings.
   *
   * @param symbol the symbol
   * @return the symbol earnings
   * @throws PolygonAPIException the polygon API exception
   */
  public List<SymbolEarning> getSymbolEarnings(String symbol) throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "meta/symbols");

    builder.appendEndpoint(symbol);
    builder.appendEndpoint("earnings");


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolEarning>>() {}.getType();

    List<SymbolEarning> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }


  /**
   * Gets the symbol financials.
   *
   * @param symbol the symbol
   * @return the symbol financials
   * @throws PolygonAPIException the polygon API exception
   */
  public List<SymbolFinancial> getSymbolFinancials(String symbol) throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "meta/symbols");

    builder.appendEndpoint(symbol);
    builder.appendEndpoint("financials");


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolFinancial>>() {}.getType();

    List<SymbolFinancial> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }

  /**
   * Gets the symbol news.
   *
   * @param symbol the symbol
   * @return the symbol news
   * @throws PolygonAPIException the polygon API exception
   */
  public List<SymbolNews> getSymbolNews(String symbol) throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "meta/symbols");

    builder.appendEndpoint(symbol);
    builder.appendEndpoint("news");


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolNews>>() {}.getType();


    List<SymbolNews> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }

  /**
   * Gets the symbol news.
   *
   * @param symbol the symbol
   * @param perpage the perpage
   * @param page the page
   * @return the symbol news
   * @throws PolygonAPIException the polygon API exception
   */
  public List<SymbolNews> getSymbolNews(String symbol, Integer perpage, Integer page)
      throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "meta/symbols");

    builder.appendEndpoint(symbol);
    builder.appendEndpoint("news");
    if (perpage != null) {
      builder.appendURLParameter("perpage", perpage + "");
    }

    if (page != null) {
      builder.appendURLParameter("page", page + "");
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
   * Gets the tickers.
   *
   * @param sort the sort
   * @param type the type
   * @param market the market
   * @param locale the locale
   * @param search the search
   * @param perpage the perpage
   * @param page the page
   * @param active the active
   * @return the tickers
   * @throws PolygonAPIException the polygon API exception
   */
  public Tickers getTickers(Sort sort, io.github.mainstringargs.polygon.enums.Type type,
      io.github.mainstringargs.polygon.enums.Market market, Locale locale, String search,
      Integer perpage, Integer page, Boolean active) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "reference/tickers");

    builder.setVersion("v2");

    if (sort != null) {
      builder.appendURLParameter("sort", sort.getAPIName());
    }

    if (type != null) {
      builder.appendURLParameter("type", type.getAPIName());
    }

    if (market != null) {
      builder.appendURLParameter("market", market.getAPIName());
    }

    if (locale != null) {
      builder.appendURLParameter("locale", locale.getAPIName());
    }

    if (search != null) {
      builder.appendURLParameter("search", search);
    }

    if (perpage != null) {
      builder.appendURLParameter("perpage", perpage + "");
    }

    if (page != null) {
      builder.appendURLParameter("page", page + "");
    }

    if (active != null) {
      builder.appendURLParameter("active", active + "");
    }

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Tickers symbolDetails = polygonRequest.getResponseObject(response, Tickers.class);

    return symbolDetails;
  }

  /**
   * Gets the markets.
   *
   * @return the markets
   * @throws PolygonAPIException the polygon API exception
   */
  public List<Market> getMarkets() throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "reference/markets");

    builder.setVersion("v2");


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    Type listType = new TypeToken<List<Market>>() {}.getType();

    List<Market> markets = polygonRequest.getResponseObject(response, listType);

    return markets;
  }

  /**
   * Gets the locales.
   *
   * @return the locales
   * @throws PolygonAPIException the polygon API exception
   */
  public List<io.github.mainstringargs.polygon.domain.reference.Locale> getLocales()
      throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "reference/locales");

    builder.setVersion("v2");


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
   * Gets the types mapping.
   *
   * @return the types mapping
   * @throws PolygonAPIException the polygon API exception
   */
  public TypesMapping getTypesMapping() throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "reference/types");

    builder.setVersion("v2");


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    TypesMapping typesMapping = polygonRequest.getResponseObject(response, TypesMapping.class);

    return typesMapping;
  }

  public List<Split> getSplits(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl, "reference/splits");

    builder.setVersion("v2");

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
