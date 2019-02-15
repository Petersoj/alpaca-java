package io.github.mainstringargs.alpaca.rest.orders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * The Class GetOrderByClientIdRequestBuilder.
 */
public class GetOrderByClientIdRequestBuilder extends OrdersRequestBuilder {

  /**
   * Instantiates a new gets the order by client id request builder.
   *
   * @param baseUrl the base url
   */
  public GetOrderByClientIdRequestBuilder(String baseUrl) {
    super(baseUrl);
  }

  /**
   * Orders by client order id.
   *
   * @param clientOrderId the client order id
   * @return the gets the order by client id request builder
   */
  public GetOrderByClientIdRequestBuilder ordersByClientOrderId(String clientOrderId) {
    if (clientOrderId != null) {
      super.setDefaultEndpoint(false);
      super.appendEndpoint("orders:by_client_order_id");

      try {
        clientOrderId = URLEncoder.encode(clientOrderId, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }

      super.appendURLParameter("client_order_id", clientOrderId);
    }
    return this;

  }
}
