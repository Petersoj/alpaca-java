package io.github.mainstringargs.alpaca.rest.orders;

public class GetOrderRequestBuilder extends OrdersRequestBuilder {

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
