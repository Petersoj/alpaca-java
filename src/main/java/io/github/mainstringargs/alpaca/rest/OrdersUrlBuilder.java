package io.github.mainstringargs.alpaca.rest;

/**
 * The Class OrdersUrlBuilder.
 */
public class OrdersUrlBuilder extends AlpacaUrlBuilder {

  /** The Constant ORDERS_ENDPOINT. */
  public final static String ORDERS_ENDPOINT = "orders";



  /**
   * Instantiates a new orders url builder.
   *
   * @param baseUrl the base url
   */
  public OrdersUrlBuilder(String baseUrl) {
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
