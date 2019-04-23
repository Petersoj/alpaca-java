package io.github.mainstringargs.polygon.rest.reference.tickers;

import io.github.mainstringargs.polygon.enums.Locale;
import io.github.mainstringargs.polygon.enums.Market;
import io.github.mainstringargs.polygon.enums.Sort;
import io.github.mainstringargs.polygon.enums.Type;
import io.github.mainstringargs.polygon.rest.reference.ReferenceRequestBuilder;

/**
 * The Class GetTickersRequestBuilder.
 */
public class GetTickersRequestBuilder extends ReferenceRequestBuilder {

  /** The Constant SYMBOLS_ENDPOINT. */
  public final static String TICKERS_REQUEST_ENDPOINT = "reference/tickers";



  /**
   * Instantiates a new gets the tickers request builder.
   *
   * @param baseUrl the base url
   */
  public GetTickersRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.polygon.rest.reference.ReferenceRequestBuilder#getEndpoint()
   */
  @Override
  public String getEndpoint() {
    return TICKERS_REQUEST_ENDPOINT;
  }

  /**
   * Sort.
   *
   * @param sort the sort
   * @return the gets the tickers request builder
   */
  public GetTickersRequestBuilder sort(Sort sort) {
    if (sort != null) {
      super.appendURLParameter("sort", sort.getAPIName());
    }
    return this;
  }

  /**
   * Type.
   *
   * @param type the type
   * @return the gets the tickers request builder
   */
  public GetTickersRequestBuilder type(Type type) {
    if (type != null) {
      super.appendURLParameter("type", type.getAPIName());
    }
    return this;
  }

  /**
   * Type.
   *
   * @param market the market
   * @return the gets the tickers request builder
   */
  public GetTickersRequestBuilder type(Market market) {
    if (market != null) {
      super.appendURLParameter("market", market.getAPIName());
    }
    return this;
  }

  /**
   * Type.
   *
   * @param locale the locale
   * @return the gets the tickers request builder
   */
  public GetTickersRequestBuilder type(Locale locale) {
    if (locale != null) {
      super.appendURLParameter("locale", locale.getAPIName());
    }
    return this;
  }

  /**
   * Type.
   *
   * @param search the search
   * @return the gets the tickers request builder
   */
  public GetTickersRequestBuilder type(String search) {
    if (search != null) {
      super.appendURLParameter("search", search);
    }
    return this;
  }

  /**
   * Perpage.
   *
   * @param perpage the perpage
   * @return the gets the tickers request builder
   */
  public GetTickersRequestBuilder perpage(int perpage) {
    super.appendURLParameter("perpage", perpage + "");

    return this;
  }


  /**
   * Page.
   *
   * @param page the page
   * @return the gets the tickers request builder
   */
  public GetTickersRequestBuilder page(int page) {
    super.appendURLParameter("page", page + "");

    return this;
  }

  /**
   * Active.
   *
   * @param active the active
   * @return the gets the tickers request builder
   */
  public GetTickersRequestBuilder active(boolean active) {
    super.appendURLParameter("active", active + "");

    return this;
  }
}
