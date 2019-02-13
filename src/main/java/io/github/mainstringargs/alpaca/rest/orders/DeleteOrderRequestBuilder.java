package io.github.mainstringargs.alpaca.rest.orders;

public class DeleteOrderRequestBuilder extends OrdersRequestBuilder {

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
