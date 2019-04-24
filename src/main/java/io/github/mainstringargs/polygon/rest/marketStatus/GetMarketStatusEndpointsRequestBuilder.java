package io.github.mainstringargs.polygon.rest.marketStatus;


/**
 * The Class GetMarketStatusEndpointsRequestBuilder.
 */
public class GetMarketStatusEndpointsRequestBuilder extends MarketStatusRequestBuilder {


  /**
   * Instantiates a new gets the market status endpoints request builder.
   *
   * @param baseDataUrl the base data url
   */
  public GetMarketStatusEndpointsRequestBuilder(String baseDataUrl) {
    super(baseDataUrl);
  }


  /**
   * Market status endpoint.
   *
   * @param endpoint the endpoint
   */
  public void marketStatusEndpoint(String endpoint) {
    super.appendEndpoint(endpoint);

  }


}
