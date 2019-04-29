package io.github.mainstringargs.polygon.rest.meta.exchanges;

import io.github.mainstringargs.polygon.rest.PolygonRequestBuilder;


/**
 * The Class GetExchangesRequestBuilder.
 */
public class GetExchangesRequestBuilder extends PolygonRequestBuilder {


  /** The Constant EXCHANGES_ENDPOINT. */
  public final static String EXCHANGES_ENDPOINT = "meta/exchanges";


  /**
   * Instantiates a new gets the exchanges request builder.
   *
   * @param baseUrl the base url
   */
  public GetExchangesRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  /* (non-Javadoc)
   * @see io.github.mainstringargs.polygon.rest.PolygonRequestBuilder#getEndpoint()
   */
  @Override
  public String getEndpoint() {
    return EXCHANGES_ENDPOINT;
  }

}
