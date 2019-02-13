package io.github.mainstringargs.alpaca.rest.orders;

/**
 * The Class GetOrderRequestBuilder.
 */
public class GetOrderRequestBuilder extends OrdersRequestBuilder {

  /**
   * Instantiates a new gets the order request builder.
   *
   * @param baseUrl the base url
   */
  public GetOrderRequestBuilder(String baseUrl) {
    super(baseUrl);
  }

  /**
   * Order id.
   *
   * @param orderId the order id
   * @return the orders url builder
   */
  public GetOrderRequestBuilder orderId(String orderId) {
    if (orderId != null) {
      super.appendEndpoint(orderId);
    }

    return this;

  }
}
