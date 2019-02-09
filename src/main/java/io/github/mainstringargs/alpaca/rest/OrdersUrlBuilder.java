package io.github.mainstringargs.alpaca.rest;

/**
 * The Class OrdersUrlBuilder.
 */
public class OrdersUrlBuilder  extends AlpacaUrlBuilder {

  /**
   * Instantiates a new orders url builder.
   *
   * @param baseUrl the base url
   */
  public OrdersUrlBuilder(String baseUrl) {
    super(baseUrl);
  }

  /* (non-Javadoc)
   * @see io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder#endpoint()
   */
  @Override
  public AlpacaUrlBuilder endpoint() {
    builder.append(ORDERS_ENDPOINT);
    return this;
  }

}
