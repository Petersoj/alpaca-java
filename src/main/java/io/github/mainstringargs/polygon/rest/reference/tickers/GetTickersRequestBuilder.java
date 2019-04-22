package io.github.mainstringargs.polygon.rest.reference.tickers;

import io.github.mainstringargs.polygon.rest.reference.ReferenceRequestBuilder;

public class GetTickersRequestBuilder extends ReferenceRequestBuilder {

  /** The Constant SYMBOLS_ENDPOINT. */
  public final static String TICKERS_REQUEST_ENDPOINT = "reference/tickers";



  public GetTickersRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  @Override
  public String getEndpoint() {
    return TICKERS_REQUEST_ENDPOINT;
  }
}
