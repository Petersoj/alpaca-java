package io.github.mainstringargs.alpaca.rest.orders;

/**
 * The Class DeleteOrderRequestBuilder.
 */
public class DeleteOrderRequestBuilder extends OrdersRequestBuilder {

  /**
   * Instantiates a new delete order request builder.
   *
   * @param baseUrl the base url
   */
  public DeleteOrderRequestBuilder(String baseUrl) {
    super(baseUrl);
  }

  /**
   * Order id.
   *
   * @param orderId the order id
   * @return the orders url builder
   */
  public DeleteOrderRequestBuilder orderId(String orderId) {
    if (orderId != null) {
      super.appendEndpoint(orderId);
    }

    return this;

  }

}
