package io.github.mainstringargs.polygon.rest.reference.markets;

import io.github.mainstringargs.polygon.rest.reference.ReferenceRequestBuilder;

/**
 * The Class GetMarketsRequestBuilder.
 */
public class GetMarketsRequestBuilder extends ReferenceRequestBuilder {

  /** The Constant MARKETS_REQUEST_ENDPOINT. */
  public final static String MARKETS_REQUEST_ENDPOINT = "reference/markets";



  /**
   * Instantiates a new gets the markets request builder.
   *
   * @param baseUrl the base url
   */
  public GetMarketsRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.polygon.rest.reference.ReferenceRequestBuilder#getEndpoint()
   */
  @Override
  public String getEndpoint() {
    return MARKETS_REQUEST_ENDPOINT;
  }


}
