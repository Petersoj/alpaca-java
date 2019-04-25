package io.github.mainstringargs.polygon.rest.meta.exchanges;

import io.github.mainstringargs.polygon.rest.PolygonRequestBuilder;


public class GetExchangesRequestBuilder extends PolygonRequestBuilder {


  public final static String EXCHANGES_ENDPOINT = "meta/exchanges";


  public GetExchangesRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  @Override
  public String getEndpoint() {
    return EXCHANGES_ENDPOINT;
  }

}
