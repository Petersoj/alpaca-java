package io.github.mainstringargs.polygon.rest.marketStatus;

import io.github.mainstringargs.polygon.rest.PolygonRequestBuilder;

/**
 * The Class MarketStatusRequestBuilder.
 */
public abstract class MarketStatusRequestBuilder extends PolygonRequestBuilder {


  /** The Constant MARKET_STATUS_ENDPOINT. */
  public final static String MARKET_STATUS_ENDPOINT = "marketstatus";



  /**
   * Instantiates a new market status request builder.
   *
   * @param baseUrl the base url
   */
  public MarketStatusRequestBuilder(String baseUrl) {
    super(baseUrl);
    super.setVersion("v1");
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public String getEndpoint() {
    return MARKET_STATUS_ENDPOINT;
  }

}
