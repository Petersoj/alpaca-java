package io.github.mainstringargs.alpaca.rest.orders;

import io.github.mainstringargs.alpaca.rest.AlpacaRequestBuilder;

/**
 * The Class OrdersRequestBuilder.
 */
public abstract class OrdersRequestBuilder extends AlpacaRequestBuilder {

  /** The Constant ORDERS_ENDPOINT. */
  public final static String ORDERS_ENDPOINT = "orders";



  /**
   * Instantiates a new orders request builder.
   *
   * @param baseUrl the base url
   */
  public OrdersRequestBuilder(String baseUrl) {
    super(baseUrl);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public String getEndpoint() {
    return ORDERS_ENDPOINT;
  }

}
